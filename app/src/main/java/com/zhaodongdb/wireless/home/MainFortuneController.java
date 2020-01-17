package com.zhaodongdb.wireless.home;

import android.content.Context;

import com.tmall.wireless.tangram.TangramEngine;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public class MainFortuneController extends HomeController {

    public MainFortuneController(Context context, String pageName, TangramEngine engine) {
        super(context, pageName, engine);
    }

    @Override
    protected String getTitle() {
        return "Lab";
    }

//    @Override
//    protected ItemAdapter getItemAdapter() {
//        return new ItemAdapter(getContext(), QDDataManager.getInstance().getLabDescriptions());
//    }
}
