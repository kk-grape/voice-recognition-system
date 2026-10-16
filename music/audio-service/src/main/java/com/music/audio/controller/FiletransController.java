package com.music.audio.controller;

import com.music.audio.req.FiletransPayReq;
import com.music.audio.service.FiletransService;
import com.music.common.alipay.AlipayInfoPayResp;
import com.music.common.context.LoginMemberContext;
import com.music.common.req.FiletransQueryReq;
import com.music.common.resp.CommonResp;
import com.music.common.resp.FiletransQueryResp;
import com.music.common.resp.PageResp;
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
    public CommonResp<PageResp<FiletransQueryResp>> query(@Valid FiletransQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<FiletransQueryResp> pageResp = filetransService.query(req);
        return new CommonResp<>(pageResp);
    }

    /**
     * 删除语音识别任务（逻辑删除）
     *
     * 说明：
     * - 前端使用 DELETE /web/filetrans/delete/{id} 调用
     * - 仅对当前登录会员自己的任务生效（通过 LoginMemberContext 约束查询范围）
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Void> delete(@PathVariable("id") Long id) {
        log.info("删除语音识别任务开始，id={}", id);
        filetransService.delete(id);
        log.info("删除语音识别任务结束，id={}", id);
        return new CommonResp<>();
    }

    /**
     * client
     * 支付成功后回调：更新语音识别记录状态并发起识别任务。
     * 供订单服务在支付回调时调用。
     *
     * @param filetransId 语音识别记录ID
     */
    @PostMapping("/after-pay-success")
    public CommonResp<Void> afterPaySuccess(@RequestParam("filetransId") Long filetransId) {
        log.info("支付成功回调开始，filetransId={}", filetransId);
        filetransService.afterPaySuccess(filetransId);
        log.info("支付成功回调结束，filetransId={}", filetransId);
        return new CommonResp<>();
    }
}
