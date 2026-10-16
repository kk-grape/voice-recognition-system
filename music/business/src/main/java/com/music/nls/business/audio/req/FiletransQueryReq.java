package com.music.nls.business.audio.req;

import com.music.nls.business.common.req.PageReq;
import lombok.Data;

@Data
public class FiletransQueryReq extends PageReq {

    private Long memberId;

    private String lang;

    private String status;

    private String name;
}
