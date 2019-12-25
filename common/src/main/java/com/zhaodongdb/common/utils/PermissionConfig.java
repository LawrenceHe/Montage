package com.zhaodongdb.common.utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class PermissionConfig {

    private static PermissionConfig instance;
    private PermissionConfigInterface config;

    public interface PermissionConfigInterface {
        void showPermissionDialog(String message, Activity activity, DialogInterface.OnClickListener cancleListener, DialogInterface.OnClickListener settingListener);
    }

    public static PermissionConfig instance() {
        if (instance == null) {
            instance = new PermissionConfig();
        }
        return instance;
    }

    public void config(PermissionConfigInterface config) {
        this.config = config;
    }

    public PermissionConfigInterface getConfig() {
        return config;
    }

    void showPermissionDialog(String message, Activity activity, DialogInterface.OnClickListener cancleListener, DialogInterface.OnClickListener settingListener) {
        if (config == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity).setMessage(message)
                    .setPositiveButton("设置", settingListener)
                    .setNegativeButton("取消", cancleListener);
            builder.create().show();
        } else {
            config.showPermissionDialog(message, activity, cancleListener, settingListener);
        }
    }
}
