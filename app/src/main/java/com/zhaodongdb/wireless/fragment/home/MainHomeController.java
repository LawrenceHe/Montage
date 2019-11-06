package com.zhaodongdb.wireless.fragment.home;

import android.content.Context;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public class MainHomeController extends HomeController {

    public MainHomeController(Context context) {
        super(context);
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
