package com.music.nls.business.fileformat.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件格式转换服务接口
 */
public interface FileFormatService {

    /**
     * 转换音频文件格式
     * 
     * @param file         输入文件
     * @param inputFormat  输入格式
     * @param outputFormat 输出格式
     * @param bitrate      比特率
     * @param samplerate   采样率
     * @param channels     声道数
     * @return 转换后的文件
     */
    File convertAudioFile(MultipartFile file, String inputFormat, String outputFormat,
            String bitrate, String samplerate, String channels) throws Exception;

}
