package com.zhaodongdb.wireless.login;

public class LoginRegisterRespData {

    private String verifyUserId;
    private String userName;
    private Boolean hasSetGesture;

    public String getVerifyUserId() {
        return verifyUserId;
    }

    public void setVerifyUserId(String verifyUserId) {
        this.verifyUserId = verifyUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getHasSetGesture() {
        return hasSetGesture;
    }

    public void setHasSetGesture(Boolean hasSetGesture) {
        this.hasSetGesture = hasSetGesture;
    }
}
