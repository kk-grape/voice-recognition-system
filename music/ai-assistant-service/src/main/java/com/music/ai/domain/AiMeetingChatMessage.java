package com.music.ai.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AiMeetingChatMessage {

    private Long id;

    /** 会话ID */
    private Long sessionId;

    /** 角色：user=用户  assistant=AI */
    private String role;

    /** 消息内容 */
    private String content;

    /** AI 技能标记：summary/generate/modify */
    private String aiSkill;

    /** 输入Token数 */
    private Integer inputTokens;

    /** 输出Token数 */
    private Integer outputTokens;

    private Date createdAt;
}
