package com.music.fileformat.admin.resp;

import lombok.Data;

/**
 * 目录信息响应类
 */
@Data
public class DirInfoResp {
    /**
     * 目录路径
     */
    private String path;
    /**
     * 目录大小
     */
    private long totalSize;
    /**
     * 文件数量
     */
    private int fileCount;
    /**
     * 目录是否存在
     */
    private boolean exists;
}
