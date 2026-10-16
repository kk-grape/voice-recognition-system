package com.music.asr.translate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 翻译服务配置类。
 * 对应配置文件中的 translate.baidu.* 项
 */
@Data
@Component
@ConfigurationProperties(prefix = "translate.baidu")
public class TranslateProperties {

    /**
     * 百度翻译 APPID
     * 在 https://fanyi-api.baidu.com/ 注册获取
     */
    private String appId;

    /**
     * 百度翻译密钥
     */
    private String secretKey;

    /**
     * 默认源语言（auto 表示自动检测）
     */
    private String from = "auto";

    /**
     * 默认目标语言
     */
    private String to = "en";

}
