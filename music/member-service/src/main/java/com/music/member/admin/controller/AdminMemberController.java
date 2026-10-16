package com.music.member.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.common.resp.CommonResp;
import com.music.common.resp.PageResp;
import com.music.member.admin.req.AdminMemberQueryReq;
import com.music.member.admin.resp.AdminMemberResp;
import com.music.member.admin.req.AdminMemberUpdateReq;
import com.music.member.admin.resp.MemberCountResp;
import com.music.member.domain.Member;
import com.music.member.domain.MemberExample;
import com.music.member.enums.MemberLevelEnum;
import com.music.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 管理员-会员管理接口
 * 路径：/admin/member/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Resource
    private MemberMapper memberMapper;

    /**
     * 查询会员列表（管理员版本）
     */
    @GetMapping("/list")
    public CommonResp<PageResp<AdminMemberResp>> list(AdminMemberQueryReq req) {
        MemberExample example = new MemberExample();
        MemberExample.Criteria criteria = example.createCriteria();

        // 搜索条件
        if (StringUtils.hasText(req.getMobile())) {
            criteria.andMobileLike("%" + req.getMobile() + "%");
        }
        if (StringUtils.hasText(req.getNickname())) {
            criteria.andNicknameLike("%" + req.getNickname() + "%");
        }
        if (req.getMemberLevel() != null) {
            criteria.andMemberLevelEqualTo(req.getMemberLevel());
        }

        example.setOrderByClause("id desc");

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Member> list = memberMapper.selectByExample(example);

        PageResp<AdminMemberResp> pageResp = new PageResp<>();
        PageInfo<Member> pageInfo = new PageInfo<>(list);
        pageResp.setTotal(pageInfo.getTotal());

        // 复制属性并设置会员等级名称
        List<AdminMemberResp> respList = BeanUtil.copyToList(list, AdminMemberResp.class);
        for (AdminMemberResp resp : respList) {
            MemberLevelEnum levelEnum = MemberLevelEnum.getByCode(resp.getMemberLevel());
            if (levelEnum != null) {
                resp.setMemberLevelName(levelEnum.getName());
            }
            // 判断会员状态
            if (resp.getMemberLevel() != null && resp.getMemberLevel() > 0) {
                if (resp.getMemberExpireAt() != null && resp.getMemberExpireAt().after(new Date())) {
                    resp.setMemberStatus(1); // 有效
                } else {
                    resp.setMemberStatus(2); // 已过期
                }
            } else {
                resp.setMemberStatus(0); // 非会员
            }
        }
        pageResp.setList(respList);

        return new CommonResp<>(pageResp);
    }

    /**
     * 查看会员详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<AdminMemberResp> detail(@PathVariable Long id) {
        Member member = memberMapper.selectByPrimaryKey(id);
        if (member == null) {
            return new CommonResp<>();
        }
        AdminMemberResp resp = new AdminMemberResp();
        BeanUtil.copyProperties(member, resp);
        MemberLevelEnum levelEnum = MemberLevelEnum.getByCode(member.getMemberLevel());
        if (levelEnum != null) {
            resp.setMemberLevelName(levelEnum.getName());
        }
        return new CommonResp<>(resp);
    }

    /**
     * 修改会员状态（启用/禁用）
     */
    @PutMapping("/status/{id}")
    public CommonResp<Void> updateStatus(@PathVariable Long id, @RequestParam Integer memberLevel) {
        log.info("管理员修改会员等级，id={}, memberLevel={}", id, memberLevel);
        Member member = new Member();
        member.setId(id);
        member.setMemberLevel(memberLevel);
        member.setUpdatedAt(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
        return new CommonResp<>();
    }

    /**
     * 调整会员到期时间
     */
    @PutMapping("/expire/{id}")
    public CommonResp<Void> updateExpire(@PathVariable Long id, @RequestParam Date memberExpireAt) {
        log.info("管理员调整会员到期时间，id={}, memberExpireAt={}", id, memberExpireAt);
        Member member = new Member();
        member.setId(id);
        member.setMemberExpireAt(memberExpireAt);
        member.setUpdatedAt(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
        return new CommonResp<>();
    }

    /**
     * 修改会员信息
     */
    @PutMapping("/update/{id}")
    public CommonResp<Void> update(@PathVariable Long id, @RequestBody AdminMemberUpdateReq req) {
        log.info("管理员修改会员信息，id={}", id);
        Member member = new Member();
        member.setId(id);
        if (req.getName() != null) {
            member.setName(req.getName());
        }
        if (req.getNickname() != null) {
            member.setNickname(req.getNickname());
        }
        if (req.getEmail() != null) {
            member.setEmail(req.getEmail());
        }
        if (req.getMemberRemark() != null) {
            member.setMemberRemark(req.getMemberRemark());
        }
        member.setUpdatedAt(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
        return new CommonResp<>();
    }

    /**
     * 重置会员密码
     */
    @PutMapping("/reset-password/{id}")
    public CommonResp<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        log.info("管理员重置会员密码，id={}", id);
        password = (DigestUtil.md5Hex(password.toLowerCase()));//密码加密
        Member member = new Member();
        member.setId(id);
        member.setPassword(password);
        member.setUpdatedAt(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
        return new CommonResp<>();
    }

    /**
     * 统计会员总数
     */
    @GetMapping("/count")
    public CommonResp<Long> count() {
        long count = memberMapper.countByExample(new MemberExample());
        return new CommonResp<>(count);
    }

    /**
     * 按会员等级统计
     */
    @GetMapping("/count-by-level")
    public CommonResp<List<MemberCountResp>> countByLevel() {
        List<MemberCountResp> result = memberMapper.countByLevel();
        return new CommonResp<>(result);
    }
}
