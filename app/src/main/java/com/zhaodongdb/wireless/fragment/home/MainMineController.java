package com.zhaodongdb.wireless.fragment.home;

import android.content.Context;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public class MainMineController extends HomeController {

    public MainMineController(Context context) {
        super(context);
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
