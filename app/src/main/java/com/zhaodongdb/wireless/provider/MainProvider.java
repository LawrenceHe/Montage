package com.zhaodongdb.wireless.provider;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhaodongdb.common.provider.IBaseProvider;

@Route(path = "/app/provider")
public class MainProvider implements IBaseProvider {

    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
        Log.d("MainProvider", "init ok");
    }

    @Override
    public void call() {
        Log.d("MainProvider", "call ok");
    }
}
