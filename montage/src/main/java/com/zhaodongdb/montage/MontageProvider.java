package com.zhaodongdb.montage;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhaodongdb.common.provider.IBaseProvider;

@Route(path = "/montage/provider")
public class MontageProvider implements IBaseProvider {

    @Override
    public void call() {
        Log.d("MontageProvider", "call ok");
    }

    @Override
    public void init(Context context) {
        Log.d("MontageProvider", "init ok");
    }
}
