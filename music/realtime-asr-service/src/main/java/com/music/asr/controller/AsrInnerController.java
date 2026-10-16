package com.music.asr.controller;

import com.music.api.resp.AsrSubtitleResp;
import com.music.asr.service.AsrSubtitleService;
import com.music.common.resp.CommonResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供其他微服务内部调用的接口（不对外暴露）
 */
@Slf4j
@RestController
@RequestMapping("/inner/asr-subtitle")
@RequiredArgsConstructor
public class AsrInnerController {

    private final AsrSubtitleService asrSubtitleService;

    /**
     * 获取指定会话的字幕列表，供 ai-assistant-service 生成会议纪要使用
     *
     * @param asrSessionId ASR 会话ID
     * @return 字幕数据列表
     */
    @GetMapping("/list")
    public CommonResp<List<AsrSubtitleResp>> listSubtitles(@RequestParam("asrSessionId") Long asrSessionId) {
        log.info("内部接口：获取字幕列表，asrSessionId={}", asrSessionId);
        List<AsrSubtitleResp> subtitles = asrSubtitleService.getSubtitleDtoList(asrSessionId);
        return new CommonResp<>(subtitles);
    }
}
