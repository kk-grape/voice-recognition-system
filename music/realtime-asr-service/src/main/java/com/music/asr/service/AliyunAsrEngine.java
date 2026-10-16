package com.music.asr.service;

import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;

/**
 * 封装与阿里云实时语音识别（NLS）的交互。
 *
 * <p>实际对接阿里云时，需要：
 * <ul>
 *     <li>根据 accessKeyId/accessKeySecret 获取 token；</li>
 *     <li>使用 JDK HttpClient 的 WebSocket 客户端连接阿里云 NLS 网关；</li>
 *     <li>按阿里云文档发送 StartTranscription / StopTranscription 指令；</li>
 *     <li>将前端推送过来的 PCM16 音频帧转发给阿里云；</li>
 *     <li>解析阿里云返回的中间结果/最终结果，保存到数据库并通过 WebSocket 推送给前端。</li>
 * </ul>
 */
public interface AliyunAsrEngine {

    /**
     * 前端发起 start 配置后，调用此方法建立到阿里云的实时识别通道。
     */
    void startStream(Long bizSessionId,
                     String sourceLang,
                     String targetLang,
                     Integer sampleRate,
                     String format,
                     WebSocketSession frontendSession);

    /**
     * 前端推送音频帧时调用。
     */
    void pushAudio(Long bizSessionId, ByteBuffer audioData);

    /**
     * 前端或后端主动结束会话时调用。
     */
    void stopStream(Long bizSessionId);

    /**
     * WebSocket 出现异常时调用。
     */
    void onError(Long bizSessionId, Throwable t);

    /**
     * WebSocket 连接关闭时调用，确保资源释放。
     */
    void closeStream(Long bizSessionId);
}

