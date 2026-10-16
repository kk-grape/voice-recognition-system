package com.music.common.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 管理员登录响应类
 */
@Data
public class AdminLoginResp {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    private String loginName;

    private String token;
}
