package com.music.nls.business.admin.controller;

import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.admin.resp.StatisticResp;
import com.music.nls.business.admin.service.ReportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    /**
     * 首页数字统计
     */
    @GetMapping("/query-statistic")
    public CommonResp<StatisticResp> queryStatistic() {
        StatisticResp resp = reportService.queryStatistic();
        return new CommonResp<>(resp);
    }

}
