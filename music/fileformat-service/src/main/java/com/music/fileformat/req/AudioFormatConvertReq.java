package com.music.fileformat.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 音频转换请求实体类
 */
@Data
public class AudioFormatConvertReq {
    
    /**
     * 待转换的文件列表（通常只包含一个文件）
     */
    private List<MultipartFile> files;
    
    /**
     * 输入格式
     */
    private String inputFormat;
    
    /**
     * 输出格式
     */
    private String outputFormat;
    
    /**
     * 比特率
     */
    private String bitrate;
    
    /**
     * 采样率
     */
    private String samplerate;
    
    /**
     * 声道数
     */
    private String channels;
    
    /**
     * 任务ID
     */
    private String taskId;

}