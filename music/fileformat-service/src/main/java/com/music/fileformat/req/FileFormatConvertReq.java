package com.music.fileformat.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件格式转换请求参数实体类
 */
@Data
public class FileFormatConvertReq {
    
    /**
     * 上传的文件列表
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
     * 比特率（kbps）
     */
    private String bitrate;
    
    /**
     * 采样率（Hz）
     */
    private String samplerate;
    
    /**
     * 声道数
     */
    private String channels;
}
