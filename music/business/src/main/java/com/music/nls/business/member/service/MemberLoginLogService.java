package com.music.nls.business.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.music.nls.business.member.context.LoginMemberContext;
import com.music.nls.business.member.domain.MemberLoginLog;
import com.music.nls.business.member.domain.MemberLoginLogExample;
import com.music.nls.business.member.mapper.MemberLoginLogMapper;
import com.music.nls.business.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MemberLoginLogService {

    @Resource
    MemberLoginLogMapper memberLoginLogMapper;

    public void save(MemberLoginResp memberLoginResp) {
        log.info("增加会员登录日志：{}", memberLoginResp);
        Date now = new Date();
        MemberLoginLog memberLoginLog = new MemberLoginLog();
        memberLoginLog.setId(IdUtil.getSnowflakeNextId());
        memberLoginLog.setMemberId(memberLoginResp.getId());
        memberLoginLog.setLoginTime(now);
        memberLoginLog.setToken(memberLoginResp.getToken());
        memberLoginLog.setHeartCount(0);
        memberLoginLog.setLastHeartTime(now);
        memberLoginLogMapper.insert(memberLoginLog);
    }

    public void updateHeartInfo() {
        MemberLoginResp memberLoginResp = LoginMemberContext.getMember();
        String token = memberLoginResp.getToken();
        log.info("更新会员心跳信息：{}", token);
        MemberLoginLogExample memberLoginLogExample = new MemberLoginLogExample();
        memberLoginLogExample.createCriteria().andTokenEqualTo(token);
        memberLoginLogExample.setOrderByClause("id desc");
        List<MemberLoginLog> memberLoginLogList = memberLoginLogMapper.selectByExample(memberLoginLogExample);
        if (CollUtil.isEmpty(memberLoginLogList)) {
            log.warn("未找到该token的登录信息：{}，会员ID：{}", token, memberLoginResp.getId());
            save(memberLoginResp);
            return;
        }

        MemberLoginLog memberLoginLogDB = memberLoginLogList.get(0);
        memberLoginLogDB.setHeartCount(memberLoginLogDB.getHeartCount() + 1);
        memberLoginLogDB.setLastHeartTime(new Date());
        memberLoginLogMapper.updateByPrimaryKey(memberLoginLogDB);
    }
}
