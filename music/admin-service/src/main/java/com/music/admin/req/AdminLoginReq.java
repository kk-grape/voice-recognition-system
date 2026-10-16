package com.music.admin.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员登录请求类
 */
@Data
public class AdminLoginReq {

    /**
     * 登录账号
     */
    @NotBlank(message = "登录名不能为空")
    private String loginName;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 图形验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String imageCode;

    /**
     * 验证码Token
     */
    @NotBlank(message = "验证码Token不能为空")
    private String imageCodeToken;

    /**
     * 登录IP
     */
    private String loginIp;

}
