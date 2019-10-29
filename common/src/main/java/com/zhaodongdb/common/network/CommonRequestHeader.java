package com.zhaodongdb.common.network;

import java.io.Serializable;
import java.util.Map;

public class CommonRequestHeader implements Serializable {

    // 用户登录态
    private String token;

    // APP版本号
    private Integer version;

    // 用户Id
    private String userId;

    /* 平台Id
     * a:android
     * i:ios
     * w:小程序
     * h:H5内页面
     * 这些值可以组合，例如ah指Android的H5内页面
     */
    private String platformId;

    // 用户设备Id
    private String deviceId;

    // 语言, cn: 中文, en: 英文
    private String lang;

    // 扩展字段
    private Map<String, String> ext;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Map<String, String> getExt() {
        return ext;
    }

    public void setExt(Map<String, String> ext) {
        this.ext = ext;
    }

}
