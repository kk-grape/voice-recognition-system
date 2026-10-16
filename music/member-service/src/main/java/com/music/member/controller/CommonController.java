package com.music.member.controller;


import com.music.common.resp.CommonResp;
import com.music.common.util.AliOssUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/web/common")
@Slf4j
public class CommonController {

    @Resource
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public CommonResp<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file);

        try {
            //获取文件原始名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成随机文件名
            String objectName = UUID.randomUUID().toString() + suffix;

            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return new CommonResp<>(filePath);
        } catch (IOException e) {
            log.info("文件上传失败: {}", e);
        }

        CommonResp<String> resp = new CommonResp<>();
        resp.setSuccess(false);
        resp.setMessage("上传失败");
        return resp;

    }
}
