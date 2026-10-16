package com.music.admin.req;

import com.music.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminQueryReq extends PageReq {

    /**
     * 登录名（模糊搜索）
     */
    private String loginName;

    /**
     * 角色筛选：super-超级管理员，admin-普通管理员，operator-操作员
     */
    private String role;

    /**
     * 状态筛选：1-正常，0-禁用
     */
    private Integer status;
}
