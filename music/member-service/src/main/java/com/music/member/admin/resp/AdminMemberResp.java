package com.music.member.admin.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 管理员-会员响应类
 */
@Data
public class AdminMemberResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String mobile;

    private String name;

    private String nickname;

    private String avatar;

    private Integer gender;

    private Date birthday;

    private String email;

    private String bio;

    private Integer loginCount;

    private Date lastLoginAt;

    /**
     * 会员等级：0-非会员，1-月卡，2-季卡，3-年卡
     */
    private Integer memberLevel;

    /**
     * 会员等级名称
     */
    private String memberLevelName;

    /**
     * 会员状态：0-非会员，1-有效，2-已过期
     */
    private Integer memberStatus;

    private Date memberExpireAt;

    private String memberRemark;

    private Date createdAt;

    private Date updatedAt;
}
