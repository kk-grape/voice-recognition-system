package com.music.asr.service;

import com.music.asr.domain.AsrSession;

public interface AsrSessionService {

    /**
     * 创建一条新的会话记录，返回会话ID。
     *
     * 【说明】
     * - 会员登录 / token 校验已经在网关完成，这里不再做 JWT 解析
     * - 通过网关透传的会员信息（X-Member-Id / X-Member-Name）建立会话与会员之间的关联
     */
    Long createSession(String wsSessionId, Long memberId, String memberName);

    /**
     * 更新会话的语言等配置。
     */
    void updateConfig(Long sessionId, String sourceLang, String targetLang, Integer sampleRate, String format);

    /**
     * 将会话标记为失败。
     */
    void markFailed(Long sessionId, String reason);

    /**
     * 结束会话，补充时长等信息。
     */
    void finishSession(Long sessionId);

    /**
     * 获取当前可用的阿里云 NLS token。
     * 实现里可以对接你的业务配置中心，或自己封装一层缓存。
     */
    String getAliyunToken();

    /**
     * 根据会话ID获取会话信息
     *
     * @param sessionId 会话ID
     * @return 会话对象
     */
    AsrSession getById(Long sessionId);
}

