package com.music.api.client;

import com.music.api.client.fallback.RealtimeAsrClientFallback;
import com.music.api.config.DefaultFeignConfig;
import com.music.api.resp.AsrSubtitleResp;
import com.music.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 实时语音识别服务对外的 API Client，封装远程调用。
 */
@FeignClient(
        name = "realtime-asr-service",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = RealtimeAsrClientFallback.class
)
public interface RealtimeAsrClient {

    /**
     * 获取指定会话的字幕列表
     *
     * @param asrSessionId ASR 会话ID
     * @return 字幕数据列表
     */
    @GetMapping("/inner/asr-subtitle/list")
    CommonResp<List<AsrSubtitleResp>> listSubtitles(@RequestParam("asrSessionId") Long asrSessionId);
}
