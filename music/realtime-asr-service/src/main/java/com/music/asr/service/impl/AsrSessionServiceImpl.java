package com.music.asr.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.music.asr.domain.AsrSession;
import com.music.asr.enums.AsrLangEnum;
import com.music.asr.enums.AsrSessionStatusEnum;
import com.music.asr.enums.TranslateLangEnum;
import com.music.asr.mapper.AsrSessionMapper;
import com.music.asr.service.AsrSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 实时语音识别会话相关的服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsrSessionServiceImpl implements AsrSessionService {

    private final AsrSessionMapper asrSessionMapper;

    // 创建会话
    @Override
    public Long createSession(String wsSessionId, Long memberId, String memberName) {
        AsrSession session = new AsrSession();
        session.setId(IdUtil.getSnowflakeNextId());
        session.setWsSessionId(wsSessionId);
        session.setMemberId(memberId);
        Date now = DateUtil.date();
        session.setStartTime(now);
        session.setStatus(AsrSessionStatusEnum.ACTIVE.getCode());
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        asrSessionMapper.insert(session);
        log.info("创建实时语音识别会话，id={}，wsSessionId={}", session.getId(), wsSessionId);
        return session.getId();
    }

    // 更新会话配置
    @Override
    public void updateConfig(Long sessionId, String sourceLang, String targetLang, Integer sampleRate, String format) {
        AsrSession session = asrSessionMapper.selectByPrimaryKey(sessionId);
        if (session == null) {
            return;
        }
        session.setSourceLang(sourceLang);
        session.setTargetLang(targetLang);
        session.setSourceLangDesc(getLanguageDesc(sourceLang));
        session.setTargetLangDesc(getLanguageDesc(targetLang));
        session.setSampleRate(sampleRate);
        session.setFormat(format);
        session.setUpdatedAt(DateUtil.date());
        asrSessionMapper.updateByPrimaryKey(session);
    }

    // 标记会话失败
    @Override
    public void markFailed(Long sessionId, String reason) {
        AsrSession session = asrSessionMapper.selectByPrimaryKey(sessionId);
        if (session == null) {
            return;
        }
        // 如果已经完成，就不要再被后续回调覆盖成 failed
        if (AsrSessionStatusEnum.COMPLETED.getCode().equals(session.getStatus())) {
            log.warn("会话已完成，忽略标记失败，id={}，reason={}", sessionId, reason);
            return;
        }
        session.setStatus(AsrSessionStatusEnum.FAILED.getCode());
        session.setEndTime(DateUtil.date());
        if (session.getStartTime() != null && session.getEndTime() != null) {
            long diffSec = (session.getEndTime().getTime() - session.getStartTime().getTime()) / 1000;
            session.setDurationSeconds((int) diffSec);
        }
        session.setUpdatedAt(DateUtil.date());
        asrSessionMapper.updateByPrimaryKey(session);
        log.warn("会话标记为失败，id={}，reason={}", sessionId, reason);
    }

    // 结束会话
    @Override
    public void finishSession(Long sessionId) {
        AsrSession session = asrSessionMapper.selectByPrimaryKey(sessionId);
        if (session == null) {
            return;
        }
        // 幂等：已经 completed/failed 不重复更新
        if (AsrSessionStatusEnum.COMPLETED.getCode().equals(session.getStatus())
                || AsrSessionStatusEnum.FAILED.getCode().equals(session.getStatus())) {
            return;
        }
        session.setStatus(AsrSessionStatusEnum.COMPLETED.getCode());
        session.setEndTime(DateUtil.date());
        if (session.getStartTime() != null && session.getEndTime() != null) {
            long diffSec = (session.getEndTime().getTime() - session.getStartTime().getTime()) / 1000;
            session.setDurationSeconds((int) diffSec);
        }
        session.setUpdatedAt(DateUtil.date());
        asrSessionMapper.updateByPrimaryKey(session);
        log.info("会话完成，id={}", sessionId);
    }

    // 获取阿里云 token
    @Override
    public String getAliyunToken() {
        // TODO：目前实时识别通过 AliyunAsrEngineImpl 中的 NlsClient 获取 token
        // 这里保留接口，由 AliyunAsrEngineImpl 实现具体逻辑
        return null;
    }

    // 根据ID获取会话
    @Override
    public AsrSession getById(Long sessionId) {
        return asrSessionMapper.selectByPrimaryKey(sessionId);
    }

    // 根据语言代码获取语言描述
    public String getLanguageDesc(String langCode) {
        if (langCode == null) {
            return null;
        }
        // 尝试识别语言枚举
        try {
            AsrLangEnum asrLang = AsrLangEnum.valueOf(langCode.toUpperCase());
            return asrLang.getDesc();
        } catch (IllegalArgumentException e) {
            // 不是识别语言枚举，尝试翻译语言枚举
            try {
                TranslateLangEnum transLang = TranslateLangEnum.valueOf(langCode.toUpperCase());
                return transLang.getDesc();
            } catch (IllegalArgumentException ex) {
                return langCode;
            }
        }
    }
}
