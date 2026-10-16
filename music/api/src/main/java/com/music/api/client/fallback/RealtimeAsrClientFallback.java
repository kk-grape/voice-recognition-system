package com.music.api.client.fallback;

import com.music.api.client.RealtimeAsrClient;
import com.music.api.resp.AsrSubtitleResp;
import com.music.common.resp.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RealtimeAsrClientFallback implements FallbackFactory<RealtimeAsrClient> {

    @Override
    public RealtimeAsrClient create(Throwable cause) {
        log.error("调用 realtime-asr-service 失败，触发降级：{}", cause.getMessage(), cause);

        return new RealtimeAsrClient() {
            @Override
            public CommonResp<List<AsrSubtitleResp>> listSubtitles(Long asrSessionId) {
                CommonResp<List<AsrSubtitleResp>> resp = new CommonResp<>();
                resp.setSuccess(false);
                resp.setMessage("获取字幕列表失败：实时语音识别服务暂时不可用");
                resp.setContent(Collections.emptyList());
                return resp;
            }
        };
    }
}
