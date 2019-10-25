package com.zhaodongdb.wireless;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.picasso.Picasso;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.zhaodongdb.common.utils.DeviceUtil;
import com.zhaodongdb.common.utils.FoundationContextHolder;

public class ZhaodongDBApplication extends Application {

    private static ZhaodongDBApplication instance;
    public static ZhaodongDBApplication getInstance() {
        return instance;
    }

    private TangramBuilder.InnerBuilder montageBuilder;
    public TangramBuilder.InnerBuilder getMontageBuilder() {
        return montageBuilder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        FoundationContextHolder.setApplication(instance);
        FoundationContextHolder.setContext(instance);

        Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());
        final Context appContext = this.getApplicationContext();
        //Step 1: init tangram
        TangramBuilder.init(appContext, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view,
                                                                 @Nullable String url) {
                Picasso.with(appContext).load(url).into(view);
            }
        }, ImageView.class);
        //Step 2: register build=in cells and cards
        montageBuilder = TangramBuilder.newInnerBuilder(this);
        //Step 3: register business cells and cards
        //builder.registerVirtualView(templateName);

        if (DeviceUtil.isApkDebugable()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(instance); // 尽可能早，推荐在Application中初始化
    }
}
