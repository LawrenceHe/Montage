package com.zhaodongdb.common.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhaodongdb.common.utils.FoundationContextHolder;

public class UserInfo {

    private String userId;
    private String userName;
    private String accessToken;
    private String refreshToken;

    public String getUserId() {
        if (userId == null){
            userId = getUserInfoSP().getString("userId", "");
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        getUserInfoSP().edit().putString("userId", userId).apply();
    }

    public String getUserName() {
        if (userName == null) {
            userName = getUserInfoSP().getString("userName", "");
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        getUserInfoSP().edit().putString("userName", userName).apply();

    }

    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = getUserInfoSP().getString("accessToken", "");
        }
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        getUserInfoSP().edit().putString("accessToken", accessToken).apply();
    }

    public String getRefreshToken() {
        if (refreshToken == null) {
            refreshToken = getUserInfoSP().getString("refreshToken", "");
        }
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        getUserInfoSP().edit().putString("refreshToken", refreshToken).apply();
    }

    private UserInfo() {

    }

    private SharedPreferences getUserInfoSP() {
        return FoundationContextHolder.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
    }

    static private UserInfo instance = null;
    static public UserInfo getInstance() {
        if (instance == null) {
            synchronized (UserInfo.class) {
                if (instance == null) {
                    instance = new UserInfo();
                }
            }
        }
        return instance;
    }
}
