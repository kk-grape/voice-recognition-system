package com.music.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.admin.domain.Admin;
import com.music.admin.enums.AdminRoleEnum;
import com.music.admin.enums.AdminStatusEnum;
import com.music.admin.mapper.AdminMapper;
import com.music.admin.req.AdminCreateReq;
import com.music.admin.req.AdminLoginReq;
import com.music.admin.req.AdminQueryReq;
import com.music.admin.req.AdminUpdateReq;
import com.music.common.resp.AdminLoginResp;
import com.music.admin.resp.AdminResp;
import com.music.common.exception.BusinessException;
import com.music.common.exception.BusinessExceptionEnum;
import com.music.common.resp.PageResp;
import com.music.common.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     */
    public AdminLoginResp login(AdminLoginReq req) {
        Admin adminDB = adminMapper.selectByLoginName(req.getLoginName());
        if (adminDB == null) {
            log.warn("登录名不存在，{}", req.getLoginName());
            throw new BusinessException(BusinessExceptionEnum.USER_LOGIN_ERROR);
        }

        if (adminDB.getPassword().equalsIgnoreCase(req.getPassword())) {
            log.info("登录成功，{}", req.getLoginName());

            // 更新登录信息
            Admin updateAdmin = new Admin();
            updateAdmin.setId(adminDB.getId());
            updateAdmin.setLoginCount(adminDB.getLoginCount() == null ? 1 : adminDB.getLoginCount() + 1);
            updateAdmin.setLastLoginAt(new Date());
            updateAdmin.setLastLoginIp(req.getLoginIp());
            adminMapper.updateLoginInfo(updateAdmin);

            // 构建返回结果
            AdminLoginResp resp = new AdminLoginResp();
            resp.setId(adminDB.getId());
            resp.setLoginName(adminDB.getLoginName());

            // 生成JWT Token
            Map<String, Object> map = BeanUtil.beanToMap(resp);
            String token = JwtUtil.createLoginToken(map);
            resp.setToken(token);

            return resp;
        } else {
            log.warn("密码错误，{}", req.getLoginName());
            throw new BusinessException(BusinessExceptionEnum.USER_LOGIN_ERROR);
        }
    }

    /**
     * 查询管理员列表
     */
    public PageResp<AdminResp> queryList(AdminQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Admin> list = adminMapper.selectListByCondition(
                req.getLoginName(),
                req.getRole(),
                req.getStatus()
        );

        PageResp<AdminResp> pageResp = new PageResp<>();
        PageInfo<Admin> pageInfo = new PageInfo<>(list);
        pageResp.setTotal(pageInfo.getTotal());

        // 转换数据并设置角色/状态名称
        List<AdminResp> respList = BeanUtil.copyToList(list, AdminResp.class);
        for (AdminResp resp : respList) {
            resp.setRoleName(AdminRoleEnum.getDescByCode(resp.getRole()));
            resp.setStatusName(AdminStatusEnum.getDescByCode(resp.getStatus()));
        }
        pageResp.setList(respList);

        return pageResp;
    }

    /**
     * 查看管理员详情
     */
    public AdminResp detail(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            return null;
        }

        AdminResp resp = new AdminResp();
        BeanUtil.copyProperties(admin, resp);
        resp.setRoleName(AdminRoleEnum.getDescByCode(admin.getRole()));
        resp.setStatusName(AdminStatusEnum.getDescByCode(admin.getStatus()));

        return resp;
    }

    /**
     * 新增管理员
     */
    public void create(AdminCreateReq req) {
        log.info("新增管理员：{}", req.getLoginName());

        // 检查登录名是否已存在
        Admin exist = adminMapper.selectByLoginName(req.getLoginName());
        if (exist != null) {
            throw new BusinessException(BusinessExceptionEnum.ADMIN_LOGIN_NAME_EXIST);
        }

        Admin admin = new Admin();
        admin.setId(IdUtil.getSnowflakeNextId());
        admin.setLoginName(req.getLoginName());
        admin.setPassword(req.getPassword());
        admin.setName(req.getName());
        admin.setAvatar(req.getAvatar());
        admin.setPhone(req.getPhone());
        admin.setEmail(req.getEmail());
        admin.setDepartment(req.getDepartment());
        // 默认普通管理员
        admin.setRole(req.getRole() != null ? req.getRole() : AdminRoleEnum.ADMIN.getCode());
        admin.setStatus(AdminStatusEnum.NORMAL.getCode());
        admin.setLoginCount(0);
        admin.setCreatedAt(new Date());
        admin.setUpdatedAt(new Date());
        admin.setRemark(req.getRemark());

        adminMapper.insert(admin);
    }

    /**
     * 修改管理员
     */
    public void update(Long id, AdminUpdateReq req) {
        log.info("修改管理员：{}", id);

        Admin admin = new Admin();
        admin.setId(id);

        // 只更新非空字段
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            admin.setPassword(req.getPassword());
        }
        if (req.getName() != null) {
            admin.setName(req.getName());
        }
        if (req.getAvatar() != null) {
            admin.setAvatar(req.getAvatar());
        }
        if (req.getPhone() != null) {
            admin.setPhone(req.getPhone());
        }
        if (req.getEmail() != null) {
            admin.setEmail(req.getEmail());
        }
        if (req.getDepartment() != null) {
            admin.setDepartment(req.getDepartment());
        }
        if (req.getRole() != null) {
            admin.setRole(req.getRole());
        }
        if (req.getStatus() != null) {
            admin.setStatus(req.getStatus());
        }
        if (req.getRemark() != null) {
            admin.setRemark(req.getRemark());
        }
        admin.setUpdatedAt(new Date());

        adminMapper.updateById(admin);
    }

    /**
     * 删除管理员
     */
    public void delete(Long id) {
        log.info("删除管理员：{}", id);
        adminMapper.deleteById(id);
    }

    /**
     * 统计管理员总数
     */
    public long count() {
        return adminMapper.countAll();
    }
}
