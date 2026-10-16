package com.music.audio.admin.controller;

import com.music.audio.service.FiletransSubtitleService;
import com.music.common.req.FiletransSubtitleQueryReq;
import com.music.common.req.GenSubtitleReq;
import com.music.common.req.GenTextReq;
import com.music.common.resp.CommonResp;
import com.music.common.resp.FiletransSubtitleQueryResp;
import com.music.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员-音频字幕管理接口
 * 路径：/admin/audio/filetrans-subtitle/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/audio/filetrans-subtitle")
public class AdminFiletransSubtitleController {

    @Resource
    private FiletransSubtitleService filetransSubtitleService;

    @GetMapping("/query")
    public CommonResp<PageResp<FiletransSubtitleQueryResp>> query(@Valid FiletransSubtitleQueryReq req)  {
        PageResp<FiletransSubtitleQueryResp> pageResp = filetransSubtitleService.query(req);
        return new CommonResp<>(pageResp);
    }

    @GetMapping("/gen-subtitle")
    public CommonResp<String> genSubtitle(@Valid GenSubtitleReq req)  {
        String url = filetransSubtitleService.genSubtitle(req);
        return new CommonResp<>(url);
    }

    @GetMapping("/gen-text")
    public CommonResp<String> genText(@Valid GenTextReq req)  {
        String url = filetransSubtitleService.genText(req);
        return new CommonResp<>(url);
    }
}
