package com.music.asr.translate;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

/**
 * 翻译工具类（使用百度翻译 API）。
 *
 * 【使用方式】
 * 1. 在 https://fanyi-api.baidu.com/ 注册成为开发者
 * 2. 获取 APPID 和密钥
 * 3. 在 application.yml 中配置 translate.baidu.appId 和 translate.baidu.secretKey
 *
 * 【免费额度】
 * - 百度翻译 API 每月有免费额度，适合学习项目
 * - 超出后按字符数计费
 */
@Slf4j
@Component
public class TranslateUtil {

    private static TranslateProperties translateProperties;

    @Resource
    public void setTranslateProperties(TranslateProperties translateProperties) {
        TranslateUtil.translateProperties = translateProperties;
    }

    /**
     * 翻译文本
     *
     * @param text 要翻译的文本
     * @return 翻译后的文本，翻译失败返回原文本
     */
    public static String translate(String text) {
        return translate(text, null, null);
    }

    /**
     * 翻译文本
     *
     * @param text   要翻译的文本
     * @param from   源语言（如 zh, en, ja），null 则使用默认配置
     * @param to     目标语言（如 en, zh, ja），null 则使用默认配置
     * @return 翻译后的文本，翻译失败返回原文本
     */
    public static String translate(String text, String from, String to) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // 使用默认配置
        if (from == null || from.isEmpty()) {
            from = translateProperties.getFrom();
        }
        if (to == null || to.isEmpty()) {
            to = translateProperties.getTo();
        }

        try {
            // 生成签名
            String appId = translateProperties.getAppId();
            String secretKey = translateProperties.getSecretKey();
            String salt = String.valueOf(new Random().nextInt(100000));
            String sign = generateSign(appId, text, salt, secretKey);

            // 构建请求 URL
            String url = String.format(
                    "https://fanyi-api.baidu.com/api/trans/vip/translate?q=%s&from=%s&to=%s&appid=%s&salt=%s&sign=%s",
                    java.net.URLEncoder.encode(text, "utf-8"),
                    from, to, appId, salt, sign
            );

            // 发送请求
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应
            String result = parseResponse(response.body());
            if (result != null) {
                log.info("翻译成功: {} -> {}", text, result);
                return result;
            }
        } catch (Exception e) {
            log.error("翻译失败: {}", text, e);
        }

        // 翻译失败返回原文本
        return text;
    }

    /**
     * 生成百度翻译签名
     * 签名算法：MD5(appid + q + salt + secretKey)
     */
    private static String generateSign(String appId, String text, String salt, String secretKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(appId).append(text).append(salt).append(secretKey);
        return DigestUtil.md5Hex(sb.toString());
    }

    /**
     * 解析百度翻译 API 响应
     */
    private static String parseResponse(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);

            // 检查错误码
            if (json.containsKey("error_code")) {
                log.error("百度翻译 API 错误: {} - {}",
                        json.getString("error_code"),
                        json.getString("error_msg"));
                return null;
            }

            // 解析翻译结果
            JSONArray transResult = json.getJSONArray("trans_result");
            if (transResult != null && !transResult.isEmpty()) {
                JSONObject firstResult = transResult.getJSONObject(0);
                return firstResult.getString("dst");
            }
        } catch (Exception e) {
            log.error("解析翻译响应失败: {}", responseBody, e);
        }
        return null;
    }
}
