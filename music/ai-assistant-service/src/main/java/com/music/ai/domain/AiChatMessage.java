package com.music.ai.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AiChatMessage {

    private Long id;

    /** 会话ID */
    private Long sessionId;

    /**
     * 角色：user=用户  assistant=AI
     */
    private String role;

    /** 消息内容 */
    private String content;

    /** AI 技能标记：greet/guide/emotion/chat */
    private String aiSkill;

    /** 情感标签：positive/neutral/negative */
    private String sentimentLabel;

    /** 输入Token数 */
    private Integer inputTokens;

    /** 输出Token数 */
    private Integer outputTokens;

    /** 引导动作：navigate/open/download */
    private String guidanceAction;

    /** 引导目标：页面路径或功能名 */
    private String guidanceTarget;

    private Date createdAt;
}
