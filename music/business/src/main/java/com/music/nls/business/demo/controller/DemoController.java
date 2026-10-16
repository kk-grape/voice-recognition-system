package com.music.nls.business.demo.controller;

import com.music.nls.business.demo.req.DemoQueryReq;
import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.demo.resp.DemoQueryResp;
import com.music.nls.business.demo.service.DemoService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {

    @Resource
    private DemoService demoService;


    //@Value("${test.name}")
    //@NacosValue(value = "${test.name:000}", autoRefreshed = true)
    private String testName;


    @GetMapping("/hello")
    public CommonResp<String> hello() {
//        return "Hello World!";
        return new CommonResp<>("Hello World!" + testName);
    }

    @GetMapping("/count")
    public CommonResp<Integer> count() {
//        return demoService.count();
        return new CommonResp<>(demoService.count());
    }

    @GetMapping("/query")
    public CommonResp<List<DemoQueryResp>> query(@Valid DemoQueryReq req) {
        List<DemoQueryResp> demoList = demoService.query(req);
//        CommonResp<List<Demo>> commonResp = new CommonResp<>();
//        commonResp.setContent(demoList);
//        return commonResp;

        return new CommonResp<>(demoList);
    }
}
