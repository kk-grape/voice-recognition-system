package com.music.asr.admin.req;

import com.music.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 管理员-实时识别会话查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminAsrSessionQueryReq extends PageReq {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 源语言
     */
    private String sourceLang;

    /**
     * 目标语言
     */
    private String targetLang;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;
}
