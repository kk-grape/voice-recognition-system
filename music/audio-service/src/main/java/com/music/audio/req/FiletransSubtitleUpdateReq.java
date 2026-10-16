package com.music.audio.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户在线编辑字幕时，前端提交的单条字幕更新请求
 */
@Data
public class FiletransSubtitleUpdateReq {

    /**
     * 字幕行主键ID
     */
    @NotNull(message = "字幕ID不能为空")
    private Long id;

    /**
     * 修改后的字幕文本
     */
    @NotNull(message = "字幕文本不能为空")
    private String text;
}

