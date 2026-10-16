package com.music.asr.service;

import com.music.asr.enums.SubtitleTypeEnum;

/**
 * 语音合成（TTS）服务接口
 */
public interface AliyunTtsService {

    /**
     * 合成会话音频
     *
     * @param sessionId     会话ID
     * @param subtitleTypeEnum  字幕类型
     * @return 合成后的音频文件路径
     */
    String synthesizeSessionAudio(Long sessionId, SubtitleTypeEnum subtitleTypeEnum);

}
