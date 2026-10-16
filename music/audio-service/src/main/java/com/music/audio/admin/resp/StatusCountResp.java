package com.music.audio.admin.resp;

import lombok.Data;

/**
 * 状态统计响应类
 */
@Data
public class StatusCountResp {

    private String status;

    private Long count;
}
