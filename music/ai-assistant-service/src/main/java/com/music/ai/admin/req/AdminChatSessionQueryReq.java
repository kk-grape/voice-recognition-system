package com.music.ai.admin.req;

import com.music.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员-AI对话会话查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminChatSessionQueryReq extends PageReq {

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 会话状态
     */
    private Integer status;

    /**
     * 标题关键词搜索
     */
    private String title;
}
