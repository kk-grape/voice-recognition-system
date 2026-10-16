package com.music.ai.resp;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会议纪要生成结果封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSummaryResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 源语言纪要
     */
    private String summarySource;

    /**
     * 目标语言纪要
     */
    private String summaryTarget;

    /**
     * 关键要点
     */
    private JSONArray keyPoints;

    /**
     * 行动项
     */
    private JSONArray actionItems;

    /**
     * 决策结果
     */
    private JSONArray decisions;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 输入 token 数量
     */
    private int inputTokens;

    /**
     * 输出 token 数量
     */
    private int outputTokens;

    public static MeetingSummaryResult error(String errorMessage) {
        MeetingSummaryResult result = new MeetingSummaryResult();
        result.success = false;
        result.errorMessage = errorMessage;
        return result;
    }
}
