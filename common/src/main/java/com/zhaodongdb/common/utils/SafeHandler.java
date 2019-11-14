package com.zhaodongdb.common.utils;

import android.os.Handler;
import android.os.Message;

import com.zhaodongdb.common.component.BaseActivity;

import java.lang.ref.WeakReference;

public class SafeHandler extends Handler {

    private WeakReference<BaseActivity> self;

    public SafeHandler(BaseActivity activity) {
        this.self = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        self.get().safeHandleMessage(msg);
    }

}
