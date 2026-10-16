package com.music.api.client;

import com.music.common.req.FiletransQueryReq;
import com.music.common.req.FiletransSubtitleQueryReq;
import com.music.common.req.GenSubtitleReq;
import com.music.common.req.GenTextReq;
import com.music.common.resp.CommonResp;
import com.music.common.resp.FiletransQueryResp;
import com.music.common.resp.FiletransSubtitleQueryResp;
import com.music.common.resp.PageResp;
import com.music.api.client.fallback.AudioClientFallback;
import com.music.api.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 音频服务对外的 API Client，封装远程调用。
 *
 * 供订单服务、管理端等其他业务服务直接调用：
 * - 负责发起远程 HTTP 请求到 audio-service
 * - 上层只关心方法调用，不关心远程细节
 * - 通过 fallbackFactory = AudioClientFallback.class 在远程调用失败时给出友好提示
 */
@FeignClient(
        name = "audio-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = AudioClientFallback.class
)
public interface AudioClient {

    /**
     * 支付成功后回调，更新语音识别记录状态并发起识别任务。
     *
     * @param filetransId 语音识别记录ID
     */
    @PostMapping("/web/filetrans/after-pay-success")
    void afterPaySuccess(@RequestParam("filetransId") Long filetransId);

//order使用
//----------------------------------------------------------------------------------------------
//admin使用
    /**
     * 查询语音识别记录（admin管理端）。
     */
    @GetMapping("/web/filetrans/query")
    CommonResp<PageResp<FiletransQueryResp>> queryFiletrans(@SpringQueryMap FiletransQueryReq req);

    /**
     * 查询字幕明细。
     */
    @GetMapping("/web/filetrans-subtitle/query")
    CommonResp<PageResp<FiletransSubtitleQueryResp>> queryFiletransSubtitle(@SpringQueryMap FiletransSubtitleQueryReq req);

    /**
     * 生成字幕文件（.srt），返回可访问的 URL。
     */
    @GetMapping("/web/filetrans-subtitle/gen-subtitle")
    CommonResp<String> genSubtitle(@SpringQueryMap GenSubtitleReq req);

    /**
     * 生成纯文本文件（.txt），返回可访问的 URL。
     */
    @GetMapping("/web/filetrans-subtitle/gen-text")
    CommonResp<String> genText(@SpringQueryMap GenTextReq req);
}
