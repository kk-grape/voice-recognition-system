package com.music.admin.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增管理员请求类
 */
@Data
public class AdminCreateReq {

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
     * 管理员姓名
     */
    private String name;

    /**
     * 管理员头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 角色：super-超级管理员，admin-普通管理员，operator-操作员
     */
    private String role;

    /**
     * 备注
     */
    private String remark;
}
