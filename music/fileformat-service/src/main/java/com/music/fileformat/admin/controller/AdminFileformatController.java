package com.music.fileformat.admin.controller;

import com.music.common.resp.CommonResp;
import com.music.fileformat.admin.resp.DirInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * 管理员-文件格式转换管理接口
 * 路径：/admin/fileformat/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/fileformat")
public class AdminFileformatController {

    @Value("${music.dir}")
    private String musicDir;

    /**
     * 获取转换文件目录大小
     */
    @GetMapping("/dir-size")
    public CommonResp<Long> getDirSize() {
        File dir = new File(musicDir);
        long size = getFolderSize(dir);
        return new CommonResp<>(size);
    }

    /**
     * 获取转换文件目录信息
     */
    @GetMapping("/dir-info")
    public CommonResp<DirInfoResp> getDirInfo() {
        File dir = new File(musicDir);
        DirInfoResp resp = new DirInfoResp();
        resp.setPath(musicDir);
        resp.setTotalSize(getFolderSize(dir));
        resp.setFileCount(countFiles(dir));
        resp.setExists(dir.exists());
        return new CommonResp<>(resp);
    }

    /**
     * 计算文件夹大小
     */
    private long getFolderSize(File folder) {
        long size = 0;
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += getFolderSize(file);
                    }
                }
            }
        }
        return size;
    }

    /**
     * 统计文件数量
     */
    private int countFiles(File folder) {
        int count = 0;
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        count++;
                    } else if (file.isDirectory()) {
                        count += countFiles(file);
                    }
                }
            }
        }
        return count;
    }
}
