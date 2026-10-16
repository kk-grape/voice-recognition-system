package com.music.nls.business.audio.req;

import com.music.nls.business.common.req.PageReq;
import lombok.Data;

@Data
public class FiletransSubtitleQueryReq extends PageReq {

    private Long filetransId;
}
