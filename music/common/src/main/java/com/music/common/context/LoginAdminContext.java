package com.music.common.context;


import com.music.common.resp.AdminLoginResp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginAdminContext {

    private static ThreadLocal<AdminLoginResp> admin = new ThreadLocal<>();

    public static AdminLoginResp getAdmin() {
        return admin.get();
    }

    public static void setAdmin(AdminLoginResp admin) { LoginAdminContext.admin.set(admin); }

    public static void removeAdmin() { LoginAdminContext.admin.remove(); }

    public static Long getId() {
        try {
            return admin.get().getId();
        } catch (Exception e) {
            log.error("获取登录用户信息异常", e);
            throw e;
        }
    }

}
