package com.music.asr.service.impl;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;
import com.music.asr.domain.AsrSession;
import com.music.asr.domain.AsrSubtitle;
import com.music.asr.enums.AsrLangEnum;
import com.music.asr.enums.SubtitleTypeEnum;
import com.music.asr.nls.NlsProperties;
import com.music.asr.nls.TtsProperties;
import com.music.asr.service.AsrSessionService;
import com.music.asr.service.AsrSubtitleService;
import com.music.asr.service.AliyunTtsService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 阿里云 TTS 语音合成服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunTtsServiceImpl implements AliyunTtsService {

    private final NlsProperties nlsProperties;
    private final TtsProperties ttsProperties;
    private final AsrSessionService asrSessionService;
    private final AsrSubtitleService asrSubtitleService;

    private NlsClient nlsClient;

    @PostConstruct
    public void init() {
        try {
            AccessToken accessToken = new AccessToken(
                    nlsProperties.getAccessKeyId(),
                    nlsProperties.getAccessKeySecret()
            );
            accessToken.apply();
            log.info("获取阿里云 NLS Token 成功，过期时间: {}", accessToken.getExpireTime());

            nlsClient = new NlsClient(accessToken.getToken());
            log.info("TTS NlsClient 初始化成功");
        } catch (IOException e) {
            log.error("TTS NlsClient 初始化失败", e);
            throw new RuntimeException("TTS NlsClient 初始化失败", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (nlsClient != null) {
            nlsClient.shutdown();
            log.info("TTS NlsClient 已关闭");
        }
    }

    @Override
    public String synthesizeSessionAudio(Long sessionId, SubtitleTypeEnum subtitleTypeEnum) {
        // 获取配置
        String outputPath = ttsProperties.getOutputPath();
        String voice = ttsProperties.getVoice();
        Integer speechRate = ttsProperties.getSpeechRate();
        Integer pitchRate = ttsProperties.getPitchRate();
        Integer volume = ttsProperties.getVolume();
        String format = ttsProperties.getFormat();
        Integer sampleRate = ttsProperties.getSampleRate();

        // 获取会话信息，根据字幕类型决定使用哪个语言
        AsrSession session = asrSessionService.getById(sessionId);
        if (session == null) {
            log.warn("会话 {} 不存在", sessionId);
            throw new RuntimeException("会话 " + sessionId + " 不存在");
        }

        // 根据字幕类型决定使用哪个语言：原文用源语言，翻译用目标语言
        String nlsLanguage;
        if (subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL) {
            nlsLanguage = session.getSourceLang();
            log.info("合成原文音频，使用源语言: {}", nlsLanguage);
        } else {
            nlsLanguage = session.getTargetLang();
            log.info("合成翻译音频，使用目标语言: {}", nlsLanguage);
        }

        // 根据字幕类型获取最终文件名
        String finalFileName = getFinalFileName(subtitleTypeEnum);
        String fileExtension = getFileExtension(format);

        // 创建会话音频保存目录
        Path sessionDir = Paths.get(outputPath, sessionId.toString());
        try {
            Files.createDirectories(sessionDir);
        } catch (IOException e) {
            log.error("创建会话音频目录失败: {}", sessionDir, e);
            throw new RuntimeException("创建会话音频目录失败: " + sessionDir, e);
        }

        // 检查是否已有合并后的音频文件
        Path finalFilePath = sessionDir.resolve(finalFileName + "." + fileExtension);

        if (Files.exists(finalFilePath)) {
            log.info("会话 {} 的{}音频已存在，直接返回: {}", sessionId, subtitleTypeEnum.getDesc(), finalFilePath);
            return finalFilePath.toString();
        }

        // 获取该会话的所有字幕
        List<AsrSubtitle> subtitles = asrSubtitleService.getSubtitlesBySessionId(sessionId);
        if (subtitles == null || subtitles.isEmpty()) {
            log.warn("会话 {} 没有字幕数据", sessionId);
            throw new RuntimeException("会话 " + sessionId + " 没有字幕数据");
        }

        // 过滤空字幕并收集要合成的文本
        List<AsrSubtitle> validSubtitles = subtitles.stream()
                .filter(s -> {
                    String text = subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL ? s.getOriginalText() : s.getTranslatedText();
                    return StringUtils.hasText(text);
                })
                .collect(Collectors.toList());

        if (validSubtitles.isEmpty()) {
            log.warn("会话 {} 没有有效的字幕文本", sessionId);
            throw new RuntimeException("会话 " + sessionId + " 没有有效的字幕文本");
        }

        log.info("会话 {} 共 {} 条字幕需要合成音频", sessionId, validSubtitles.size());

        // 分段合成音频（阿里云限制每次最多300字符）
        List<Path> audioFiles = new ArrayList<>();
        int segmentIndex = 0;
        StringBuilder currentSegment = new StringBuilder();

        for (AsrSubtitle subtitle : validSubtitles) {
            String text = subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL ? subtitle.getOriginalText() : subtitle.getTranslatedText();

            // 如果加上这段文本超过300字符，先合成当前的
            if (currentSegment.length() + text.length() > 300) {
                if (currentSegment.length() > 0) {
                    // 合成当前段落
                    Path audioFile = synthesizeToFile(sessionDir, subtitleTypeEnum, nlsLanguage, segmentIndex++, currentSegment.toString(),
                            voice, speechRate, pitchRate, volume, format, sampleRate);
                    audioFiles.add(audioFile);
                    currentSegment = new StringBuilder();
                }
            }

            // 如果单个字幕就超过300字符，需要拆分
            if (text.length() > 300) {
                // 先合成累积的
                if (currentSegment.length() > 0) {
                    Path audioFile = synthesizeToFile(sessionDir, subtitleTypeEnum, nlsLanguage, segmentIndex++, currentSegment.toString(),
                            voice, speechRate, pitchRate, volume, format, sampleRate);
                    audioFiles.add(audioFile);
                    currentSegment = new StringBuilder();
                }
                // 拆分超长文本
                List<String> chunks = splitText(text, 300);
                for (String chunk : chunks) {
                    Path audioFile = synthesizeToFile(sessionDir, subtitleTypeEnum, nlsLanguage, segmentIndex++, chunk,
                            voice, speechRate, pitchRate, volume, format, sampleRate);
                    audioFiles.add(audioFile);
                }
            } else {
                currentSegment.append(text);
            }
        }

        // 处理最后一段
        if (currentSegment.length() > 0) {
            Path audioFile = synthesizeToFile(sessionDir, subtitleTypeEnum, nlsLanguage, segmentIndex++, currentSegment.toString(),
                    voice, speechRate, pitchRate, volume, format, sampleRate);
            audioFiles.add(audioFile);
        }

        // 使用 FFmpeg 合并音频文件
        if (audioFiles.size() == 1) {
            // 只有一个文件，直接重命名
            try {
                Files.move(audioFiles.get(0), finalFilePath);
            } catch (IOException e) {
                log.error("重命名音频文件失败", e);
                throw new RuntimeException("重命名音频文件失败", e);
            }
        } else {
            // 多个文件，合并
            mergeAudioFiles(audioFiles, finalFilePath);
        }

        log.info("会话 {} 的{}音频合成完成: {}", sessionId, subtitleTypeEnum.getDesc(), finalFilePath);
        return finalFilePath.toString();
    }

    /**
     * 根据字幕类型获取最终音频文件名
     */
    private String getFinalFileName(SubtitleTypeEnum subtitleTypeEnum) {
        if (subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL) {
            return StringUtils.hasText(ttsProperties.getOriginalAudioFileName())
                    ? ttsProperties.getOriginalAudioFileName()
                    : "original_audio";
        } else {
            return StringUtils.hasText(ttsProperties.getTranslatedAudioFileName())
                    ? ttsProperties.getTranslatedAudioFileName()
                    : "translated_audio";
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String format) {
        if (!StringUtils.hasText(format)) {
            return "wav";
        }
        String ext = format.toLowerCase();
        // mp3直接返回，其他统一返回wav
        return "mp3".equals(ext) ? "mp3" : "wav";
    }

    /**
     * 将文本合成音频并保存到文件
     */
    private Path synthesizeToFile(Path directory, SubtitleTypeEnum subtitleTypeEnum, String nlsLanguage, int index, String text,
                                  String voice, Integer speechRate, Integer pitchRate, Integer volume,
                                  String format, Integer sampleRate) {
        byte[] audioData = doSynthesize(text, nlsLanguage, voice, speechRate, pitchRate, volume, format, sampleRate);

        // 分段文件名包含字幕类型，避免原文和翻译音频冲突
        String prefix = subtitleTypeEnum == SubtitleTypeEnum.ORIGINAL ? "original" : "translated";
        String fileExtension = getFileExtension(format);
        String fileName = String.format("%s_segment_%03d.%s", prefix, index, fileExtension);
        Path filePath = directory.resolve(fileName);

        try {
            Files.write(filePath, audioData);
            log.debug("分段音频已保存: {}，大小: {} bytes", filePath, audioData.length);
        } catch (IOException e) {
            log.error("保存分段音频失败: {}", filePath, e);
            throw new RuntimeException("保存分段音频失败: " + filePath, e);
        }

        return filePath;
    }

    /**
     * 执行语音合成
     */
    private byte[] doSynthesize(String text, String nlsLanguage, String voice, Integer speechRate,
                                Integer pitchRate, Integer volume, String format, Integer sampleRate) {
        // 根据语言获取 AppKey
        String appKey = getAppKey(nlsLanguage);

        // 使用配置默认值
        voice = StringUtils.hasText(voice) ? voice : ttsProperties.getVoice();
        speechRate = speechRate != null ? speechRate : ttsProperties.getSpeechRate();
        pitchRate = pitchRate != null ? pitchRate : ttsProperties.getPitchRate();
        volume = volume != null ? volume : ttsProperties.getVolume();
        format = StringUtils.hasText(format) ? format : ttsProperties.getFormat();
        sampleRate = sampleRate != null ? sampleRate : (ttsProperties.getSampleRate() != null ? ttsProperties.getSampleRate() : 16000);

        // 用于存储音频数据的输出流
        ByteArrayOutputStream audioStream = new ByteArrayOutputStream();

        SpeechSynthesizer synthesizer = null;
        try {
            // 创建监听器
            SpeechSynthesizerListener listener = new SpeechSynthesizerListener() {
                @Override
                public void onMessage(ByteBuffer message) {
                    try {
                        byte[] bytes = new byte[message.remaining()];
                        message.get(bytes);
                        audioStream.write(bytes);
                    } catch (IOException e) {
                        log.error("写入音频数据失败", e);
                    }
                }

                @Override
                public void onComplete(SpeechSynthesizerResponse response) {
                    log.debug("TTS 合成完成，taskId={}", response.getTaskId());
                }

                @Override
                public void onFail(SpeechSynthesizerResponse response) {
                    log.error("TTS 合成失败，taskId={}，status={}，statusText={}",
                            response.getTaskId(), response.getStatus(), response.getStatusText());
                }
            };

            // 创建合成器
            synthesizer = new SpeechSynthesizer(nlsClient, listener);
            synthesizer.setAppKey(appKey);

            // 设置音频格式
            OutputFormatEnum outputFormat = "mp3".equalsIgnoreCase(format)
                    ? OutputFormatEnum.MP3
                    : OutputFormatEnum.WAV;
            synthesizer.setFormat(outputFormat);

            // 设置采样率
            synthesizer.setSampleRate(sampleRate == 8000
                    ? SampleRateEnum.SAMPLE_RATE_8K
                    : SampleRateEnum.SAMPLE_RATE_16K);

            // 设置发音人
            synthesizer.setVoice(voice);

            // 设置语速
            synthesizer.setSpeechRate(speechRate);

            // 设置语调
            synthesizer.setPitchRate(pitchRate);

            // 设置音量
            synthesizer.setVolume(volume);

            // 设置文本
            synthesizer.setText(text);

            // 启动合成
            synthesizer.start();

            // 等待完成
            synthesizer.waitForComplete();

            // 获取音频数据
            byte[] audioData = audioStream.toByteArray();
            if (audioData == null || audioData.length == 0) {
                throw new RuntimeException("TTS 合成返回音频数据为空");
            }

            log.info("TTS 合成成功，语言={}，文本长度={}，音频大小={} bytes", nlsLanguage, text.length(), audioData.length);
            return audioData;

        } catch (Exception e) {
            log.error("TTS 合成失败，语言={}，text={}", nlsLanguage, text, e);
            throw new RuntimeException("TTS 合成失败: " + e.getMessage(), e);
        } finally {
            if (synthesizer != null) {
                synthesizer.close();
            }
        }
    }

    /**
     * 根据阿里云语言代码获取 AppKey
     */
    private String getAppKey(String nlsLanguage) {
        AsrLangEnum langEnum = AsrLangEnum.getByNlsLanguage(nlsLanguage);
        if (langEnum == null) {
            log.warn("未找到语言配置 {}，使用默认 LANG0(普通话)", nlsLanguage);
            return AsrLangEnum.LANG0.getAppKey();
        }
        return langEnum.getAppKey();
    }

    /**
     * 使用 FFmpeg 合并多个音频文件
     */
    private void mergeAudioFiles(List<Path> audioFiles, Path outputFile) {
        try {
            // 创建临时文件列表
            Path listFile = outputFile.getParent().resolve("concat_list.txt");
            StringBuilder sb = new StringBuilder();
            for (Path file : audioFiles) {
                sb.append("file '").append(file.toAbsolutePath()).append("'\n");
            }
            Files.write(listFile, sb.toString().getBytes());

            // 执行 FFmpeg 合并
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y", "-f", "concat", "-safe", "0",
                    "-i", listFile.toAbsolutePath().toString(),
                    "-c", "copy",
                    outputFile.toAbsolutePath().toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 读取错误输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                StringBuilder errorMsg = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
                log.error("FFmpeg 合并失败，退出码: {}，错误信息: {}", exitCode, errorMsg);
                throw new RuntimeException("FFmpeg 合并失败，退出码: " + exitCode);
            }

            log.info("FFmpeg 合并完成: {}", outputFile);

            // 清理临时列表文件
            Files.deleteIfExists(listFile);

        } catch (Exception e) {
            log.error("合并音频文件失败", e);
            throw new RuntimeException("合并音频文件失败", e);
        }
    }

    /**
     * 将文本拆分成多个小块（按句子或逗号拆分）
     */
    private List<String> splitText(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        // 按句子结束符拆分
        String[] sentences = text.split("[。！？.!?\n]");
        StringBuilder current = new StringBuilder();

        for (String sentence : sentences) {
            if (current.length() + sentence.length() > maxLength) {
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    current = new StringBuilder();
                }
                // 如果单个句子就超长，按长度强制拆分
                if (sentence.length() > maxLength) {
                    int i = 0;
                    while (i < sentence.length()) {
                        int end = Math.min(i + maxLength, sentence.length());
                        chunks.add(sentence.substring(i, end));
                        i = end;
                    }
                } else {
                    current.append(sentence);
                }
            } else {
                current.append(sentence);
            }
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }
}
