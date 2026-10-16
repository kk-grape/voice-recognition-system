package com.music.asr.service;

import com.music.asr.domain.AsrSubtitle;
import com.music.asr.enums.SubtitleTypeEnum;
import com.music.asr.enums.TranslateLangEnum;
import com.music.api.resp.AsrSubtitleResp;

import java.util.List;

/**
 * 处理阿里云返回结果、生成字幕和翻译的服务。
 */
public interface AsrSubtitleService {

    /**
     * 处理阿里云返回的原始 JSON，保存到数据库，并返回最终要展示给前端的一段文本。
     *
     * @param sessionId        业务会话ID
     * @param aliyunJson      阿里云返回的 JSON 字符串
     * @param translateLangEnum 翻译语言枚举，可为 null
     * @param sentenceIndex    句子序号（从 1 开始递增）
     * @param startTimeMs      开始时间（毫秒）
     * @param endTimeMs        结束时间（毫秒）
     * @return 最终展示给前端的文本（可能是原文，或"原文 + 翻译"）
     */
    String handleAliyunResult(Long sessionId, String aliyunJson, TranslateLangEnum translateLangEnum,
                             Integer sentenceIndex, Integer startTimeMs, Integer endTimeMs);

    /**
     * 根据会话ID获取字幕列表
     *
     * @param sessionId 会话ID
     * @return 字幕列表
     */
    List<AsrSubtitle> getSubtitlesBySessionId(Long sessionId);

    /**
     * 根据会话ID获取字幕列表（转换为通用 DTO 格式，供远程调用返回）
     */
    List<AsrSubtitleResp> getSubtitleDtoList(Long sessionId);

    /**
     * 导出字幕文件（SRT格式）
     *
     * @param sessionId 会话ID
     * @param subtitleTypeEnum 字幕类型：原文或翻译
     * @return 字幕文本内容
     */
    String exportSubtitle(Long sessionId, SubtitleTypeEnum subtitleTypeEnum);
}
