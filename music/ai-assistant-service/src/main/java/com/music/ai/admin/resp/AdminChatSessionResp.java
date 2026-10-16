package com.music.ai.admin.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 管理员-AI对话会话响应类
 */
@Data
public class AdminChatSessionResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String sessionUuid;

    private String sessionTitle;

    /**
     * 状态：1=正常，2=已删除
     */
    private Integer status;

    /**
     * 消息总数
     */
    private Integer messageCount;

    /**
     * 末条消息预览
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private Date lastMessageAt;

    private Date createdAt;

    private Date updatedAt;
}
