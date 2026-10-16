package com.music.ai.controller;

import com.music.ai.req.AiMeetingGenerateReq;
import com.music.ai.req.AiMeetingMessageReq;
import com.music.ai.resp.AiMeetingResp;
import com.music.ai.service.AiMeetingService;
import com.music.common.context.LoginMemberContext;
import com.music.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 会议纪要 - 纪要相关接口
 */
@Slf4j
@RestController
@RequestMapping("/web/ai/meeting")
public class AiMeetingController {

    @Resource
    private AiMeetingService aiMeetingService;

    /**
     * 生成会议纪要（主功能）
     */
    @PostMapping("/generate")
    public CommonResp<AiMeetingResp> generateSummary(@Valid @RequestBody AiMeetingGenerateReq req) {
        Long userId = LoginMemberContext.getId();
        Long asrSessionId = Long.parseLong(req.getAsrSessionId());
        String targetLang = req.getTargetLang() != null ? req.getTargetLang() : "en";
        log.info("生成会议纪要，用户：{}，asrSessionId：{}，目标语言：{}", userId, asrSessionId, targetLang);
        AiMeetingResp resp = aiMeetingService.generateSummary(userId, asrSessionId, targetLang);
        return new CommonResp<>(resp);
    }

    /**
     * 追问/修改会议纪要
     */
    @PostMapping("/message")
    public CommonResp<AiMeetingResp> sendMessage(@Valid @RequestBody AiMeetingMessageReq req) {
        Long userId = LoginMemberContext.getId();
        Long asrSessionId = Long.parseLong(req.getAsrSessionId());
        log.info("追问会议纪要，用户：{}，asrSessionId：{}", userId, asrSessionId);
        AiMeetingResp resp = aiMeetingService.sendMessage(userId, asrSessionId, req.getContent());
        return new CommonResp<>(resp);
    }

    /**
     * 获取会议纪要及对话历史
     */
    @GetMapping("/history/{asrSessionId}")
    public CommonResp<AiMeetingResp> getMeetingHistory(@PathVariable String asrSessionId) {
        Long userId = LoginMemberContext.getId();
        Long asrSessionIdLong = Long.parseLong(asrSessionId);
        log.info("获取会议纪要历史，用户：{}，asrSessionId：{}", userId, asrSessionIdLong);
        AiMeetingResp resp = aiMeetingService.getMeetingHistory(userId, asrSessionIdLong);
        return new CommonResp<>(resp);
    }
}
