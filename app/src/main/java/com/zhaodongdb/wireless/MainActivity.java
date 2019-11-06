package com.zhaodongdb.wireless;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.zhaodongdb.wireless.fragment.home.MainFragment;

@FirstFragments(
        value = {
                MainFragment.class,
        })
@DefaultFirstFragment(MainFragment.class)
@LatestVisitRecord
public class MainActivity extends QMUIFragmentActivity {

    @Override
    protected int getContextViewId() {
        return R.id.main_page_root_view;
    }

    private final String TAG = MainActivity.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
