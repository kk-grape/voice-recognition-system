package com.music.asr.controller;

import com.music.asr.enums.SubtitleTypeEnum;
import com.music.asr.nls.TtsProperties;
import com.music.asr.req.TtsAudioReq;
import com.music.asr.req.TtsDownloadReq;
import com.music.asr.resp.TtsAudioResp;
import com.music.asr.service.AliyunTtsService;
import com.music.common.resp.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * 语音合成（TTS）接口
 */
@Slf4j
@RestController
@RequestMapping("/web/tts")
public class TtsController {

    @Autowired
    private AliyunTtsService aliyunTtsService;

    @Autowired
    private TtsProperties ttsProperties;

    /**
     * 合成会话音频
     */
    @PostMapping("/synthesize")
    public CommonResp<TtsAudioResp> synthesize(@RequestBody TtsAudioReq req) {
        log.info("TTS 合成音频请求，sessionId={}，subtitleTypeEnum={}", req.getSessionId(), req.getSubtitleType());

        // 参数校验
        if (req.getSessionId() == null || req.getSessionId().isEmpty()) {
            return new CommonResp<>(false, "会话ID不能为空", null);
        }

        // 将 String 类型的 sessionId 转换为 Long（避免 JavaScript 大数精度丢失）
        Long sessionId;
        try {
            sessionId = Long.parseLong(req.getSessionId());
        } catch (NumberFormatException e) {
            log.warn("会话ID格式错误: {}", req.getSessionId());
            return new CommonResp<>(false, "会话ID格式错误", null);
        }

        // 获取字幕类型
        SubtitleTypeEnum subtitleTypeEnum = SubtitleTypeEnum.getByCode(req.getSubtitleType());

        // 合成音频
        String audioPath = aliyunTtsService.synthesizeSessionAudio(sessionId, subtitleTypeEnum);

        // 返回结果
        TtsAudioResp resp = new TtsAudioResp(String.valueOf(sessionId), audioPath, subtitleTypeEnum.getCode());
        return new CommonResp<>(resp);
    }

    /**
     * 下载音频文件
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(TtsDownloadReq req) {
        log.info("TTS 下载音频请求，sessionId={}，type={}", req.getSessionId(), req.getType());

        // 获取字幕类型
        SubtitleTypeEnum subtitleTypeEnum = SubtitleTypeEnum.getByCode(req.getType());

        // 构建音频文件路径
        String outputPath = ttsProperties.getOutputPath();
        String format = ttsProperties.getFormat();
        String fileExtension = getFileExtension(format);

        // 根据类型获取文件名
        String fileName;
        if (subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL) {
            fileName = StringUtils.hasText(ttsProperties.getOriginalAudioFileName())
                    ? ttsProperties.getOriginalAudioFileName()
                    : "original_audio";
        } else {
            fileName = StringUtils.hasText(ttsProperties.getTranslatedAudioFileName())
                    ? ttsProperties.getTranslatedAudioFileName()
                    : "translated_audio";
        }

        String audioPath = outputPath + "\\" + req.getSessionId() + "\\" + fileName + "." + fileExtension;
        File audioFile = new File(audioPath);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName + "." + fileExtension);
        headers.setContentType(MediaType.parseMediaType("audio/" + fileExtension));
        headers.setContentLength(audioFile.length());

        Resource resource = new FileSystemResource(audioFile);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String format) {
        if (!StringUtils.hasText(format)) {
            return "wav";
        }
        String ext = format.toLowerCase();
        return "mp3".equals(ext) ? "mp3" : "wav";
    }

}
