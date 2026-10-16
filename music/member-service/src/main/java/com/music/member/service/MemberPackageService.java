package com.music.member.service;


import com.music.common.context.LoginMemberContext;
import com.music.common.exception.BusinessException;
import com.music.common.exception.BusinessExceptionEnum;
import com.music.member.domain.Member;
import com.music.member.domain.MemberPackage;
import com.music.member.domain.MemberPackageExample;
import com.music.member.mapper.MemberMapper;
import com.music.member.mapper.MemberPackageMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MemberPackageService {

    @Resource
    private MemberPackageMapper memberPackageMapper;

    @Resource
    private MemberMapper memberMapper;

    /**
     * 获取所有可用的会员套餐
     * @return 会员套餐列表
     */
    public List<MemberPackage> getAllAvailablePackages() {
        MemberPackageExample example = new MemberPackageExample();
        example.createCriteria().andStatusEqualTo(1); // 1表示启用状态
        example.setOrderByClause("level asc"); // 按等级升序排列
        return memberPackageMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 根据ID获取会员套餐详情
     * @param id 套餐ID
     * @return 会员套餐详情
     */
    public MemberPackage getPackageById(Long id) {
        return memberPackageMapper.selectByPrimaryKey(id);
    }

    /**
     * 开通会员
     * @param packageId 套餐ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void openMember(Long packageId) {
        Long memberId = LoginMemberContext.getId();

        // 获取套餐信息
        MemberPackage memberPackage = memberPackageMapper.selectByPrimaryKey(packageId);
        if (memberPackage == null || memberPackage.getStatus() != 1) {
            log.error("会员套餐不存在或已下架，会员ID：{}，套餐ID：{}", memberId, packageId);
        }

        // 获取会员信息
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_NOT_EXIST);
        }

        Date now = new Date();
        Date expireAt = member.getMemberExpireAt();

        // 计算新的过期时间
        Calendar calendar = Calendar.getInstance();

        // 如果会员已过期或没有会员资格，从现在开始计算
        if (expireAt == null || expireAt.before(now)) {
            calendar.setTime(now);
        } else {
            // 如果会员未过期，在现有过期时间基础上增加
            calendar.setTime(expireAt);
        }

        // 增加套餐天数
        calendar.add(Calendar.DAY_OF_MONTH, memberPackage.getDuration());
        Date newExpireAt = calendar.getTime();

        // 更新会员信息
        Member updateMember = new Member();
        updateMember.setId(memberId);
        updateMember.setMemberLevel(memberPackage.getLevel());
        updateMember.setMemberExpireAt(newExpireAt);
        updateMember.setMemberRemark("开通" + memberPackage.getName());
        updateMember.setUpdatedAt(now);

        memberMapper.updateByPrimaryKeySelective(updateMember);
    }
}
