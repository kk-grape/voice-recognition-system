package com.music.asr.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.asr.admin.req.AdminAsrSessionQueryReq;
import com.music.asr.admin.resp.AdminAsrSessionResp;
import com.music.asr.admin.resp.SessionCountResp;
import com.music.asr.domain.AsrSession;
import com.music.asr.mapper.AsrSessionMapper;
import com.music.common.resp.CommonResp;
import com.music.common.resp.PageResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-实时语音识别会话管理接口
 * 路径：/admin/realtime/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/realtime")
public class AdminAsrSessionController {

    @Resource
    private AsrSessionMapper asrSessionMapper;

    /**
     * 查询实时识别会话列表（管理员版本）
     * - 不限制会员ID，可查看所有记录
     */
    @GetMapping("/session/list")
    public CommonResp<PageResp<AdminAsrSessionResp>> list(AdminAsrSessionQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());

        // 使用自定义条件查询方法
        List<AsrSession> list = asrSessionMapper.selectListByCondition(
                req.getMemberId(),
                req.getStatus(),
                req.getSourceLang(),
                req.getTargetLang()
        );

        PageInfo<AsrSession> pageInfo = new PageInfo<>(list);
        PageResp<AdminAsrSessionResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        List<AdminAsrSessionResp> respList = BeanUtil.copyToList(list, AdminAsrSessionResp.class);
        pageResp.setList(respList);

        return new CommonResp<>(pageResp);
    }

    /**
     * 查看会话详情
     */
    @GetMapping("/session/detail/{id}")
    public CommonResp<AdminAsrSessionResp> detail(@PathVariable Long id) {
        AsrSession session = asrSessionMapper.selectByPrimaryKey(id);
        if (session == null) {
            return new CommonResp<>();
        }
        AdminAsrSessionResp resp = new AdminAsrSessionResp();
        BeanUtil.copyProperties(session, resp);
        return new CommonResp<>(resp);
    }

    /**
     * 统计会话总数
     */
    @GetMapping("/session/count")
    public CommonResp<Long> count() {
        Long count = asrSessionMapper.countAll();
        return new CommonResp<>(count);
    }

    /**
     * 按状态统计会话数量
     */
    @GetMapping("/session/count-by-status")
    public CommonResp<List<SessionCountResp>> countByStatus() {
        List<SessionCountResp> result = asrSessionMapper.countByStatus();
        return new CommonResp<>(result);
    }

    /**
     * 统计总时长
     */
    @GetMapping("/session/sum-duration")
    public CommonResp<Long> sumDuration() {
        Long total = asrSessionMapper.sumDuration();
        return new CommonResp<>(total);
    }
}
