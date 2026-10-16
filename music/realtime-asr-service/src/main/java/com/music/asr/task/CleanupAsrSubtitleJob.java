package com.music.asr.task;

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

/**
 * 清理过期字幕文件的定时任务
 * 每天凌晨执行，清理超过1个月的字幕文件（.srt文件）
 */
@Slf4j
@Component
public class CleanupAsrSubtitleJob {

    @Autowired
    private TtsProperties ttsProperties;

    /**
     * 每天凌晨2点清理超过1个月的字幕文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cron() {
        try {
            MDC.put("LOG_ID", String.valueOf(System.currentTimeMillis()));
            log.info("清理过期字幕任务开始");

            long start = System.currentTimeMillis();
            int deletedCount = cleanupSubtitleFiles();
            log.info("清理过期字幕任务结束，耗时：{}毫秒，删除文件数：{}",
                    System.currentTimeMillis() - start, deletedCount);
        } catch (Exception e) {
            log.error("清理过期字幕任务异常", e);
        } finally {
            MDC.remove("LOG_ID");
        }
    }

    /**
     * 清理超过1个月的字幕文件
     * @return 删除的文件数量
     */
    private int cleanupSubtitleFiles() {
        String outputPath = ttsProperties.getOutputPath();
        if (outputPath == null || outputPath.isEmpty()) {
            log.warn("TTS输出路径未配置，跳过清理");
            return 0;
        }

        Path subtitleDir = Paths.get(outputPath, "subtitles");
        if (!Files.exists(subtitleDir)) {
            log.info("字幕目录不存在，跳过清理: {}", subtitleDir);
            return 0;
        }

        int deletedCount = 0;
        // 1个月前的时间戳
        Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        try (var stream = Files.list(subtitleDir)) {
            for (Path file : stream.toList()) {
                // 只处理 .srt 文件
                if (!Files.isRegularFile(file) || !file.toString().endsWith(".srt")) {
                    continue;
                }

                // 检查文件创建时间
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                Instant createTime = attrs.creationTime().toInstant();

                // 如果创建时间早于1个月前，删除文件
                if (createTime.isBefore(oneMonthAgo)) {
                    try {
                        Files.delete(file);
                        deletedCount++;
                        log.debug("删除过期字幕文件: {}", file.getFileName());
                    } catch (IOException e) {
                        log.warn("删除字幕文件失败: {}", file, e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("遍历字幕目录失败: {}", subtitleDir, e);
        }

        return deletedCount;
    }
}
