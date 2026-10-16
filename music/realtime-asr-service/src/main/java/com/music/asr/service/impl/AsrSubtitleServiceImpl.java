package com.music.asr.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.music.asr.domain.AsrSubtitle;
import com.music.asr.enums.SubtitleTypeEnum;
import com.music.asr.enums.TranslateLangEnum;
import com.music.asr.mapper.AsrSubtitleMapper;
import com.music.asr.service.AsrSubtitleService;
import com.music.asr.translate.TranslateUtil;
import com.music.api.resp.AsrSubtitleResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsrSubtitleServiceImpl implements AsrSubtitleService {

    private final AsrSubtitleMapper asrSubtitleMapper;

    /**
     * 处理阿里云返回的 JSON，保存到数据库，并返回最终要展示给前端的一段文本。
     *
     * @param sessionId          业务会话ID
     * @param aliyunJson        阿里云返回的 JSON 字符串
     * @param translateLangEnum 翻译语言枚举（来自 TranslateLangEnum）
     * @param sentenceIndex      句子序号（从 1 开始递增）
     * @param startTimeMs        开始时间（毫秒）
     * @param endTimeMs          结束时间（毫秒）
     * @return 最终展示给前端的文本（可能是原文，或"原文 + 翻译"）
     */
    @Override
    public String handleAliyunResult(Long sessionId, String aliyunJson,
                                     TranslateLangEnum translateLangEnum, Integer sentenceIndex,
                                     Integer startTimeMs, Integer endTimeMs) {
        JSONObject json = JSON.parseObject(aliyunJson);

        // 兼容多种可能格式，尽量抽取出一段文本
        JSONObject payload = json.getJSONObject("payload");
        if (payload == null) {
            payload = json.getJSONObject("Payload");
        }
        String text = null;
        if (payload != null) {
            if (payload.containsKey("result")) {
                Object result = payload.get("result");
                if (result instanceof String) {
                    text = (String) result;
                } else if (result instanceof JSONObject) {
                    text = ((JSONObject) result).getString("text");
                }
            } else if (payload.containsKey("text")) {
                text = payload.getString("text");
            }
        }
        if (text == null) {
            text = json.getString("text");
        }

        if (text == null || text.isEmpty()) {
            return null;
        }

        // 保存字幕到数据库，包含句子序号
        AsrSubtitle subtitle = new AsrSubtitle();
        subtitle.setId(IdUtil.getSnowflakeNextId());
        subtitle.setSessionId(sessionId);
        subtitle.setSentenceIndex(sentenceIndex != null ? sentenceIndex : 0);
        subtitle.setOriginalText(text);
        subtitle.setStartTimeMs(startTimeMs);
        subtitle.setEndTimeMs(endTimeMs);

        // 调用翻译服务（如果有设置翻译语言枚举）
        if (translateLangEnum != null) {
            String translateLangCode = translateLangEnum.getCode();
            if (translateLangCode != null && !translateLangCode.isEmpty()) {
                String translatedText = TranslateUtil.translate(text, null, translateLangCode);
                subtitle.setTranslatedText(translatedText);
                log.info("翻译结果: {} -> {} (目标语言: {})", text, translatedText, translateLangEnum.getDesc());
            }
        }

        Date now = DateUtil.date();
        subtitle.setCreatedAt(now);
        subtitle.setUpdatedAt(now);
        asrSubtitleMapper.insert(subtitle);

        // 返回翻译结果（如果有的话），否则返回原文
        String resultText = subtitle.getTranslatedText();
        return resultText != null ? resultText : text;
    }

    /**
     * 根据会话ID获取字幕列表
     *
     * @param sessionId 会话ID
     * @return 字幕列表
     */
    @Override
    public List<AsrSubtitle> getSubtitlesBySessionId(Long sessionId) {
        return asrSubtitleMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<AsrSubtitleResp> getSubtitleDtoList(Long sessionId) {
        List<AsrSubtitle> subtitles = getSubtitlesBySessionId(sessionId);
        return subtitles.stream().map(s -> {
            AsrSubtitleResp r = new AsrSubtitleResp();
            r.setId(s.getId());
            r.setSessionId(s.getSessionId());
            r.setSentenceIndex(s.getSentenceIndex());
            r.setOriginalText(s.getOriginalText());
            r.setTranslatedText(s.getTranslatedText());
            r.setStartTimeMs(s.getStartTimeMs());
            r.setEndTimeMs(s.getEndTimeMs());
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 导出字幕文件（SRT格式）
     *
     * @param sessionId 会话ID
     * @param subtitleTypeEnum 字幕类型：原文或翻译
     * @return 字幕文本内容（SRT格式）
     */
    @Override
    public String exportSubtitle(Long sessionId, SubtitleTypeEnum subtitleTypeEnum) {
        List<AsrSubtitle> subtitles = getSubtitlesBySessionId(sessionId);
        if (subtitles == null || subtitles.isEmpty()) {
            log.warn("会话 {} 没有字幕数据", sessionId);
            return null;
        }

        boolean isOriginal = subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL;
        log.info("格式化字幕数据，sessionId={}，类型={}，总行数={}",
                sessionId, subtitleTypeEnum.getDesc(), subtitles.size());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subtitles.size(); i++) {
            AsrSubtitle item = subtitles.get(i);
            String text = isOriginal ? item.getOriginalText() : item.getTranslatedText();
            if (text == null || text.isEmpty()) {
                continue;
            }

            sb.append(i + 1);
            sb.append("\n");
            sb.append(convertMs(item.getStartTimeMs())).append(" --> ").append(convertMs(item.getEndTimeMs()));
            sb.append("\n");
            sb.append(text);
            sb.append("\n\n");
        }

        log.info("格式化字幕完成，字符数：{}", sb.length());
        return sb.toString();
    }

    /**
     * 毫秒转换为 SRT 时间格式（HH:mm:ss,SSS）
     */
    private String convertMs(Integer ms) {
        if (ms == null) {
            return "00:00:00,000";
        }
        int hours = ms / 3600000;
        int minutes = (ms % 3600000) / 60000;
        int seconds = (ms % 60000) / 1000;
        int millis = ms % 1000;
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis);
    }
}
