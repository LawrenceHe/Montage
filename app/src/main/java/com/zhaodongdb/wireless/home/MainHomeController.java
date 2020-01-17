package com.zhaodongdb.wireless.home;

import android.content.Context;

import com.tmall.wireless.tangram.TangramEngine;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public class MainHomeController extends HomeController {

    public MainHomeController(Context context, String pageName, TangramEngine engine) {
        super(context, pageName, engine);
    }

    @Override
    protected String getTitle() {
        return "Components";
    }

//    @Override
//    protected ItemAdapter getItemAdapter() {
//        return new ItemAdapter(getContext(), QDDataManager.getInstance().getComponentsDescriptions());
//    }
}
