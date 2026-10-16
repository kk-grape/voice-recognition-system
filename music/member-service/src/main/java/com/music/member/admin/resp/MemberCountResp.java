package com.music.member.admin.resp;

import lombok.Data;

/**
 * 会员统计响应类
 */
@Data
public class MemberCountResp {

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 会员等级名称
     */
    private String memberLevelName;

    /**
     * 数量
     */
    private Long count;
}
