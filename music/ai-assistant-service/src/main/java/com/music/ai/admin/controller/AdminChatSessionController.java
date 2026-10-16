package com.music.ai.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.ai.admin.req.AdminChatSessionQueryReq;
import com.music.ai.admin.resp.AdminChatMessageResp;
import com.music.ai.admin.resp.AdminChatSessionResp;
import com.music.ai.admin.resp.ChatSessionCountResp;
import com.music.ai.domain.AiChatSession;
import com.music.ai.domain.AiChatMessage;
import com.music.ai.mapper.AiChatSessionMapper;
import com.music.ai.mapper.AiChatMessageMapper;
import com.music.common.resp.CommonResp;
import com.music.common.resp.PageResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-AI小助手会话管理接口
 * 路径：/admin/ai/chat/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/ai/chat")
public class AdminChatSessionController {

    @Resource
    private AiChatSessionMapper aiChatSessionMapper;

    @Resource
    private AiChatMessageMapper aiChatMessageMapper;

    /**
     * 查询AI对话会话列表（管理员版本）
     * - 不限制用户ID，可查看所有记录
     * - 支持按用户ID、状态、标题查询
     */
    @GetMapping("/session/list")
    public CommonResp<PageResp<AdminChatSessionResp>> list(AdminChatSessionQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<AiChatSession> list = aiChatSessionMapper.selectListByCondition(
                req.getMemberId(),
                req.getStatus(),
                req.getTitle()
        );

        PageResp<AdminChatSessionResp> pageResp = new PageResp<>();
        PageInfo<AiChatSession> pageInfo = new PageInfo<>(list);
        pageResp.setTotal(pageInfo.getTotal());
        List<AdminChatSessionResp> respList = BeanUtil.copyToList(list, AdminChatSessionResp.class);
        pageResp.setList(respList);

        return new CommonResp<>(pageResp);
    }

    /**
     * 查看会话详情
     */
    @GetMapping("/session/detail/{id}")
    public CommonResp<AdminChatSessionResp> detail(@PathVariable Long id) {
        AiChatSession session = aiChatSessionMapper.selectByPrimaryKey(id);
        if (session == null) {
            return new CommonResp<>();
        }
        AdminChatSessionResp resp = new AdminChatSessionResp();
        BeanUtil.copyProperties(session, resp);
        return new CommonResp<>(resp);
    }

    /**
     * 查看会话消息列表
     */
    @GetMapping("/message/list/{sessionUuid}")
    public CommonResp<List<AdminChatMessageResp>> messageList(@PathVariable String sessionUuid) {
        List<AiChatMessage> messages = aiChatMessageMapper.selectBySessionUuid(sessionUuid);
        List<AdminChatMessageResp> respList = BeanUtil.copyToList(messages, AdminChatMessageResp.class);
        return new CommonResp<>(respList);
    }

    /**
     * 统计会话总数
     */
    @GetMapping("/session/count")
    public CommonResp<Long> count() {
        Long total = aiChatSessionMapper.countAll();
        return new CommonResp<>(total);
    }

    /**
     * 按状态统计会话数量
     */
    @GetMapping("/session/count-by-status")
    public CommonResp<List<ChatSessionCountResp>> countByStatus() {
        List<ChatSessionCountResp> result = aiChatSessionMapper.countByStatus();
        return new CommonResp<>(result);
    }
}
