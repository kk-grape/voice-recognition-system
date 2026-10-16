package com.music.asr.admin.resp;

import lombok.Data;

/**
 * 会话统计响应类
 */
@Data
public class SessionCountResp {

    /**
     * 会话状态
     */
    private String status;

    /**
     * 数量
     */
    private Long count;
}
