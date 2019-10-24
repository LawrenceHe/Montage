package com.zhaodongdb.wireless.router;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import java.net.URI;
import java.util.regex.PatternSyntaxException;

public class ZDRouter {

    public static void navigation(String url) {
        URI uri =  URI.create(url);
        Postcard postcard = ARouter.getInstance().build(uri.getPath());
        try {
            String[] query = uri.getQuery().split("&");
            for (String q: query) {
                String[] i = q.split("=");
                postcard.withString(i[0], i[1]);
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }

        postcard.navigation();
    }

}
