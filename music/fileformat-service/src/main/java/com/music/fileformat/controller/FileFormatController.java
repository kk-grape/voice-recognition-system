package com.music.fileformat.controller;


import com.music.fileformat.service.FileFormatService;
import com.music.fileformat.service.impl.FileFormatServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件格式转换控制器
 */
@RestController
@RequestMapping("/web/fileformat")
@Slf4j
public class FileFormatController {

    @Autowired
    private FileFormatService fileFormatService;

    @Value("${music.dir}")
    private String tempDir;

    /**
     * 单个文件转换接口
     */
    @PostMapping(value = "/convert-single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> convertSingleAudioFormat(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("inputFormat") String inputFormat,
            @RequestPart("outputFormat") String outputFormat,
            @RequestPart("bitrate") String bitrate,
            @RequestPart("samplerate") String samplerate,
            @RequestPart("channels") String channels,
            @RequestPart(value = "taskId", required = true) String taskId) throws Exception {
        if (files == null || files.isEmpty() || files.size() > 1) {
            return ResponseEntity.badRequest().build();
        }

        MultipartFile file = files.get(0);

        log.info("单个文件转换开始，文件名：{}，任务ID：{}", file.getOriginalFilename(), taskId);
        log.info("转换参数：{} -> {}, 比特率：{}kbps, 采样率：{}Hz, 声道：{}",
                inputFormat, outputFormat, bitrate, samplerate, channels);

        try {
            // 调用服务进行单个文件转换（带进度推送）
            File convertedFile = ((FileFormatServiceImpl) fileFormatService).convertAudioFile(
                    file, inputFormat, outputFormat, bitrate, samplerate, channels, taskId);

            log.info("文件转换结束");

            // 直接返回转换后的文件
            FileSystemResource resource = new FileSystemResource(convertedFile);

            HttpHeaders headers = new HttpHeaders();

            // 获取源文件名，保持原文件名不变，只修改后缀名
            String originalFilename = file.getOriginalFilename();
            String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            String fileName = baseName + "." + outputFormat;

            // 纯UTF-8 URL编码，仅处理中文/特殊字符，不碰后缀
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replace("%2E", "."); // 还原点，保证后缀识别

            // 标准化Content-Disposition响应头
            headers.clear();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFileName);
            // 可选：设置文件长度，让浏览器显示下载进度
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(convertedFile.length()));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            // 注意：现在不删除转换后的文件，只在批量下载后删除整个文件夹
            // 原始文件已经在转换过程中被删除了，所以这里不需要再删除

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } finally {
            // 注意：这里不直接删除文件，而是在返回结果后通过线程延迟删除
        }
    }

    /**
     * 批量下载接口 - 优化ZIP创建速度
     */
    @RequestMapping(value = "/download-batch", method = { RequestMethod.GET, RequestMethod.POST })
    public void downloadBatchFilesStream(
            HttpServletResponse response,
            @RequestParam("taskId") String taskId) throws Exception {
        log.info("批量下载请求，任务ID：{}", taskId);

        if (taskId == null || taskId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 根据任务ID找到对应文件夹
        File tempDirectory = new File(tempDir);
        File taskDirectory = new File(tempDirectory, taskId);

        if (!taskDirectory.exists() || !taskDirectory.isDirectory()) {
            log.error("任务目录不存在：{}", taskDirectory.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 从任务文件夹中获取所有转换后的文件
        List<File> files = new ArrayList<>();
        File[] fileArray = taskDirectory.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }

        if (files.isEmpty()) {
            log.warn("任务目录中没有文件：{}", taskDirectory.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        log.info("找到需要打包的文件数量：{}", files.size());

        // 创建zip文件 - 优化版本
        // 为每个请求生成唯一的文件名，避免多个下载请求相互干扰
        String zipFileName = "converted-files-" + System.currentTimeMillis() + ".zip";
        File zipFile = createZipFileOptimized(files, zipFileName);

        log.info("ZIP文件创建完成，大小：{} bytes", zipFile.length());

        // 设置响应头，启用流式传输
        response.setContentType("application/zip");
        // 响应头中使用固定的文件名，不包含时间戳，这样浏览器显示的文件名就是固定的
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''converted-files.zip");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(zipFile.length()));
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Accept-Ranges", "bytes");

        // 实现流式传输
        try (FileInputStream fis = new FileInputStream(zipFile);
                OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[250]; // 250字节缓冲区，控制下载速度在3-4MB/s左右
            int bytesRead;
            long totalRead = 0;
            long fileLength = zipFile.length();
            long lastLogTime = System.currentTimeMillis();

            log.info("开始传输文件，大小：{} bytes", fileLength);

            // 逐块读取并写入响应
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush(); // 刷新缓冲区，确保数据及时发送
                totalRead += bytesRead;

                // 每读取5MB记录一次日志，避免日志过多
                long currentTime = System.currentTimeMillis();
                if (totalRead % (5 * 1024 * 1024) == 0 && currentTime - lastLogTime > 1000) {
                    log.info("已传输：{} bytes / {} bytes ({:.2f}%)",
                            totalRead, fileLength, (totalRead * 100.0) / fileLength);
                    lastLogTime = currentTime;
                }
            }

            log.info("传输完成，总大小：{} bytes", fileLength);
        } catch (Exception e) {
            log.error("传输失败", e);
            throw e;
        } finally {
            // 在返回结果后，延迟删除zip文件，保留任务文件夹
            new Thread(() -> {
                try {
                    // 等待10秒，让前端有足够时间下载
                    Thread.sleep(10000);
                    // 删除zip文件
                    if (zipFile.exists()) {
                        if (zipFile.delete()) {
                            log.info("临时zip文件删除成功: {}", zipFile.getAbsolutePath());
                        } else {
                            log.warn("临时zip文件删除失败: {}", zipFile.getAbsolutePath());
                        }
                    }

                    // 保留任务文件夹和转换后的文件，以便用户可以重复下载
                    log.info("保留任务文件夹: {}", taskDirectory.getAbsolutePath());
                } catch (InterruptedException e) {
                    log.error("删除临时文件时线程被中断", e);
                }
            }).start();
        }
    }

    /**
     * 优化的ZIP文件创建方法
     */
    private File createZipFileOptimized(List<File> files, String zipFileName) throws Exception {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }

        // 使用@Value注解注入的tempDir属性
        String tempDirPath = tempDir;

        // 确保临时目录存在
        File tempDirectory = new File(tempDirPath);
        if (!tempDirectory.exists()) {
            if (!tempDirectory.mkdirs()) {
                throw new IOException("无法创建临时目录: " + tempDirectory.getAbsolutePath());
            }
        }

        // 生成zip文件路径
        String zipFilePath = tempDirectory.getAbsolutePath() + File.separator + zipFileName;
        File zipFile = new File(zipFilePath);

        log.info("开始创建zip文件: {}", zipFilePath);

        // 开始计时
        long startTime = System.currentTimeMillis();

        // 创建zip文件 - 使用更大的缓冲区和无压缩模式提高速度
        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

            // 设置无压缩模式，提高速度
            zos.setLevel(0); // 0 = 无压缩，最快

            byte[] buffer = new byte[65536]; // 64KB buffer，更大的缓冲区提高速度
            long totalFiles = files.size();
            long currentFile = 0;
            long totalSize = 0;

            for (File file : files) {
                currentFile++;
                log.info("添加文件 {}/{}: {}", currentFile, totalFiles, file.getName());

                if (!file.exists()) {
                    log.warn("文件不存在，跳过: {}", file.getAbsolutePath());
                    continue;
                }

                // 创建zip条目
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipEntry.setSize(file.length());
                zipEntry.setCompressedSize(file.length());
                zos.putNextEntry(zipEntry);

                // 写入文件内容
                try (FileInputStream fis = new FileInputStream(file)) {
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                }

                zos.closeEntry();
                totalSize += file.length();
                log.info("完成文件 {}/{}: {}, 累计大小: {} bytes",
                        currentFile, totalFiles, file.getName(), totalSize);
            }
        }

        // 计算耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("ZIP文件创建完成: {}, 大小: {} bytes, 耗时: {} ms",
                zipFilePath, zipFile.length(), duration);

        return zipFile;
    }
}
