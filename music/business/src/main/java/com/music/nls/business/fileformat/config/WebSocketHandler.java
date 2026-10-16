package com.music.nls.business.fileformat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    // 存储所有活动的WebSocket会话
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    // 存储任务ID到会话ID的映射
    private static final Map<String, String> TASK_SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立时添加会话
        String sessionId = session.getId();
        SESSIONS.put(sessionId, session);
        log.info("WebSocket连接建立: {}", sessionId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端消息
        String payload = message.getPayload();
        log.info("收到WebSocket消息: {}", payload);

        try {
            // 解析客户端消息
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(payload);
            String type = jsonObject.getString("type");

            if ("connect".equals(type)) {
                // 处理连接信息，生成任务ID并保存用户信息和转换信息
                String userId = jsonObject.getJSONObject("userInfo").getString("userId");
                String userName = jsonObject.getJSONObject("userInfo").getString("userName");
                String userEmail = jsonObject.getJSONObject("userInfo").getString("userEmail");

                // 生成任务ID
                String taskId = "task_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
                log.info("生成任务ID: {}", taskId);

                // 保存用户信息和转换信息到数据库（这里简化处理，实际项目中应该调用数据库服务）
                // TODO: 保存用户信息和转换信息到数据库

                // 绑定任务ID和会话ID
                String sessionId = session.getId();
                bindTaskSession(taskId, sessionId);

                // 返回任务ID给前端
                com.alibaba.fastjson.JSONObject response = new com.alibaba.fastjson.JSONObject();
                response.put("type", "taskId");
                response.put("taskId", taskId);
                session.sendMessage(new TextMessage(response.toJSONString()));
                log.info("返回任务ID给前端: {}", taskId);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", e.getMessage());
            // 返回错误消息给前端
            com.alibaba.fastjson.JSONObject errorResponse = new com.alibaba.fastjson.JSONObject();
            errorResponse.put("type", "error");
            errorResponse.put("message", "处理连接信息失败: " + e.getMessage());
            session.sendMessage(new TextMessage(errorResponse.toJSONString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭时移除会话
        String sessionId = session.getId();
        SESSIONS.remove(sessionId);

        // 查找该会话对应的任务ID
        String taskId = null;
        for (Map.Entry<String, String> entry : TASK_SESSION_MAP.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                taskId = entry.getKey();
                break;
            }
        }

        // 移除相关的任务映射
        TASK_SESSION_MAP.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
        log.info("WebSocket连接关闭: {}, 任务ID: {}", sessionId, taskId);

        // 如果找到任务ID，在关闭时再次返回给前端
        if (taskId != null) {
            try {
                // 构建关闭原因，包含任务ID
                com.alibaba.fastjson.JSONObject closeReason = new com.alibaba.fastjson.JSONObject();
                closeReason.put("type", "taskId");
                closeReason.put("taskId", taskId);

                // 注意：WebSocket的close方法的reason参数有长度限制（123字节）
                // 实际项目中可能需要使用其他方式传递任务ID
                log.info("连接关闭时返回任务ID: {}", taskId);
            } catch (Exception e) {
                log.error("连接关闭时返回任务ID失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 广播消息给所有客户端
     * 
     * @param message 消息内容
     */
    public static void broadcast(String message) {
        for (WebSocketSession session : SESSIONS.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("发送WebSocket消息失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 发送消息给指定客户端
     * 
     * @param sessionId 会话ID
     * @param message   消息内容
     */
    public static void sendToSession(String sessionId, String message) {
        WebSocketSession session = SESSIONS.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送WebSocket消息失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送消息给指定任务的客户端
     * 
     * @param taskId  任务ID
     * @param message 消息内容
     */
    public static void sendToTask(String taskId, String message) {
        String sessionId = TASK_SESSION_MAP.get(taskId);
        if (sessionId != null) {
            sendToSession(sessionId, message);
        } else {
            // 如果找不到任务对应的会话，就广播消息
            broadcast(message);
        }
    }

    /**
     * 绑定任务ID和会话ID
     * 
     * @param taskId    任务ID
     * @param sessionId 会话ID
     */
    public static void bindTaskSession(String taskId, String sessionId) {
        TASK_SESSION_MAP.put(taskId, sessionId);
        log.info("绑定任务ID和会话ID: {} -> {}", taskId, sessionId);
    }

    /**
     * 获取当前活动会话数
     * 
     * @return 会话数
     */
    public static int getActiveSessionCount() {
        return SESSIONS.size();
    }

    /**
     * 清理任务映射
     * 
     * @param taskId 任务ID
     */
    public static void removeTaskMapping(String taskId) {
        TASK_SESSION_MAP.remove(taskId);
        log.info("移除任务映射: {}", taskId);
    }

    /**
     * 获取指定任务ID对应的会话ID
     * 
     * @param taskId 任务ID
     * @return 会话ID
     */
    public static String getSessionIdByTaskId(String taskId) {
        return TASK_SESSION_MAP.get(taskId);
    }

    /**
     * 获取所有任务映射
     * 
     * @return 任务映射
     */
    public static Map<String, String> getAllTaskMappings() {
        return TASK_SESSION_MAP;
    }
}
