package com.music.asr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import com.music.asr.enums.AsrLangEnum;
import com.music.asr.enums.TranslateLangEnum;
import com.music.asr.nls.NlsProperties;
import com.music.asr.nls.RealtimeAsrProperties;
import com.music.asr.service.AliyunAsrEngine;
import com.music.asr.service.AsrSessionService;
import com.music.asr.service.AsrSubtitleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 阿里云 NLS 实时语音识别引擎实现。
 *
 * 【核心类说明】
 * - NlsClient：语音处理客户端，线程安全，建议全局只创建一个实例
 * - SpeechTranscriber：实时识别类，非线程安全，每个识别任务创建一个实例
 * - SpeechTranscriberListener：识别结果监听器
 *
 * 【使用流程】
 * 1. 在 afterConnectionEstablished 中调用 startStream() 创建 SpeechTranscriber
 * 2. 在 onaudioprocess 中调用 transcriber.send() 发送音频数据
 * 3. 在收到 onSentenceEnd 时通过 WebSocket 把结果推给前端
 * 4. 在连接关闭时调用 transcriber.stop() 停止识别
 *
 * 参考官方文档：https://help.aliyun.com/zh/isi/developer-reference/sdk-for-java-8
 */
@Slf4j
@Service
public class AliyunAsrEngineImpl implements AliyunAsrEngine {

    /**
     * NlsClient 是线程安全的，整个应用生命周期内只创建一次
     * 由 @PostConstruct 初始化，@PreDestroy 销毁
     */
    private NlsClient nlsClient;

    @Resource
    private NlsProperties nlsProperties;

    @Resource
    private RealtimeAsrProperties realtimeAsrProperties;

    @Resource
    private AsrSessionService asrSessionService;

    @Resource
    private AsrSubtitleService asrSubtitleService;

    /**
     * 业务会话ID -> SpeechTranscriber 实例
     * 每个识别任务对应一个 SpeechTranscriber 实例
     */
    private final Map<Long, SpeechTranscriber> transcriberMap = new ConcurrentHashMap<>();

    /**
     * 业务会话ID -> 前端 WebSocket Session
     * 用于把识别结果推给前端
     */
    private final Map<Long, WebSocketSession> frontendSessionMap = new ConcurrentHashMap<>();

    /**
     * 业务会话ID -> 翻译语言枚举（用于翻译）
     */
    private final Map<Long, TranslateLangEnum> translateLangEnumMap = new ConcurrentHashMap<>();

    /**
     * 业务会话ID -> 句子序号（用于字幕顺序）
     */
    private final Map<Long, Integer> sentenceIndexMap = new ConcurrentHashMap<>();

    /**
     * 业务会话ID -> 句子开始时间Map（sentenceIndex -> beginTimeMs）
     * 用于在 onSentenceEnd 时获取对应句子的开始时间
     */
    private final Map<Long, Map<Integer, Long>> sentenceBeginTimeMap = new ConcurrentHashMap<>();

    /**
     * 业务会话ID -> 会话开始时间（毫秒）
     * 用于计算字幕的相对时间（SRT格式需要从0开始的时间）
     */
    private final Map<Long, Long> sessionStartTimeMap = new ConcurrentHashMap<>();

    /**
     * 已经进入停止流程的会话：用于避免 stop 之后又被 onFail/onError 覆盖成 failed
     */
    private final Set<Long> stoppingSessions = ConcurrentHashMap.newKeySet();

    /**
     * 应用启动时初始化 NlsClient
     */
    @PostConstruct
    public void init() {
        try {
            // 获取 token（实际使用时应注意 token 过期时间，提前刷新）
            AccessToken accessToken = new AccessToken(
                    nlsProperties.getAccessKeyId(),
                    nlsProperties.getAccessKeySecret()
            );
            accessToken.apply();
            log.info("获取阿里云 NLS Token 成功，过期时间: {}", accessToken.getExpireTime());

            // 创建 NlsClient 实例
            // 使用默认外网服务地址：wss://nls-gateway-cn-shanghai.aliyuncs.com/ws/v1
            nlsClient = new NlsClient(accessToken.getToken());
            log.info("NlsClient 初始化成功");
        } catch (IOException e) {
            log.error("NlsClient 初始化失败", e);
            throw new RuntimeException("NlsClient 初始化失败", e);
        }
    }

    /**
     * 应用关闭时销毁 NlsClient
     */
    @PreDestroy
    public void shutdown() {
        if (nlsClient != null) {
            nlsClient.shutdown();
            log.info("NlsClient 已关闭");
        }
    }

    /**
     * 刷新 Token（当 token 即将过期时调用）
     */
    public void refreshToken() {
        try {
            AccessToken accessToken = new AccessToken(
                    nlsProperties.getAccessKeyId(),
                    nlsProperties.getAccessKeySecret()
            );
            accessToken.apply();
            log.info("刷新 NLS Token 成功，过期时间: {}", accessToken.getExpireTime());
        } catch (IOException e) {
            log.error("刷新 NLS Token 失败", e);
        }
    }

    /**
     * 前端发起 start 配置后，调用此方法建立到阿里云的实时识别通道。
     */
    @Override
    public void startStream(Long bizSessionId,
                            String sourceLang,
                            String targetLang,
                            Integer sampleRate,
                            String format,
                            WebSocketSession frontendSession) {
        try {
            // 1. 根据 sourceLang 获取阿里云识别语言枚举（包含 AppKey）
            AsrLangEnum asrLangEnum = AsrLangEnum.getByCode(sourceLang);
            if (asrLangEnum == null) {
                log.warn("未知的语言代码: {}，使用默认普通话", sourceLang);
                asrLangEnum = AsrLangEnum.LANG0;
            }

            // 2. 根据 targetLang 获取翻译语言枚举
            TranslateLangEnum translateLangEnum = TranslateLangEnum.getByCode(targetLang);
            if (translateLangEnum == null) {
                log.warn("未知的翻译语言代码: {}，使用默认中文", targetLang);
                translateLangEnum = TranslateLangEnum.LANG0;
            }

            log.info("使用语言 - 识别: {} (AppKey={}), 翻译: {}",
                    asrLangEnum.getDesc(), asrLangEnum.getAppKey(),
                    translateLangEnum.getDesc());

            // 保存语言枚举用于后续翻译
            translateLangEnumMap.put(bizSessionId, translateLangEnum);
            // 初始化句子序号
            sentenceIndexMap.put(bizSessionId, 1);
            stoppingSessions.remove(bizSessionId);

            // 3. 创建 SpeechTranscriberListener（处理识别结果）
            SpeechTranscriberListener listener = createListener(bizSessionId);

            // 4. 创建 SpeechTranscriber 实例
            SpeechTranscriber transcriber = new SpeechTranscriber(nlsClient, listener);

            // 5. 设置识别参数
            transcriber.setAppKey(asrLangEnum.getAppKey());

            // 音频格式（PCM）
            transcriber.setFormat(InputFormatEnum.PCM);

            // 采样率（默认 16000）
            if (sampleRate != null && sampleRate == 8000) {
                transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_8K);
            } else {
                transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            }

            // 中间结果（是否返回实时识别过程）
            transcriber.setEnableIntermediateResult(
                    realtimeAsrProperties.getEnableIntermediateResult() != null
                            ? realtimeAsrProperties.getEnableIntermediateResult()
                            : false
            );

            // 标点符号
            transcriber.setEnablePunctuation(
                    realtimeAsrProperties.getEnablePunctuation() != null
                            ? realtimeAsrProperties.getEnablePunctuation()
                            : true
            );

            // 数字规整化（ITN）
            transcriber.setEnableITN(
                    realtimeAsrProperties.getEnableITN() != null
                            ? realtimeAsrProperties.getEnableITN()
                            : false
            );

            // VAD 断句参数
            if (realtimeAsrProperties.getMaxSentenceSilence() != null) {
                transcriber.addCustomedParam("max_sentence_silence",
                        realtimeAsrProperties.getMaxSentenceSilence());
            }

            // 保存 transcriber 和前端 session 的映射
            transcriberMap.put(bizSessionId, transcriber);
            frontendSessionMap.put(bizSessionId, frontendSession);

            // 5. 启动识别
            transcriber.start();

            // 6. 记录会话开始时间（用于计算字幕相对时间）
            sessionStartTimeMap.put(bizSessionId, System.currentTimeMillis());

            log.info("启动实时语音识别成功，bizSessionId={}，language={}，sampleRate={}",
                    bizSessionId, asrLangEnum.getDesc(), sampleRate);

        } catch (Exception e) {
            log.error("启动实时语音识别失败，bizSessionId={}", bizSessionId, e);
            onError(bizSessionId, e);
        }
    }

    /**
     * 前端推送音频帧时调用。
     * 前端每采集一段时间（如 100ms）的音频，就调用一次 send()
     */
    @Override
    public void pushAudio(Long bizSessionId, ByteBuffer audioData) {
        SpeechTranscriber transcriber = transcriberMap.get(bizSessionId);
        if (transcriber == null) {
            log.warn("pushAudio 时找不到 transcriber，bizSessionId={}", bizSessionId);
            return;
        }

        try {
            // 将 ByteBuffer 转换为 byte[]
            byte[] data = new byte[audioData.remaining()];
            audioData.get(data);

            // 发送音频数据
            transcriber.send(data);

        } catch (Exception e) {
            log.error("发送音频数据失败，bizSessionId={}", bizSessionId, e);
            onError(bizSessionId, e);
        }
    }

    /**
     * 停止识别
     * 当前端发送 type=stop 时调用
     */
    @Override
    public void stopStream(Long bizSessionId) {
        stoppingSessions.add(bizSessionId);
        SpeechTranscriber transcriber = transcriberMap.remove(bizSessionId);
        if (transcriber != null) {
            try {
                transcriber.stop();
                log.info("停止实时语音识别成功，bizSessionId={}", bizSessionId);
                // stop 后显式 close，尽快释放资源，避免阿里云端 idle timeout 继续回调
                transcriber.close();
            } catch (Exception e) {
                log.error("停止实时语音识别失败，bizSessionId={}", bizSessionId, e);
            }
        }
        frontendSessionMap.remove(bizSessionId);
        translateLangEnumMap.remove(bizSessionId);
        sentenceIndexMap.remove(bizSessionId);
        sentenceBeginTimeMap.remove(bizSessionId);
    }

    /**
     * 处理错误
     */
    @Override
    public void onError(Long bizSessionId, Throwable t) {
        if (stoppingSessions.contains(bizSessionId)) {
            log.warn("会话已停止，忽略错误回调，bizSessionId={}，msg={}", bizSessionId, String.valueOf(t.getMessage()));
            return;
        }
        // 标记会话失败
        asrSessionService.markFailed(bizSessionId, t.getMessage());

        // 向前端发送错误消息
        WebSocketSession frontendSession = frontendSessionMap.get(bizSessionId);
        if (frontendSession != null && frontendSession.isOpen()) {
            try {
                frontendSession.sendMessage(new TextMessage(JSON.toJSONString(
                        Map.of("type", "error", "message", String.valueOf(t.getMessage()))
                )));
            } catch (Exception e) {
                log.warn("向前端发送错误消息失败，bizSessionId={}", bizSessionId, e);
            }
        }

        // 清理资源
        transcriberMap.remove(bizSessionId);
        frontendSessionMap.remove(bizSessionId);
        translateLangEnumMap.remove(bizSessionId);
        sentenceIndexMap.remove(bizSessionId);
        sentenceBeginTimeMap.remove(bizSessionId);
    }

    /**
     * 关闭流（连接断开时调用）
     */
    @Override
    public void closeStream(Long bizSessionId) {
        SpeechTranscriber transcriber = transcriberMap.remove(bizSessionId);
        if (transcriber != null) {
            try {
                transcriber.close();
            } catch (Exception e) {
                log.debug("关闭 transcriber 异常，bizSessionId={}", bizSessionId, e);
            }
        }
        frontendSessionMap.remove(bizSessionId);
        translateLangEnumMap.remove(bizSessionId);
        sentenceIndexMap.remove(bizSessionId);
        sentenceBeginTimeMap.remove(bizSessionId);
        sessionStartTimeMap.remove(bizSessionId);
        stoppingSessions.remove(bizSessionId);
    }

    /**
     * 创建 SpeechTranscriberListener，处理识别结果
     */
    private SpeechTranscriberListener createListener(Long bizSessionId) {
        return new SpeechTranscriberListener() {

            // 识别开始
            @Override
            public void onTranscriberStart(SpeechTranscriberResponse response) {
                log.info("识别开始，taskId={}，bizSessionId={}",
                        response.getTaskId(), bizSessionId);
            }

            // 句子开始
            @Override
            public void onSentenceBegin(SpeechTranscriberResponse response) {
                int index = response.getTransSentenceIndex();
                log.debug("句子开始，taskId={}，sentenceIndex={}，bizSessionId={}",
                        response.getTaskId(), index, bizSessionId);

                // 记录句子开始时间
                sentenceBeginTimeMap
                        .computeIfAbsent(bizSessionId, k -> new ConcurrentHashMap<>())
                        .put(index, System.currentTimeMillis());
            }

            // 识别出中间结果（仅当 enableIntermediateResult=true 时返回）
            @Override
            public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
                log.debug("中间结果，taskId={}，text={}，bizSessionId={}",
                        response.getTaskId(), response.getTransSentenceText(), bizSessionId);

                // 可以选择是否推送中间结果给前端（实时识别过程）
                //sendToFrontend(bizSessionId, "interim", response.getTransSentenceText(), null);
            }

            // 识别出一句话（句子结束）
            @Override
            public void onSentenceEnd(SpeechTranscriberResponse response) {
                log.info("【识别一句话成功】，taskId={}，text={}，confidence={}，bizSessionId={}",
                        response.getTaskId(), response.getTransSentenceText(),
                        response.getConfidence(), bizSessionId);

                String text = response.getTransSentenceText();
                if (text != null && !text.isEmpty()) {
                    // 获取当前句子的序号
                    Integer sentenceIndex = sentenceIndexMap.get(bizSessionId);
                    if (sentenceIndex == null) {
                        sentenceIndex = 1;
                    }
                    sentenceIndexMap.put(bizSessionId, sentenceIndex + 1);

                    // 获取翻译语言枚举
                    TranslateLangEnum translateLangEnum = translateLangEnumMap.get(bizSessionId);

                    // 获取时间信息（相对时间，用于SRT字幕）
                    long sessionStartTime = sessionStartTimeMap.getOrDefault(bizSessionId, System.currentTimeMillis());
                    long currentTime = System.currentTimeMillis();
                    int currentIndex = sentenceIndex;
                    long beginTime = 0;
                    long endTime = currentTime;

                    // 尝试从 map 中获取句子的开始时间
                    Map<Integer, Long> beginTimeMap = sentenceBeginTimeMap.get(bizSessionId);
                    if (beginTimeMap != null) {
                        Long recordedBegin = beginTimeMap.remove(currentIndex);
                        if (recordedBegin != null) {
                            beginTime = recordedBegin;
                        }
                    }

                    // 如果没有记录到开始时间，根据序号估算（每句话约3秒）
                    if (beginTime == 0 && currentIndex > 1) {
                        beginTime = endTime - 3000;
                    }

                    // 计算相对时间（相对于会话开始）
                    int relativeBeginTimeMs = (int) Math.max(0, beginTime - sessionStartTime);
                    int relativeEndTimeMs = (int) Math.max(0, endTime - sessionStartTime);

                    // 调用 asrSubtitleService 处理识别结果（包含翻译）
                    String resultText = asrSubtitleService.handleAliyunResult(
                            bizSessionId,
                            "{\"payload\":{\"result\":{\"text\":\"" + text + "\"}}}",
                            translateLangEnum,
                            sentenceIndex,
                            relativeBeginTimeMs,
                            relativeEndTimeMs
                    );

                    // 推给前端（包含原文和翻译）
                    sendToFrontend(bizSessionId, "result", text, resultText);
                }
            }

            // 识别完成
            @Override
            public void onTranscriptionComplete(SpeechTranscriberResponse response) {
                log.info("识别完成，taskId={}，bizSessionId={}",
                        response.getTaskId(), bizSessionId);

                // 结束状态交给 WebSocket handler（stop/close）统一处理，避免重复 finish
            }

            // 识别失败
            @Override
            public void onFail(SpeechTranscriberResponse response) {
                log.error("识别失败，taskId={}，status={}，statusText={}，bizSessionId={}",
                        response.getTaskId(), response.getStatus(),
                        response.getStatusText(), bizSessionId);

                if (stoppingSessions.contains(bizSessionId)) {
                    log.warn("会话已停止，忽略 onFail，bizSessionId={}，statusText={}", bizSessionId, response.getStatusText());
                    return;
                }
                onError(bizSessionId, new RuntimeException(response.getStatusText()));
            }
        };
    }

    /**
     * 发送消息给前端
     * 包含原文和翻译结果
     */
    private void sendToFrontend(Long bizSessionId, String type, String text, String translatedText) {
        WebSocketSession session = frontendSessionMap.get(bizSessionId);
        if (session != null && session.isOpen()) {
            try {
                // 构建返回数据
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("type", type);
                result.put("text", text != null ? text : "");

                // 如果有翻译结果，也一并返回
                if (translatedText != null) {
                    result.put("translatedText", translatedText);
                }

                session.sendMessage(new TextMessage(JSON.toJSONString(result)));
            } catch (Exception e) {
                log.warn("发送消息给前端失败，bizSessionId={}", bizSessionId, e);
            }
        }
    }
}
