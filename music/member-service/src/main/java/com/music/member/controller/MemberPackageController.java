package com.music.member.controller;


import com.music.common.resp.CommonResp;
import com.music.member.domain.MemberPackage;
import com.music.member.service.MemberPackageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/member/package")
public class MemberPackageController {

    @Resource
    private MemberPackageService memberPackageService;

    /**
     * 获取所有可用的会员套餐
     */
    @GetMapping("/list")
    public CommonResp<List<MemberPackage>> getMemberPackages() {
        List<MemberPackage> memberPackages = memberPackageService.getAllAvailablePackages();
        return new CommonResp<>(memberPackages);
    }

    /**
     * 根据ID获取会员套餐详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<MemberPackage> getPackageDetail(@PathVariable Long id) {
        MemberPackage memberPackage = memberPackageService.getPackageById(id);
        return new CommonResp<>(memberPackage);
    }

    /**
     * 开通会员
     */
    @PostMapping("/purchase/{id}")
    public CommonResp<Object> purchasePackage(@PathVariable Long id) {
        memberPackageService.openMember(id);
        return new CommonResp<>();
    }
}
