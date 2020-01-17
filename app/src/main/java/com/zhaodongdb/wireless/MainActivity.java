package com.zhaodongdb.wireless;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.zhaodongdb.common.debug.FloatDebugView;
import com.zhaodongdb.common.router.ZDRouter;
import com.zhaodongdb.common.utils.DeviceUtil;
import com.zhaodongdb.wireless.home.MainFragment;

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
        if (DeviceUtil.isApkDebugable()) {
            initDebugView(this);
        }
    }

    private void initDebugView(final Activity activity) {
        ViewGroup group = (ViewGroup) activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        FloatDebugView debugView = new FloatDebugView(activity, R.drawable.icon_bug);
        debugView.setImageMargin(DeviceUtil.getScreenHeight() - DeviceUtil.getPixelFromDip(150)
                , DeviceUtil.getScreenWidth() - DeviceUtil.getPixelFromDip(50));
        group.addView(debugView, group.getChildCount());
        debugView.setOnOpenListener(new FloatDebugView.OnOpenListener() {
            @Override
            public void onOpen() {
                ZDRouter.navigation("/common/debug");
            }
        });
    }
}
