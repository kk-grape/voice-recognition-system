package com.music.nls.business.audio.controller;

import com.music.nls.business.member.context.LoginMemberContext;
import com.music.nls.business.audio.req.FiletransPayReq;
import com.music.nls.business.audio.req.FiletransQueryReq;
import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.audio.resp.FiletransQueryResp;
import com.music.nls.business.common.alipay.AlipayInfoPayResp;
import com.music.nls.business.common.resp.PageResp;
import com.music.nls.business.audio.service.FiletransService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/web/filetrans")
public class FiletransController {

    @Resource
    private FiletransService filetransService;

    @PostMapping("/pay")
    public CommonResp<AlipayInfoPayResp> pay(@Valid @RequestBody FiletransPayReq req) throws Exception {
        log.info("语音识别支付开始");
        AlipayInfoPayResp orderInfoPayResp = filetransService.pay(req);
        log.info("语音识别支付结束");
        return new CommonResp<>(orderInfoPayResp);
    }

    @GetMapping("/query")
    public CommonResp<PageResp<FiletransQueryResp>> query(@Valid FiletransQueryReq req)  {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<FiletransQueryResp> pageResp = filetransService.query(req);
        return new CommonResp<>(pageResp);
    }
}
