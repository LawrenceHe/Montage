package com.zhaodongdb.common.network;

import android.support.annotation.NonNull;

import com.zhaodongdb.common.config.AppConfig;

public enum RequestUrlsEnum {

    LOGIN_BY_MOBILE("MAPI/sign/loginByMobile.json"),
    LOGIN_MESSAGE_TOKEN("MAPI/sign/getMessageToken.json"),
    LOGIN_BY_GESTURE("MAPI/sign/loginByGesture.json"),

    //====================
    ENUM_END("");

    private String devUrl = "";
    private String sitUrl = "";
    private String uatUrl = "";
    private String prdUrl = "";

    static final String devDomain = "http://jen-nginx-98.jdfcloud.com/";
    static final String sitDomain = "http://jen-nginx-98.jdfcloud.com/";
    static final String uatDomain = "http://jen-nginx-98.jdfcloud.com/";
    static final String prdDomain = "http://jen-nginx-98.jdfcloud.com/";

    RequestUrlsEnum(@NonNull String method) {

        devUrl = devDomain + method;
        sitUrl = sitDomain + method;
        uatUrl = uatDomain + method;
        prdUrl = prdDomain + method;

    }

    public String getEnvUrl() {
        AppConfig.EnvType env = AppConfig.getEnv();
        if (env == AppConfig.EnvType.DEV) {
            return devUrl;
        } else if (env == AppConfig.EnvType.SIT) {
            return sitUrl;
        } else if (env == AppConfig.EnvType.UAT) {
            return uatUrl;
        } else if (env == AppConfig.EnvType.PRD) {
            return prdUrl;
        } else {
            return prdUrl;
        }
    }
}
