package com.music.member.admin.req;

import lombok.Data;

/**
 * 管理员-会员更新请求类
 */
@Data
public class AdminMemberUpdateReq {

    /**
     * 昵称
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 会员备注
     */
    private String memberRemark;
}
