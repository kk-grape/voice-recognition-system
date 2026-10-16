package com.music.ai.resp;

import lombok.Data;

/**
 * 通义千问 DashScope API 调用结果封装
 */
@Data
public class DashScopeResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * AI 返回的内容
     */
    private String content;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 输入 token 数量
     */
    private int inputTokens;

    /**
     * 输出 token 数量
     */
    private int outputTokens;

    public static DashScopeResult success(String content, int inputTokens, int outputTokens) {
        DashScopeResult result = new DashScopeResult();
        result.success = true;
        result.content = content;
        result.inputTokens = inputTokens;
        result.outputTokens = outputTokens;
        return result;
    }

    public static DashScopeResult error(String errorMessage) {
        DashScopeResult result = new DashScopeResult();
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
    }
}
