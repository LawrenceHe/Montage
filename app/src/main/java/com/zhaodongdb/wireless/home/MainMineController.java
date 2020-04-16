package com.zhaodongdb.wireless.home;

import android.content.Context;

import com.tmall.wireless.tangram.TangramEngine;
import com.zhaodongdb.wireless.R;


public class MainMineController extends HomeController {

    public MainMineController(Context context, String pageName, TangramEngine engine) {
        super(context, pageName, engine);
    }

    @Override
    protected String getTitle() {
        return getResources().getString(R.string.tab_mine);
    }

//    @Override
//    protected ItemAdapter getItemAdapter() {
//        return new ItemAdapter(getContext(), QDDataManager.getInstance().getLabDescriptions());
//    }
}
