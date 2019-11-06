package com.zhaodongdb.wireless;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.qmuiteam.qmui.arch.QMUILatestVisit;
import com.zhaodongdb.common.utils.PermissionHelper;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        requestAllPermissions();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = QMUILatestVisit.intentOfLatestVisit(LauncherActivity.this);
                if (intent == null) {
                    intent = new Intent(LauncherActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    private void requestAllPermissions() {
        PermissionHelper.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                false,
                new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionCallback(String[] permissions, PermissionHelper.PermissionResult[] grantResults) {
                        try {
                            if (grantResults != null) {
                                StringBuilder sb = new StringBuilder();
                                for (PermissionHelper.PermissionResult pResult : grantResults) {
                                    sb.append(pResult.grantResult);
                                    sb.append("--");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionsError(String errMsg, String[] permissions, PermissionHelper.PermissionResult[] grantResults) {
                    }
                });
    }
}
