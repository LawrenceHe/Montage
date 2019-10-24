package com.zhaodongdb.wireless;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.util.IInnerImageSetter;

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
    }
}
