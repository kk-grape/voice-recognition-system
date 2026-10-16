package com.music.admin.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.music.admin.req.AdminLoginReq;
import com.music.admin.req.AdminQueryReq;
import com.music.admin.req.AdminCreateReq;
import com.music.admin.req.AdminUpdateReq;
import com.music.common.resp.AdminLoginResp;
import com.music.admin.resp.AdminResp;
import com.music.admin.service.AdminService;
import com.music.admin.service.KaptchaService;
import com.music.common.resp.CommonResp;
import com.music.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 Controller
 * 路径：/admin/**
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource(name = "adminKaptchaService")
    private KaptchaService kaptchaService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public CommonResp<AdminLoginResp> login(@Valid @RequestBody AdminLoginReq req) {
        req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));

        log.info("管理员登录开始：{}", req.getLoginName());

        // 校验图片验证码，防止机器人
        kaptchaService.validCode(req.getImageCode(), req.getImageCodeToken());

        AdminLoginResp resp = adminService.login(req);

        return new CommonResp<>(resp);
    }

    /**
     * 查询管理员列表（分页）
     */
    @GetMapping("/list")
    public CommonResp<PageResp<AdminResp>> list(AdminQueryReq req) {
        log.info("查询管理员列表：page={}, size={}", req.getPage(), req.getSize());
        PageResp<AdminResp> pageResp = adminService.queryList(req);
        log.info("查询结果：共 {} 条记录", pageResp.getTotal());
        return new CommonResp<>(pageResp);
    }

    /**
     * 查看管理员详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<AdminResp> detail(@PathVariable Long id) {
        log.info("查看管理员详情：id={}", id);
        AdminResp resp = adminService.detail(id);
        return new CommonResp<>(resp);
    }

    /**
     * 新增管理员
     */
    @PostMapping("/create")
    public CommonResp<Void> create(@Valid @RequestBody AdminCreateReq req) {
        log.info("新增管理员：loginName={}", req.getLoginName());
        // 密码加密
        req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));
        adminService.create(req);
        return new CommonResp<>();
    }

    /**
     * 修改管理员
     */
    @PutMapping("/update/{id}")
    public CommonResp<Void> update(@PathVariable Long id, @RequestBody AdminUpdateReq req) {
        log.info("修改管理员：id={}", id);
        // 如果传了新密码，需要加密
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));
        }
        adminService.update(id, req);
        return new CommonResp<>();
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Void> delete(@PathVariable Long id) {
        log.info("删除管理员：id={}", id);
        adminService.delete(id);
        return new CommonResp<>();
    }

    /**
     * 统计管理员总数
     */
    @GetMapping("/count")
    public CommonResp<Long> count() {
        long count = adminService.count();
        return new CommonResp<>(count);
    }
}
