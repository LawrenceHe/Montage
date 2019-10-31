package com.zhaodongdb.common.component;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.picasso.Picasso;
import com.zhaodongdb.common.utils.DeviceUtil;
import com.zhaodongdb.common.utils.FoundationContextHolder;

public class BaseApplication extends Application {

    private static BaseApplication instance;
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        FoundationContextHolder.setApplication(instance);
        FoundationContextHolder.setContext(instance);

        Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());

        if (DeviceUtil.isApkDebugable()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(instance);
    }
}

