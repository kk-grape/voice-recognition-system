package com.music.nls.business.fileformat.service.impl;

import com.music.nls.business.fileformat.config.WebSocketHandler;
import com.music.nls.business.fileformat.service.FileFormatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件格式转换服务实现类
 */
@Service
@Slf4j
public class FileFormatServiceImpl implements FileFormatService {

    @Value("${music.dir}")
    private String tempDir;

    @Value("${filetrans.progress.min-time:3}")
    private int minProgressTime;

    @Value("${filetrans.progress.max-time:4}")
    private int maxProgressTime;

    @Override
    public File convertAudioFile(MultipartFile file, String inputFormat, String outputFormat,
            String bitrate, String samplerate, String channels) throws Exception {
        return convertAudioFile(file, inputFormat, outputFormat, bitrate, samplerate, channels, null);
    }

    /**
     * 转换音频文件格式（带进度推送）
     * 
     * @param file         输入文件
     * @param inputFormat  输入格式
     * @param outputFormat 输出格式
     * @param bitrate      比特率
     * @param samplerate   采样率
     * @param channels     声道数
     * @param taskId       任务ID
     * @return 转换后的文件
     */
    public File convertAudioFile(MultipartFile file, String inputFormat, String outputFormat,
            String bitrate, String samplerate, String channels, String taskId) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 确保临时目录存在
        File tempDirectory = new File(tempDir);
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                throw new IOException("无法创建临时目录: " + tempDir);
            }
        }

        // 根据任务ID创建文件夹，去掉下划线后面的数字部分
        String baseTaskId = taskId;
        if (taskId.contains("_")) {
            // 找到最后一个下划线的位置
            int lastUnderscoreIndex = taskId.lastIndexOf('_');
            // 检查下划线后面是否是数字
            String suffix = taskId.substring(lastUnderscoreIndex + 1);
            if (suffix.matches("\\d+")) {
                // 如果是数字，去掉下划线和数字部分
                baseTaskId = taskId.substring(0, lastUnderscoreIndex);
            }
        }
        File taskDirectory = new File(tempDir + File.separator + baseTaskId);
        if (!taskDirectory.exists()) {
            if (!taskDirectory.mkdirs()) {
                throw new IOException("无法创建任务目录: " + taskDirectory.getAbsolutePath());
            }
        }

        // 生成文件路径
        String originalFilename = file.getOriginalFilename();
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String inputFilePath = taskDirectory.getAbsolutePath() + File.separator + originalFilename;
        String outputFilePath = taskDirectory.getAbsolutePath() + File.separator + baseName + "." + outputFormat;

        log.info("保存文件到: {}", inputFilePath);

        // 保存输入文件
        File inputFile = new File(inputFilePath);
        file.transferTo(inputFile);

        // 构建 FFmpeg 命令
        List<String> command = new ArrayList<>();
        command.add("E:\\Plug-ins Files\\ffmpeg-7.0.2-essentials_build\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add(inputFilePath);
        command.add("-b:a");
        command.add(bitrate + "k");
        command.add("-ar");
        command.add(samplerate);
        command.add("-ac");
        command.add(channels);
        // 添加进度输出
        command.add("-progress");
        command.add("pipe:1");
        command.add(outputFilePath);

        // 执行 FFmpeg 转换
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        log.info("文件开始转换");

        // 启动模拟进度线程
        Thread progressThread = null;
        if (taskId != null) {
            progressThread = new Thread(() -> {
                try {
                    // 根据配置文件中的时间范围计算推送次数
                    int minSteps = minProgressTime * 10; // 最小时间（秒）* 10次/秒
                    int maxSteps = maxProgressTime * 10; // 最大时间（秒）* 10次/秒
                    int stepRange = maxSteps - minSteps + 1;
                    int totalSteps = (int) (Math.random() * stepRange) + minSteps; // 计算随机步数范围
                    // 计算每次的进度增量
                    double progressPerStep = 99.0 / totalSteps;

                    // 模拟进度从0%开始
                    double currentProgress = 0;
                    for (int i = 0; i < totalSteps; i++) {
                        // 计算当前进度
                        currentProgress += progressPerStep;
                        int progress = (int) Math.round(currentProgress);
                        // 确保进度不超过99%
                        if (progress > 99) {
                            progress = 99;
                        }
                        // 推送进度消息
                        pushProgressMessage(taskId, progress, originalFilename);
                        // 打印进度到控制台
                        // System.out.println("转换进度: " + progress + "% - " + originalFilename);
                        // 控制进度更新速度，每100毫秒更新一次
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    // 线程被中断，退出循环
                    // System.out.println("模拟进度线程被中断");
                    Thread.currentThread().interrupt();
                }
            });
            progressThread.start();
        }

        // 启动线程读取FFmpeg输出流，避免进程阻塞
        Thread outputReaderThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 静默读取，不记录日志，避免日志过多
                }
            } catch (IOException e) {
                // 静默处理，不影响主流程
            }
        });
        outputReaderThread.setDaemon(true); // 设为守护线程
        outputReaderThread.start();

        // 等待进程执行完成
        int exitCode = process.waitFor();

        // 中断模拟进度线程（如果存在）
        if (progressThread != null && progressThread.isAlive()) {
            progressThread.interrupt();
            try {
                // 等待线程中断完成
                progressThread.join(100); // 最多等待100毫秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 不再推送100%进度，直接在下面完成消息中处理
        // 删除原始文件，只保留转换后的文件
        // 直接使用之前定义的inputFile变量
        if (exitCode == 0) {
            log.info("FFmpeg 转换成功：{} -> {}", inputFilePath, outputFilePath);

            if (inputFile.exists()) {
                if (inputFile.delete()) {
                    log.debug("删除原始文件成功: {}", inputFilePath);
                } else {
                    log.warn("删除原始文件失败: {}", inputFilePath);
                }
            }

            // 推送完成消息
            if (taskId != null) {
                try {
                    pushCompletionMessage(taskId, originalFilename, outputFilePath);
                    log.debug("推送完成消息成功");
                } catch (Exception e) {
                    log.warn("推送完成消息失败", e);
                    // 即使推送失败，也不影响转换结果
                }
            }
        } else {
            log.error("FFmpeg 转换失败，退出码：{}", exitCode);

            if (inputFile.exists()) {
                if (inputFile.delete()) {
                    log.debug("删除原始文件成功: {}", inputFilePath);
                } else {
                    log.warn("删除原始文件失败: {}", inputFilePath);
                }
            }

            // 推送错误消息
            if (taskId != null) {
                try {
                    pushErrorMessage(taskId, originalFilename, "转换失败，退出码：" + exitCode);
                    log.debug("推送错误消息成功");
                } catch (Exception e) {
                    log.warn("推送错误消息失败", e);
                    // 即使推送失败，也不影响错误处理
                }
            }
            throw new Exception("文件转换失败");
        }

        return new File(outputFilePath);
    }

    /**
     * 推送进度消息
     *
     * @param taskId   任务ID
     * @param progress 进度百分比
     * @param fileName 文件名
     */
    private void pushProgressMessage(String taskId, int progress, String fileName) {
        try {
            // 构建进度消息
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("type", "progress");
            jsonObject.put("taskId", taskId);
            jsonObject.put("progress", progress);
            jsonObject.put("fileName", fileName);
            String message = jsonObject.toJSONString();
            // 推送消息给指定任务的客户端
            WebSocketHandler.sendToTask(taskId, message);
        } catch (Exception e) {
            log.error("推送进度消息失败", e);
        }
    }

    /**
     * 推送完成消息
     *
     * @param taskId         任务ID
     * @param fileName       文件名
     * @param outputFilePath 输出文件路径
     */
    private void pushCompletionMessage(String taskId, String fileName, String outputFilePath) {
        try {
            // 构建完成消息
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("type", "complete");
            jsonObject.put("taskId", taskId);
            jsonObject.put("fileName", fileName);
            jsonObject.put("downloadUrl", "/nls/web/fileformat/download?filePath=" + outputFilePath);
            String message = jsonObject.toJSONString();
            // 推送消息给指定任务的客户端
            WebSocketHandler.sendToTask(taskId, message);
        } catch (Exception e) {
            log.error("推送完成消息失败", e);
        }
    }

    /**
     * 解析时间字符串为秒数
     * 支持格式：HH:MM:SS.ms
     *
     * @param timeStr 时间字符串
     * @return 秒数
     */
    private double parseTimeString(String timeStr) {
        try {
            // 分割时间字符串
            String[] parts = timeStr.split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                double seconds = Double.parseDouble(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
            return 0;
        } catch (Exception e) {
            log.warn("解析时间字符串失败：{}", timeStr, e);
            return 0;
        }
    }

    /**
     * 解析键值对
     *
     * @param key              键
     * @param value            值
     * @param originalFilename 原始文件名
     * @param taskId           任务ID
     * @param duration         总时长
     * @param gotDuration      是否获取到总时长
     * @param currentTime      当前时间
     */
    private void parseKeyValuePair(String key, String value, String originalFilename, String taskId, double[] duration,
            boolean[] gotDuration, double[] currentTime) {
        // 解析总时长
        if ("duration".equals(key)) {
            try {
                if (!"N/A".equals(value)) {
                    duration[0] = Double.parseDouble(value);
                    gotDuration[0] = true;
                }
            } catch (NumberFormatException e) {
                // 静默处理，不打印日志
            }
        }
        // 解析当前时间（毫秒）
        else if ("out_time_ms".equals(key)) {
            try {
                if (!"N/A".equals(value)) {
                    // 转换为秒
                    currentTime[0] = Long.parseLong(value) / 1000.0;

                    // 计算进度并推送
                    if (gotDuration[0] && duration[0] > 0 && taskId != null) {
                        int progress = (int) ((currentTime[0] * 100) / duration[0]);
                        // 确保进度在0-100之间
                        progress = Math.min(99, Math.max(0, progress));
                        // 打印进度到控制台
                        System.out.println("转换进度: " + progress + "% - " + originalFilename);
                        // 推送到前端
                        pushProgressMessage(taskId, progress, originalFilename);
                    }
                }
            } catch (NumberFormatException e) {
                // 静默处理，不打印日志
            }
        }
        // 解析当前时间（HH:MM:SS.ms格式）
        else if ("out_time".equals(key)) {
            try {
                if (!"N/A".equals(value)) {
                    // 解析HH:MM:SS.ms格式的时间
                    currentTime[0] = parseTimeString(value);

                    // 计算进度并推送
                    if (gotDuration[0] && duration[0] > 0 && taskId != null) {
                        int progress = (int) ((currentTime[0] * 100) / duration[0]);
                        // 确保进度在0-100之间
                        progress = Math.min(99, Math.max(0, progress));
                        // 打印进度到控制台
                        System.out.println("转换进度: " + progress + "% - " + originalFilename);
                        // 推送到前端
                        pushProgressMessage(taskId, progress, originalFilename);
                    }
                }
            } catch (Exception e) {
                // 静默处理，不打印日志
            }
        }
    }

    /**
     * 推送错误消息
     *
     * @param taskId       任务ID
     * @param fileName     文件名
     * @param errorMessage 错误消息
     */
    private void pushErrorMessage(String taskId, String fileName, String errorMessage) {
        try {
            // 构建错误消息
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("type", "error");
            jsonObject.put("taskId", taskId);
            jsonObject.put("fileName", fileName);
            jsonObject.put("errorMessage", errorMessage);
            String message = jsonObject.toJSONString();
            // 推送消息给指定任务的客户端
            WebSocketHandler.sendToTask(taskId, message);
        } catch (Exception e) {
            log.error("推送错误消息失败", e);
        }
    }

}
