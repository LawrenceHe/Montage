package com.zhaodongdb.common.router;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;

public class ZDRouter {

    public static void navigation(@NonNull String url) {
        Uri uri = Uri.parse(url);
        navigation(uri);
    }

    public static void navigation(@NonNull Uri uri) {
        ARouter.getInstance().build(uri).navigation();
    }

}
