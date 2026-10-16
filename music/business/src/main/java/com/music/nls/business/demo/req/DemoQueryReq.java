package com.music.nls.business.demo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DemoQueryReq {

    @NotBlank(message = "【手机号】不能为空")
    private String mobile;
}
