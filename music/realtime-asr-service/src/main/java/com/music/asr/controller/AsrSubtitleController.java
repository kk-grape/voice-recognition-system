package com.music.asr.controller;

import com.music.asr.enums.SubtitleTypeEnum;
import com.music.asr.nls.TtsProperties;
import com.music.asr.req.AsrExportSubtitleReq;
import com.music.asr.service.AsrSubtitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

/**
 * 字幕相关接口
 */
@Slf4j
@RestController
@RequestMapping("/web/asr-subtitle")
public class AsrSubtitleController {

    @Autowired
    private AsrSubtitleService asrSubtitleService;

    @Autowired
    private TtsProperties ttsProperties;

    /**
     * 导出字幕文件（SRT格式）
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> export(AsrExportSubtitleReq req) {
        log.info("导出字幕请求，sessionId={}，type={}", req.getSessionId(), req.getType());

        // 参数校验
        if (req.getSessionId() == null || req.getSessionId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 将 String 类型的 sessionId 转换为 Long（避免 JavaScript 大数精度丢失）
        Long sessionId;
        try {
            sessionId = Long.parseLong(req.getSessionId());
        } catch (NumberFormatException e) {
            log.warn("会话ID格式错误: {}", req.getSessionId());
            return ResponseEntity.badRequest().build();
        }

        // 获取字幕类型
        SubtitleTypeEnum subtitleTypeEnum = SubtitleTypeEnum.getByCode(req.getType());

        // 构建文件名和路径
        String fileName = subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL ? "original" : "translated";
        String fullFileName = fileName + "_" + req.getSessionId() + ".srt";
        String tempPath = ttsProperties.getOutputPath() + "\\subtitles";
        File tempDir = new File(tempPath);
        File tempFile = new File(tempPath + "\\" + fullFileName);

        // 先检查文件是否已存在
        if (tempFile.exists()) {
            log.info("字幕文件已存在，直接返回: {}", tempFile.getAbsolutePath());
        } else {
            // 文件不存在，先查询数据库生成字幕内容
            String subtitleContent = asrSubtitleService.exportSubtitle(sessionId, subtitleTypeEnum);
            if (subtitleContent == null || subtitleContent.isEmpty()) {
                log.warn("未找到字幕数据，sessionId={}", sessionId);
                return ResponseEntity.notFound().build();
            }

            // 确保目录存在
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // 写入文件
            try {
                Files.writeString(tempFile.toPath(), subtitleContent);
                log.info("字幕文件已生成: {}", tempFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("写入字幕文件失败", e);
                return ResponseEntity.internalServerError().build();
            }
        }

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fullFileName);
        headers.setContentType(MediaType.parseMediaType("application/x-subrip"));

        Resource resource = new FileSystemResource(tempFile);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
