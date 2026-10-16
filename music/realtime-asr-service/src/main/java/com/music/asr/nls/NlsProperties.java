package com.music.asr.nls;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 NLS（智能语音交互）配置属性类。
 * 用于配置阿里云语音识别、语音合成的认证信息和服务端点
 */
@Data
@Component
@ConfigurationProperties(prefix = "nls.filetrans")
public class NlsProperties {

    /**
     * 阿里云访问密钥 ID
     */
    private String accessKeyId;

    /**
     * 阿里云访问密钥 Secret
     */
    private String accessKeySecret;

    /**
     * 地域 ID
     */
    private String regionId;

    /**
     * 端点名称
     */
    private String endpointName;

    /**
     * 产品名称
     */
    private String product;

    /**
     * 域名
     */
    private String domain;

    /**
     * API 版本
     */
    private String version;

    /**
     * 任务版本
     */
    private String taskVersion;

    /**
     * 回调地址
     */
    private String callback;

}
