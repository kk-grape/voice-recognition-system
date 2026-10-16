package com.music.ai.admin.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 管理员-AI对话消息响应类
 */
@Data
public class AdminChatMessageResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String sessionUuid;

    /**
     * 消息角色：user/assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    private Date createdAt;
}
