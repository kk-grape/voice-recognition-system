package com.music.ai.admin.resp;

import lombok.Data;

/**
 * AI对话会话统计响应类
 */
@Data
public class ChatSessionCountResp {

    /**
     * 会话状态
     */
    private Integer status;

    /**
     * 数量
     */
    private Long count;
}
