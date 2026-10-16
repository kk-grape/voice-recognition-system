package com.music.nls.business.admin.controller;

import com.music.nls.business.audio.req.FiletransSubtitleQueryReq;
import com.music.nls.business.audio.req.GenSubtitleReq;
import com.music.nls.business.audio.req.GenTextReq;
import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.audio.resp.FiletransSubtitleQueryResp;
import com.music.nls.business.common.resp.PageResp;
import com.music.nls.business.audio.service.FiletransSubtitleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/filetrans-subtitle")
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
