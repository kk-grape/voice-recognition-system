package com.music.nls.business.member.resp;

import java.util.Date;
import lombok.Data;

/**
 * 会员信息响应类
 */
@Data
public class MemberInfoResp {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 真实姓名
     */
    private String name;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private Date birthday;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 会员等级：0-普通用户，1-高级会员
     */
    private Integer memberLevel;
    
    /**
     * 会员等级名称
     */
    private String memberLevelName;
    
    /**
     * 会员过期时间
     */
    private Date memberExpireAt;
    
    /**
     * 会员状态：0-非会员，1-会员有效期内，2-会员已过期
     */
    private Integer memberStatus;
    
}
