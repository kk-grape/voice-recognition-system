package com.music.member.admin.req;

import com.music.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员-会员查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminMemberQueryReq extends PageReq {

    /**
     * 手机号（模糊搜索）
     */
    private String mobile;

    /**
     * 昵称（模糊搜索）
     */
    private String nickname;

    /**
     * 会员等级
     */
    private Integer memberLevel;
}
