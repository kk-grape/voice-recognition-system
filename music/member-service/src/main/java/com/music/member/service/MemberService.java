package com.music.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.music.common.exception.BusinessException;
import com.music.common.exception.BusinessExceptionEnum;
import com.music.common.util.JwtUtil;
import com.music.common.context.LoginMemberContext;
import com.music.member.domain.Member;
import com.music.member.domain.MemberExample;
import com.music.member.enums.MemberLevelEnum;
import com.music.member.mapper.MemberMapper;
import com.music.member.req.MemberInfoUpdateReq;
import com.music.member.req.MemberLoginReq;
import com.music.member.req.MemberRegisterReq;
import com.music.member.req.MemberResetReq;
import com.music.member.resp.MemberInfoResp;
import com.music.common.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberLoginLogService memberLoginLogService;

    /**
     * 按手机号查会员信息
     * @param mobile
     * @return
     */
    public Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        MemberExample.Criteria criteria = memberExample.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void register(MemberRegisterReq req) {

        Date now= new Date();
        String mobile = req.getMobile();

        Member memberDB = selectByMobile(req.getMobile());
        if (memberDB != null){//会员必须为空
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_HAD_REGISTER);
        }

        Member member = new Member();
        member.setId(IdUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        member.setPassword(req.getPassword());
        member.setName("K哇"+mobile.substring(0, 3) + "****" + mobile.substring(7));
        member.setCreatedAt(now);
        member.setUpdatedAt(now);
        memberMapper.insert(member);

    }

    /**
     * 登录
     */
    public MemberLoginResp login(MemberLoginReq req) {
        Member memberDB = selectByMobile(req.getMobile());//按手机号查会员信息
        if (memberDB == null) {
            log.warn("手机号不存在，{}", req.getMobile());
            throw new BusinessException(BusinessExceptionEnum.MEMBER_LOGIN_ERROR);
        }

        if (memberDB.getPassword().equalsIgnoreCase(req.getPassword())) {
            log.info("登录成功，{}", req.getMobile());
            MemberLoginResp memberLoginResp = new MemberLoginResp();
            memberLoginResp.setId(memberDB.getId());
            memberLoginResp.setName(memberDB.getName());

            Map<String, Object> map = BeanUtil.beanToMap(memberLoginResp);
            String token = JwtUtil.createLoginToken(map);
            memberLoginResp.setToken(token);

            memberLoginLogService.save(memberLoginResp);

            return memberLoginResp;
        } else {
            log.warn("密码错误，{}", req.getMobile());
            throw new BusinessException(BusinessExceptionEnum.MEMBER_LOGIN_ERROR);
        }

    }


    /**
     * 重置密码
     */
    public void reset(MemberResetReq req) {
        Date now = new Date();
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        if (memberDB == null) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_REGISTER);
        }
        Member member = new Member();
        member.setId(memberDB.getId());
        member.setPassword(req.getPassword());
        member.setUpdatedAt(now);
        memberMapper.updateByPrimaryKeySelective(member);
    }

    /**
     * 获取会员信息
     */
    public MemberInfoResp getInfo() {
        Long memberId = LoginMemberContext.getId();
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_NOT_EXIST);
        }
        
        MemberInfoResp resp = new MemberInfoResp();
        BeanUtil.copyProperties(member, resp);
        
        // 设置会员等级名称
        MemberLevelEnum levelEnum = MemberLevelEnum.getByCode(member.getMemberLevel());
        if (levelEnum != null) {
            resp.setMemberLevelName(levelEnum.getName());
        }
        
        // 设置会员状态
        Integer memberStatus = 0; // 非会员
        if (member.getMemberLevel() != null && member.getMemberLevel() > 0) {
            if (member.getMemberExpireAt() != null && member.getMemberExpireAt().after(new Date())) {
                memberStatus = 1; // 会员有效期内
            } else {
                memberStatus = 2; // 会员已过期
            }
        }
        resp.setMemberStatus(memberStatus);
        
        return resp;
    }

    /**
     * 更新会员信息
     */
    public void updateInfo(MemberInfoUpdateReq req) {
        Long memberId = LoginMemberContext.getId();
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_NOT_EXIST);
        }
        
        Date now = new Date();
        Member updateMember = new Member();
        updateMember.setId(memberId);
        updateMember.setNickname(req.getNickname());
        updateMember.setAvatar(req.getAvatar());
        updateMember.setGender(req.getGender());
        updateMember.setBirthday(req.getBirthday());
        updateMember.setEmail(req.getEmail());
        updateMember.setBio(req.getBio());
        updateMember.setUpdatedAt(now);
        
        memberMapper.updateByPrimaryKeySelective(updateMember);
    }

}
