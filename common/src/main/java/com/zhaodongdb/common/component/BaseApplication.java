package com.zhaodongdb.common.component;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.picasso.Picasso;
import com.zhaodongdb.common.utils.DeviceUtil;
import com.zhaodongdb.common.utils.FoundationContextHolder;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

public class BaseApplication extends Application {

    private static BaseApplication instance;
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        long startTime = System.currentTimeMillis();

        super.onCreate();
        instance = this;
        FoundationContextHolder.setApplication(instance);
        FoundationContextHolder.setContext(instance);
        // 初始化图片加载器
        Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());
        // 初始化路由器
        if (DeviceUtil.isApkDebugable()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(FoundationContextHolder.getApplication());
        // 初始化UI库
        QMUISwipeBackActivityManager.init(this);

        long duration = System.currentTimeMillis() - startTime;
        Log.d("BaseApplication", "app startup time:" + duration);
    }
}

