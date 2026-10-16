package com.music.ai.controller;

import com.music.ai.req.AiChatMessageReq;
import com.music.ai.resp.AiChatResp;
import com.music.ai.service.AiChatService;
import com.music.common.context.LoginMemberContext;
import com.music.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 小助手 - 聊天相关接口
 */
@Slf4j
@RestController
@RequestMapping("/web/ai/chat")
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    /**
     * 获取最近会话（点开聊天框时调用）
     * - 有会话：返回最近一次 + 消息列表
     * - 无会话：自动创建新会话
     */
    @GetMapping("/recent")
    public CommonResp<AiChatResp> getRecentSession() {
        Long userId = LoginMemberContext.getId();
        log.info("获取最近会话，用户：{}", userId);
        AiChatResp resp = aiChatService.getRecentSession(userId);
        return new CommonResp<>(resp);
    }

    /**
     * 新建会话
     */
    @PostMapping("/create")
    public CommonResp<AiChatResp> createSession() {
        Long userId = LoginMemberContext.getId();
        log.info("新建会话，用户：{}", userId);
        AiChatResp resp = aiChatService.createSession(userId);
        return new CommonResp<>(resp);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    public CommonResp<AiChatResp> sendMessage(@Valid @RequestBody AiChatMessageReq req) {
        Long userId = LoginMemberContext.getId();
        log.info("发送消息，用户：{}，sessionUuid：{}", userId, req.getSessionUuid());
        AiChatResp resp = aiChatService.sendMessage(userId, req.getSessionUuid(), req.getContent());
        return new CommonResp<>(resp);
    }

    /**
     * 获取会话历史
     */
    @GetMapping("/history/{sessionUuid}")
    public CommonResp<AiChatResp> getSessionHistory(@PathVariable String sessionUuid) {
        Long userId = LoginMemberContext.getId();
        log.info("获取会话历史，用户：{}，sessionUuid：{}", userId, sessionUuid);
        AiChatResp resp = aiChatService.getSessionHistory(userId, sessionUuid);
        return new CommonResp<>(resp);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{sessionUuid}")
    public CommonResp<Void> deleteSession(@PathVariable String sessionUuid) {
        Long userId = LoginMemberContext.getId();
        log.info("删除会话，用户：{}，sessionUuid：{}", userId, sessionUuid);
        aiChatService.deleteSession(userId, sessionUuid);
        return new CommonResp<>();
    }
}
