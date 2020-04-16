package com.zhaodongdb.wireless.patternlocker;

import android.text.TextUtils;

import com.zhaodongdb.common.patternlocker.SecurityUtil;
import com.zhaodongdb.common.patternlocker.SharedPreferencesUtil;

import java.util.List;

/**
 * Created by hsg on 14/10/2017.
 */

public class PatternHelper {
    public static final int MAX_SIZE = 4;
    public static final int MAX_TIMES = 5;
    //private static final String GESTURE_PWD_KEY = "gesture_pwd_key";

    private String message;
    private String storagePwd;
    private String tmpPwd;
    private int times;
    private boolean isFinish;
    private boolean isOk;

    public String getGesture() {
        return storagePwd;
    }

    public void validateForSetting(List<Integer> hitIndexList) {
        this.isFinish = false;
        this.isOk = false;

        if ((hitIndexList == null) || (hitIndexList.size() < MAX_SIZE)) {
            this.tmpPwd = null;
            this.message = getSizeErrorMsg();
            return;
        }

        //1. draw first time
        if (TextUtils.isEmpty(this.tmpPwd)) {
            this.tmpPwd = convert2String(hitIndexList);
            this.message = getReDrawMsg();
            this.isOk = true;
            return;
        }

        //2. draw second times
        if (this.tmpPwd.equals(convert2String(hitIndexList))) {
            this.message = getSettingSuccessMsg();
            // 手势密码不再存储至本地
            // saveToStorage(this.tmpPwd);
            this.storagePwd = this.tmpPwd;
            this.isOk = true;
            this.isFinish = true;
        } else {
            this.tmpPwd = null;
            this.message = getDiffPreErrorMsg();
        }
    }

    // 此方法只校验用户连接的点是否满足最少点数
    // 校验分为两步，本地校验只校验是否满足最少点数，服务端校验看是否正确
    public void validateForChecking(List<Integer> hitIndexList) {
        this.isOk = false;

        if ((hitIndexList == null) || (hitIndexList.size() < MAX_SIZE)) {
            this.times++;
            this.isFinish = this.times >= MAX_SIZE;
            this.message = getSizeErrorMsg();
            return;
        }

        this.isOk = true;
        this.storagePwd = convert2String(hitIndexList);

// 手势密码不再进行本地校验
//        this.storagePwd = getFromStorage();
//        if (!TextUtils.isEmpty(this.storagePwd) && this.storagePwd.equals(convert2String(hitIndexList))) {
//            this.message = getCheckingSuccessMsg();
//            this.isOk = true;
//            this.isFinish = true;
//        } else {
//            this.times++;
//            this.isFinish = this.times >= MAX_SIZE;
//            this.message = getPwdErrorMsg();
//        }
    }

    public void validateForChecking(Boolean isOk) {
        this.isOk = isOk;
        if (isOk) {
            this.message = getCheckingSuccessMsg();
            this.isFinish = true;
        } else {
            this.times++;
            this.isFinish = this.times >= MAX_SIZE;
            this.message = getPwdErrorMsg();
        }
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isOk() {
        return isOk;
    }

    private String getReDrawMsg() {
        return "请再次绘制解锁图案";
    }

    private String getSettingSuccessMsg() {
        return "手势解锁图案设置成功！";
    }

    private String getCheckingSuccessMsg() {
        return "解锁成功！";
    }

    private String getSizeErrorMsg() {
        return String.format("至少连接个%d点，请重新绘制", MAX_SIZE);
    }

    private String getDiffPreErrorMsg() {
        return "与上次绘制不一致，请重新绘制";
    }

    private String getPwdErrorMsg() {
        return String.format("密码错误，还剩%d次机会", getRemainTimes());
    }

    private String convert2String(List<Integer> hitIndexList) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : hitIndexList) {
            sb.append(i);
        }
        return sb.toString();
    }

    // 手势密码不再存储至本地，而是存储在服务端
//    private void saveToStorage(String gesturePwd) {
//        final String encryptPwd = SecurityUtil.encrypt(gesturePwd);
//        SharedPreferencesUtil.getInstance().saveString(GESTURE_PWD_KEY, encryptPwd);
//    }
//
//    private String getFromStorage() {
//        final String result = SharedPreferencesUtil.getInstance().getString(GESTURE_PWD_KEY);
//        return SecurityUtil.decrypt(result);
//    }

    private int getRemainTimes() {
        return (times < 5) ? (MAX_TIMES - times) : 0;
    }
}
