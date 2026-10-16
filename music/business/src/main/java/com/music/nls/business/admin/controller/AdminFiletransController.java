package com.music.nls.business.admin.controller;

import com.music.nls.business.audio.req.FiletransQueryReq;
import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.audio.resp.FiletransQueryResp;
import com.music.nls.business.common.resp.PageResp;
import com.music.nls.business.audio.service.FiletransService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/filetrans")
public class AdminFiletransController {

    @Resource
    private FiletransService filetransService;

    @GetMapping("/query")
    public CommonResp<PageResp<FiletransQueryResp>> query(@Valid FiletransQueryReq req)  {
        PageResp<FiletransQueryResp> pageResp = filetransService.query(req);
        return new CommonResp<>(pageResp);
    }
}
