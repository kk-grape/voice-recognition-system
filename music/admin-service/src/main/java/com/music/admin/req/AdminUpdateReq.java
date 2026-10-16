package com.music.admin.req;

import lombok.Data;

/**
 * 更新管理员请求类
 */
@Data
public class AdminUpdateReq {

    /**
     * 新密码（可选，不修改则留空）
     */
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
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
