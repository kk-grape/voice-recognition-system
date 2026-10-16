package com.music.nls.business.common.alipay;

import lombok.Data;

@Data
public class AlipayInfoPayResp {

    private String orderNo;

    /**
     * 调用支付渠道的返回值
     */
    private String channelResult;
}
