package com.music.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 管理员实体
 * 对应数据库表：admin
 */
@Data
public class Admin {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 登录账号（唯一）
     */
    private String loginName;

    /**
     * 登录密码（MD5加密）
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
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱地址
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
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginAt;

    /**
     * 最后登录IP地址
     */
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;

    /**
     * 备注信息
     */
    private String remark;
}
