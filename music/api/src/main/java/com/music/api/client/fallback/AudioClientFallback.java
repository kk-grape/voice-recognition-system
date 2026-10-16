package com.music.api.client.fallback;

import com.music.api.client.AudioClient;
import com.music.common.req.FiletransQueryReq;
import com.music.common.req.FiletransSubtitleQueryReq;
import com.music.common.req.GenSubtitleReq;
import com.music.common.req.GenTextReq;
import com.music.common.resp.CommonResp;
import com.music.common.resp.FiletransQueryResp;
import com.music.common.resp.FiletransSubtitleQueryResp;
import com.music.common.resp.PageResp;
import org.springframework.cloud.openfeign.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 音频服务远程调用失败时的降级处理。
 *
 * 通过实现 {@link FallbackFactory}，可以拿到触发降级的异常信息，
 * 统一记录日志，给上层返回友好的提示，避免异常继续向外抛出。
 */
@Slf4j
@Component
public class AudioClientFallback implements FallbackFactory<AudioClient> {

    @Override
    public AudioClient create(Throwable cause) {
        // 记录远程调用失败原因，便于排查问题
        log.error("调用 audio-service 失败，触发 AudioClient 降级逻辑：{}", cause.getMessage(), cause);

        return new AudioClient() {

            @Override
            public void afterPaySuccess(Long filetransId) {
                // 支付成功后的业务一般可以做成“补偿型”，这里简单记录一下日志即可
                log.warn("afterPaySuccess 降级处理，filetransId={}，音频服务暂时不可用，将不再继续发起识别任务。", filetransId);
            }

            @Override
            public CommonResp<PageResp<FiletransQueryResp>> queryFiletrans(FiletransQueryReq req) {
                CommonResp<PageResp<FiletransQueryResp>> resp = new CommonResp<>();
                resp.setSuccess(false);
                resp.setMessage("查询语音识别记录失败：音频服务暂时不可用，请稍后重试");
                return resp;
            }

            @Override
            public CommonResp<PageResp<FiletransSubtitleQueryResp>> queryFiletransSubtitle(FiletransSubtitleQueryReq req) {
                CommonResp<PageResp<FiletransSubtitleQueryResp>> resp = new CommonResp<>();
                resp.setSuccess(false);
                resp.setMessage("查询字幕明细失败：音频服务暂时不可用，请稍后重试");
                return resp;
            }

            @Override
            public CommonResp<String> genSubtitle(GenSubtitleReq req) {
                CommonResp<String> resp = new CommonResp<>();
                resp.setSuccess(false);
                resp.setMessage("生成字幕文件失败：音频服务暂时不可用，请稍后重试");
                return resp;
            }

            @Override
            public CommonResp<String> genText(GenTextReq req) {
                CommonResp<String> resp = new CommonResp<>();
                resp.setSuccess(false);
                resp.setMessage("生成文本文件失败：音频服务暂时不可用，请稍后重试");
                return resp;
            }
        };
    }
}

