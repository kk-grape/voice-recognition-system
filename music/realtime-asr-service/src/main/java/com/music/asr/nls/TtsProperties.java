package com.music.asr.nls;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 语音合成（TTS）配置属性类。
 * 配置参数从 application.yml 或 Nacos 配置中心读取
 */
@Data
@Component
@ConfigurationProperties(prefix = "nls.tts")
public class TtsProperties {

    /**
     * 是否启用语音合成
     */
    private boolean enabled;

    /**
     * 默认发音人
     */
    private String voice;

    /**
     * 语速 -500~500
     */
    private Integer speechRate;

    /**
     * 语调 -500~500
     */
    private Integer pitchRate;

    /**
     * 音量 0~100
     */
    private Integer volume;

    /**
     * 音频格式：wav、mp3
     */
    private String format;

    /**
     * 采样率：8000、16000
     */
    private Integer sampleRate;

    /**
     * 音频输出根目录
     */
    private String outputPath;

    /**
     * 原文最终音频文件名（不含扩展名）
     */
    private String originalAudioFileName = "original_audio";

    /**
     * 翻译最终音频文件名（不含扩展名）
     */
    private String translatedAudioFileName = "translated_audio";

}
