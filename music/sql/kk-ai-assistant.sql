-- ============================================
-- AI 助手服务数据库初始化脚本
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS kk-ai-assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kk-ai-assistant;

-- ============================================
-- 第一类：AI 小助手
-- ============================================

-- AI 小助手机会话主表
CREATE TABLE ai_chat_session (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',

    session_uuid    VARCHAR(64) NOT NULL UNIQUE COMMENT '会话唯一标识',
    session_title   VARCHAR(255) DEFAULT '新对话' COMMENT '会话标题',

    -- 状态：1=正常（前端显示） 2=已删除（前端不显示）
    status          TINYINT DEFAULT 1 COMMENT '1-正常 2-已删除',

    -- 统计
    message_count   INT DEFAULT 0 COMMENT '消息总数',
    last_message    TEXT COMMENT '末条消息预览（前100字）',
    last_message_at DATETIME COMMENT '最后消息时间',

    -- 时间
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_user_status (user_id, status) COMMENT '查询用户所有正常会话',
    INDEX idx_user_id (user_id) COMMENT '按用户ID查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI小助手机会话主表';

-- AI 小助手消息明细表
CREATE TABLE ai_chat_message (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    session_id      BIGINT NOT NULL COMMENT '会话ID',

    -- 角色：user=用户  assistant=AI
    role            VARCHAR(16) NOT NULL COMMENT 'user / assistant',
    content         TEXT NOT NULL COMMENT '消息内容',

    -- AI 技能标记
    ai_skill        VARCHAR(64) COMMENT 'greet/guide/emotion/chat',
    sentiment_label VARCHAR(16) COMMENT '情感标签：positive/neutral/negative',

    -- Token 消耗
    input_tokens    INT DEFAULT 0 COMMENT '输入Token数',
    output_tokens   INT DEFAULT 0 COMMENT '输出Token数',

    -- AI 引导记录
    guidance_action VARCHAR(100) COMMENT '引导动作：navigate/open/download',
    guidance_target VARCHAR(200) COMMENT '引导目标：页面路径或功能名',

    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    INDEX idx_session_id (session_id) COMMENT '按会话ID查询消息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI小助手消息明细表';


-- ============================================
-- 第二类：会议纪要
-- ============================================

-- AI 会议纪要总结表
-- 存每次对话后更新后的最新纪要文本
CREATE TABLE ai_meeting_summary (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT COMMENT '用户ID',

    -- 关联的 ASR 会话（一个 asrSessionId 只能有一条纪要）
    asr_session_id  BIGINT NOT NULL COMMENT '关联ASR会话ID',

    -- 纪要内容
    summary_source  TEXT COMMENT '源语言会议纪要',
    summary_target  TEXT COMMENT '目标语言会议纪要',

    -- AI 提取的结构化信息（JSON数组格式）
    key_points      TEXT COMMENT '关键要点（JSON数组）',
    action_items    TEXT COMMENT '行动项（JSON数组）',
    decisions       TEXT COMMENT '决策结果（JSON数组）',
    keywords        VARCHAR(500) COMMENT '关键词，多个用逗号分隔',

    -- 元数据
    subtitle_count  INT COMMENT '字幕片段数量',
    total_tokens    INT COMMENT '总消耗Token数',

    -- 时间
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_asr_session (asr_session_id) COMMENT '一个asrSessionId只能有一条纪要',
    INDEX idx_user_id (user_id) COMMENT '按用户ID查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会议纪要总结表';

-- AI 会议纪要对话会话表
-- 一个 asrSessionId 对应一条会话（可多次追问）
CREATE TABLE ai_meeting_chat_session (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT COMMENT '用户ID',

    -- 关联的 ASR 会话
    asr_session_id  BIGINT NOT NULL COMMENT '关联ASR会话ID',

    -- 状态：1=正常 2=已删除
    status          TINYINT DEFAULT 1 COMMENT '1-正常 2-已删除',

    -- 统计
    message_count   INT DEFAULT 0 COMMENT '消息总数',
    last_message    TEXT COMMENT '末条消息预览',
    last_message_at DATETIME COMMENT '最后消息时间',

    -- 时间
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_asr_session (asr_session_id) COMMENT '一个asrSessionId只能有一条对话会话',
    INDEX idx_user_id (user_id) COMMENT '按用户ID查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会议纪要对话会话表';

-- AI 会议纪要对话消息明细表
-- 一条 AI 会议纪要对话会话 对应 多条消息明细
-- 每次用户追问后，AI 生成修改建议，同步更新 ai_meeting_summary 的纪要内容
CREATE TABLE ai_meeting_chat_message (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    session_id      BIGINT NOT NULL COMMENT '会话ID',

    role            VARCHAR(16) NOT NULL COMMENT 'user / assistant',
    content         TEXT NOT NULL COMMENT '消息内容',

    ai_skill        VARCHAR(64) COMMENT 'summary/generate/modify',

    -- Token 消耗
    input_tokens    INT DEFAULT 0 COMMENT '输入Token数',
    output_tokens   INT DEFAULT 0 COMMENT '输出Token数',

    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    INDEX idx_session_id (session_id) COMMENT '按会话ID查询消息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会议纪要对话消息明细表';
