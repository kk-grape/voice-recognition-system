package com.music.asr.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.music.asr.service.AliyunAsrEngine;
import com.music.asr.service.AsrSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 入口处理类：负责和浏览器进行双向通信。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsrWebSocketHandler extends BinaryWebSocketHandler {

    private final AliyunAsrEngine aliyunAsrEngine;
    private final AsrSessionService asrSessionService;

    /**
     * key: Spring WebSocket 的 sessionId
     * value: 我们自己生成的业务会话ID（数据库 asr_session.id）
     */
    private final Map<String, Long> wsSessionToBizSession = new ConcurrentHashMap<>();

    /**
     * 当浏览器第一次成功建立 WebSocket 连接时会回调到这里
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 典型时机：前端执行 new WebSocket(\"ws://网关域名/web/ws/asr\")，握手成功后触发
        //
        // 在这里我们要做几件事情：
        // 1）从握手请求头中取出网关透传的会员信息（X-Member-Id / X-Member-Name）
        // 2）调用 AsrSessionService#createSession 在数据库 asr_session 表里插入一条“会话记录”
        // 3）把 Spring WebSocketSession 的 id 与 业务会话 id 映射到 wsSessionToBizSession 这个 Map 里缓存起来
        // 4）给前端发一条 {"type":"started"} 的通知，告诉它“后台已经准备好，可以发送 start 指令和音频了”
        log.info("WebSocket 连接建立，id={}", session.getId());

        // 网关在 WebSocketAuthFilter 中已经完成 token 校验，并把会员信息写入了请求头：
        String memberIdHeader = session.getHandshakeHeaders().getFirst("X-Member-Id");
        String memberName = session.getHandshakeHeaders().getFirst("X-Member-Name");
        Long memberId = null;
        try {
            if (memberIdHeader != null && !memberIdHeader.isEmpty()) {
                memberId = Long.parseLong(memberIdHeader);
            }
        } catch (NumberFormatException e) {
            log.warn("解析 X-Member-Id 失败，value={}", memberIdHeader, e);
        }
        Long bizSessionId = asrSessionService.createSession(session.getId(), memberId, memberName);
        wsSessionToBizSession.put(session.getId(), bizSessionId);

        // 告知前端连接已就绪，并传递会话ID（转为字符串避免 JavaScript 大数精度丢失）
        sendJson(session, Map.of("type", "started", "sessionId", String.valueOf(bizSessionId)));
    }

    /**
     * 当浏览器发送文本消息时会回调到这里
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 1）type=start：携带本次会话的配置（语言、采样率、音频格式等），通知后端“我要开始识别了”
        // 2）type=stop：通知后端“这次识别已经结束，可以收尾关闭连接了”

        String payload = message.getPayload();
        log.debug("接收到文本消息：{}", payload);
        JSONObject json;
        try {
            json = JSON.parseObject(payload);
        } catch (Exception e) {
            log.warn("文本消息解析失败：{}", payload, e);
            sendJson(session, Map.of("type", "error", "message", "无效的JSON消息"));
            return;
        }

        // 前端通过字段 type 区分不同指令：start / stop / 其它
        String type = json.getString("type");
        if ("start".equalsIgnoreCase(type)) {
            handleStart(session, json);
        } else if ("stop".equalsIgnoreCase(type)) {
            handleStop(session);
        } else {
            log.warn("未知的文本消息类型: {}", type);
        }
    }

    /**
     * 当浏览器发送二进制消息时会回调到这里
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // 【当前端通过 WebSocket 发送二进制数据（音频流）时会回调到这里】
        //
        // 典型流程：
        // 1）前端在点击“开始说话”之后，会不断把麦克风采集的音频帧以二进制形式发送过来
        // 2）这里先根据 WebSocketSession 的 id 找到前面 createSession 时保存的“业务会话 id”
        // 3）如果找不到，说明还没正确初始化（比如没发 start 就直接发音频），直接返回错误给前端
        // 4）如果能找到，说明会话是合法的，就把音频帧透传给底层的语音引擎（AliyunAsrEngineImpl）
        Long bizSessionId = wsSessionToBizSession.get(session.getId());
        if (bizSessionId == null) {
            log.warn("收到音频数据但业务会话不存在，sessionId={}", session.getId());
            sendJson(session, Map.of("type", "error", "message", "会话未初始化"));
            return;
        }
        // WebSocket 的二进制消息就是前端编码好的音频数据（一般是 16k 采样率 PCM 裸流），
        // 这里不做任何修改，直接透传给底层的语音引擎（AliyunAsrEngineImpl）去发送到阿里云。
        aliyunAsrEngine.pushAudio(bizSessionId, message.getPayload());
    }

    /**
     * 当 WebSocket 传输过程中发生异常时会回调到这里
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        log.error("WebSocket 传输异常，id={}", session.getId(), exception);
        Long bizSessionId = wsSessionToBizSession.get(session.getId());
        if (bizSessionId != null) {
            // 只关闭流，不标记失败。让 afterConnectionClosed 统一收尾（标记为 completed）
            // 这样用户突然断开/网络抖动不会导致会话变成"失败"状态
            aliyunAsrEngine.closeStream(bizSessionId);
        }
        super.handleTransportError(session, exception);
    }

    /**
     * 当 WebSocket 连接正常关闭或异常断开时会回调到这里
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 典型触发场景：
        // - 前端主动调用 ws.close()
        // - 浏览器刷新 / 关闭页面
        // - 网络异常导致连接中断
        log.info("WebSocket 连接关闭，id={}，status={}", session.getId(), status);
        Long bizSessionId = wsSessionToBizSession.remove(session.getId());
        if (bizSessionId != null) {
            aliyunAsrEngine.closeStream(bizSessionId);
            asrSessionService.finishSession(bizSessionId);
        }
    }

    /**
     * 当浏览器发送“type=start”时会回调到这里
     */
    private void handleStart(WebSocketSession session, JSONObject json) {

        // 主要职责：
        // 1）根据当前 WebSocketSession 的 id 找到对应的业务会话 id（在 afterConnectionEstablished 里已经创建）
        // 2）从前端传来的 JSON 中读取本次识别的配置（源语言、目标语言、采样率、音频格式）
        // 3）先调用 asrSessionService.updateConfig(...) 把这些配置写入数据库，方便之后做统计/回放
        // 4）再调用 aliyunAsrEngine.startStream(...) 真正打通“本服务 <-> 阿里云实时识别”的通道
        //
        // 注意：
        // - 如果当前 WebSocketSession 对应的业务会话 id 不存在，说明连接没有正确初始化，直接给前端返回错误
        // - 这里本身不做参数合法性校验（比如 sampleRate 是否为 16000），可以在前端或引擎层做更严格的校验
        Long bizSessionId = wsSessionToBizSession.get(session.getId());
        if (bizSessionId == null) {
            sendJson(session, Map.of("type", "error", "message", "会话未初始化"));
            return;
        }
        // 以下几个参数均由前端在 type=start 的消息中传入，可以直接映射到阿里云的配置：
        // - sourceLang：源语言，例如 zh-CN
        // - targetLang：目标语言，例如 en-US（如果不需要翻译，可以忽略或者和 sourceLang 一致）
        // - sampleRate：采样率，阿里云实时识别要求通常为 16000
        // - format：音频编码格式，例如 pcm
        String sourceLang = json.getString("sourceLang");
        String targetLang = json.getString("targetLang");
        Integer sampleRate = json.getInteger("sampleRate");
        String format = json.getString("format");

        // 先把本次会话的配置更新到数据库，方便后续统计/回看
        asrSessionService.updateConfig(bizSessionId, sourceLang, targetLang, sampleRate, format);
        // 再真正启动与阿里云的实时识别通道
        aliyunAsrEngine.startStream(bizSessionId, sourceLang, targetLang, sampleRate, format, session);
    }

    /**
     * 当浏览器发送“type=stop”时会回调到这里
     */
    private void handleStop(WebSocketSession session) {
        Long bizSessionId = wsSessionToBizSession.get(session.getId());
        if (bizSessionId != null) {
            aliyunAsrEngine.stopStream(bizSessionId);
        }
        try {
            session.close();
        } catch (IOException e) {
            log.warn("关闭 WebSocket 连接失败，id={}", session.getId(), e);
        }
    }

    /**
     * 给前端发送一条 JSON 文本消息
     */
    private void sendJson(WebSocketSession session, Map<String, Object> data) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(JSON.toJSONString(data)));
            }
        } catch (IOException e) {
            log.warn("发送 WebSocket 文本消息失败，id={}", session.getId(), e);
        }
    }

}

