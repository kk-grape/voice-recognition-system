package com.music.member.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.music.common.resp.CommonResp;
import com.music.member.enums.SmsCodeUseEnum;
import com.music.member.req.MemberInfoUpdateReq;
import com.music.member.req.MemberLoginReq;
import com.music.member.req.MemberRegisterReq;
import com.music.member.req.MemberResetReq;
import com.music.member.resp.MemberInfoResp;
import com.music.common.resp.MemberLoginResp;
import com.music.member.service.KaptchaService;
import com.music.member.service.MemberLoginLogService;
import com.music.member.service.MemberService;
import com.music.member.service.SmsCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/web/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @Resource
    private SmsCodeService smsCodeService;

    @Resource(name = "memberKaptchaService")
    private KaptchaService kaptchaService;

    @Resource
    private MemberLoginLogService memberLoginLogService;


    @PostMapping("/register")
    public CommonResp<Object> register(@Valid @RequestBody MemberRegisterReq req) {
        req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));//密码加密

        log.info("会员注册开始：{}", req.getMobile());
        smsCodeService.validCode(req.getMobile(), SmsCodeUseEnum.REGISTER.getCode(), req.getCode());//验证验证码
        log.info("注册验证码校验通过：{}", req.getMobile());

        memberService.register(req);
        return new CommonResp<>();
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req) {
        req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));

        log.info("会员登录开始：{}", req.getMobile());

        // 校验图片验证码，防止机器人
        kaptchaService.validCode(req.getImageCode(), req.getImageCodeToken());

        MemberLoginResp memberLoginResp = memberService.login(req);

        return new CommonResp<>(memberLoginResp);
    }

    @PostMapping("/reset")
    public CommonResp<Object> reset(@Valid @RequestBody MemberResetReq req) {
        req.setPassword(DigestUtil.md5Hex(req.getPassword().toLowerCase()));

        log.info("会员忘记密码开始：{}", req.getMobile());

        smsCodeService.validCode(req.getMobile(), SmsCodeUseEnum.RESET.getCode(), req.getCode());
        log.info("忘记密码验证码校验通过：{}", req.getMobile());

        memberService.reset(req);
        return new CommonResp<>();
    }

    @GetMapping("/heart")
    public CommonResp<Object> heart() {
        memberLoginLogService.updateHeartInfo();
        return new CommonResp<>();
    }

    @GetMapping("/info")
    public CommonResp<MemberInfoResp> getInfo() {
        log.info("获取会员信息");
        MemberInfoResp memberInfoResp = memberService.getInfo();
        return new CommonResp<>(memberInfoResp);
    }

    @PutMapping("/update")
    public CommonResp<Object> update(@Valid @RequestBody MemberInfoUpdateReq req) {
        log.info("更新会员信息");
        memberService.updateInfo(req);
        return new CommonResp<>();
    }

}
