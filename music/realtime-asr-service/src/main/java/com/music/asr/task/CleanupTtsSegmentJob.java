package com.music.asr.task;

import cn.hutool.core.util.IdUtil;
import com.music.asr.nls.TtsProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * 清理 TTS 分段音频文件的定时任务
 * 每天零点执行，清理1小时前创建的分段音频文件
 */
@Slf4j
@Component
public class CleanupTtsSegmentJob {

    @Autowired
    private TtsProperties ttsProperties;

    /**
     * 每天零点清理1小时前的分段音频文件
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cron() {
        try {
            MDC.put("LOG_ID", IdUtil.getSnowflakeNextIdStr());
            log.info("清理TTS分段音频任务开始");

            long start = System.currentTimeMillis();
            int deletedCount = cleanupSegmentFiles();
            log.info("清理TTS分段音频任务结束，耗时：{}毫秒，删除文件数：{}", 
                    System.currentTimeMillis() - start, deletedCount);
        } catch (Exception e) {
            log.error("清理TTS分段音频任务异常", e);
        }
    }

    /**
     * 清理分段音频文件
     * @return 删除的文件数量
     */
    private int cleanupSegmentFiles() {
        String outputPath = ttsProperties.getOutputPath();
        if (outputPath == null || outputPath.isEmpty()) {
            log.warn("TTS输出路径未配置，跳过清理");
            return 0;
        }

        Path rootPath = Paths.get(outputPath);
        if (!Files.exists(rootPath)) {
            log.info("TTS输出目录不存在，跳过清理: {}", rootPath);
            return 0;
        }

        int deletedCount = 0;
        // 1小时前的时间戳
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

        try (Stream<Path> sessionDirs = Files.list(rootPath)) {
            for (Path sessionDir : sessionDirs.toList()) {
                if (!Files.isDirectory(sessionDir)) {
                    continue;
                }

                // 查找所有 segment_ 开头的文件
                try (Stream<Path> segmentFiles = Files.list(sessionDir)) {
                    for (Path segmentFile : segmentFiles.toList()) {
                        String fileName = segmentFile.getFileName().toString();
                        if (!fileName.startsWith("original_segment_") 
                                && !fileName.startsWith("translated_segment_")) {
                            continue;
                        }

                        // 检查文件创建时间
                        BasicFileAttributes attrs = Files.readAttributes(segmentFile, BasicFileAttributes.class);
                        Instant createTime = attrs.creationTime().toInstant();

                        // 如果创建时间早于1小时前，删除文件
                        if (createTime.isBefore(oneHourAgo)) {
                            try {
                                Files.delete(segmentFile);
                                deletedCount++;
                                log.debug("删除分段音频文件: {}", segmentFile);
                            } catch (IOException e) {
                                log.warn("删除分段音频文件失败: {}", segmentFile, e);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.warn("遍历会话目录失败: {}", sessionDir, e);
                }
            }
        } catch (IOException e) {
            log.error("遍历TTS输出目录失败: {}", rootPath, e);
        }

        return deletedCount;
    }
}
