package com.music.audio.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.audio.admin.resp.StatusCountResp;
import com.music.audio.domain.Filetrans;
import com.music.audio.domain.FiletransExample;
import com.music.audio.enums.FiletransStatusEnum;
import com.music.audio.mapper.FiletransMapper;
import com.music.common.req.FiletransQueryReq;
import com.music.common.resp.CommonResp;
import com.music.common.resp.FiletransQueryResp;
import com.music.common.resp.PageResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 管理员-语音识别管理接口
 * 路径：/admin/audio/filetrans/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/audio/filetrans")
public class AdminFiletransController {


    @Resource
    private FiletransMapper filetransMapper;

    /**
     * 查询语音识别记录列表（管理员版本）
     * - 不限制会员ID，可查看所有记录
     */
    @GetMapping("/query")
    public CommonResp<PageResp<FiletransQueryResp>> query(@ModelAttribute FiletransQueryReq req) {
        FiletransExample filetransExample = new FiletransExample();
        FiletransExample.Criteria criteria = filetransExample.createCriteria();

        // 管理员版本：不限制会员ID，可查看所有记录
        if (req.getMemberId() != null) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        if (req.getLang() != null && !req.getLang().isEmpty()) {
            criteria.andLangEqualTo(req.getLang());
        }
        if (req.getStatus() != null && !req.getStatus().isEmpty()) {
            criteria.andStatusEqualTo(req.getStatus());
        } else {
            // 默认排除已删除的记录
            criteria.andStatusNotEqualTo(FiletransStatusEnum.DELETED.getCode());
        }
        if (req.getName() != null && !req.getName().isEmpty()) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

        filetransExample.setOrderByClause("id desc");

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Filetrans> filetransList = filetransMapper.selectByExample(filetransExample);

        PageResp<FiletransQueryResp> pageResp = new PageResp<>();
        PageInfo<Filetrans> pageInfo = new PageInfo<>(filetransList);
        pageResp.setTotal(pageInfo.getTotal());
        List<FiletransQueryResp> list = BeanUtil.copyToList(filetransList, FiletransQueryResp.class);
        pageResp.setList(list);

        return new CommonResp<>(pageResp);
    }

    /**
     * 查看语音识别记录详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<FiletransQueryResp> detail(@PathVariable Long id) {
        Filetrans filetrans = filetransMapper.selectByPrimaryKey(id);
        if (filetrans == null) {
            return new CommonResp<>();
        }
        FiletransQueryResp resp = BeanUtil.copyToList(List.of(filetrans), FiletransQueryResp.class).get(0);
        return new CommonResp<>(resp);
    }

    /**
     * 删除语音识别记录（管理员操作）
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp<Void> delete(@PathVariable Long id) {
        log.info("管理员删除语音识别记录，id={}", id);
        Date now = new Date();
        Filetrans filetrans = new Filetrans();
        filetrans.setId(id);
        filetrans.setStatus(FiletransStatusEnum.DELETED.getCode());
        filetrans.setUpdatedAt(now);
        filetransMapper.updateByPrimaryKeySelective(filetrans);
        return new CommonResp<>();
    }

    /**
     * 统计语音识别记录数量
     */
    @GetMapping("/count")
    public CommonResp<Long> count() {
        FiletransExample example = new FiletransExample();
        long count = filetransMapper.countByExample(example);
        return new CommonResp<>(count);
    }

    /**
     * 根据状态统计数量
     */
    @GetMapping("/count-by-status")
    public CommonResp<List<StatusCountResp>> countByStatus() {
        List<StatusCountResp> result = filetransMapper.countByStatus();
        return new CommonResp<>(result);
    }
}
