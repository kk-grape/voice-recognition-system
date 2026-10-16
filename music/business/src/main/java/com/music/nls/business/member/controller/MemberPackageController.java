package com.music.nls.business.member.controller;

import com.music.nls.business.member.domain.MemberPackage;
import com.music.nls.business.member.service.MemberPackageService;
import com.music.nls.business.common.resp.CommonResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/web/member/package")
public class MemberPackageController {

    @Resource
    private MemberPackageService memberPackageService;

    /**
     * 获取所有可用的会员套餐
     * 
     * @return 会员套餐列表
     */
    @GetMapping("/list")
    public CommonResp<List<MemberPackage>> getMemberPackages() {
        List<MemberPackage> memberPackages = memberPackageService.getAllAvailablePackages();
        return new CommonResp<>(memberPackages);
    }

    /**
     * 根据ID获取会员套餐详情
     * 
     * @param id 套餐ID
     * @return 会员套餐详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<MemberPackage> getPackageDetail(@PathVariable Long id) {
        MemberPackage memberPackage = memberPackageService.getPackageById(id);
        return new CommonResp<>(memberPackage);
    }
}
