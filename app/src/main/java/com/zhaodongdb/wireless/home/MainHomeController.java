package com.zhaodongdb.wireless.home;

import android.content.Context;

import com.tmall.wireless.tangram.TangramEngine;
import com.zhaodongdb.wireless.R;

public class MainHomeController extends HomeController {

    public MainHomeController(Context context, String pageName, TangramEngine engine) {
        super(context, pageName, engine);
    }

    @Override
    protected String getTitle() {
        return getResources().getString(R.string.tab_home);
    }

//    @Override
//    protected ItemAdapter getItemAdapter() {
//        return new ItemAdapter(getContext(), QDDataManager.getInstance().getComponentsDescriptions());
//    }
}
