package com.music.asr.nls;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 实时语音识别配置属性类。
 *
 * 【说明】
 * - AppKey 通过 AsrLangEnum 枚举获取，每个语言对应一个 AppKey
 * - 前端只需传入语言代码（如 LANG0, LANG1），系统自动匹配对应的 AppKey
 * - 配置参数从 application.yml 或 Nacos 配置中心读取
 */
@Data
@Component
@ConfigurationProperties(prefix = "nls.realtime")
public class RealtimeAsrProperties {

    /**
     * 是否返回中间识别结果（断句过程中的实时结果）
     */
    private Boolean enableIntermediateResult;

    /**
     * 是否生成并返回标点符号
     */
    private Boolean enablePunctuation;

    /**
     * 是否将返回结果规整化（如"一百"转为"100"）
     * 仅中文有效
     */
    private Boolean enableITN;

    /**
     * VAD 断句最大静音时长（毫秒）
     * 有效值 200~6000
     */
    private Integer maxSentenceSilence;

}
