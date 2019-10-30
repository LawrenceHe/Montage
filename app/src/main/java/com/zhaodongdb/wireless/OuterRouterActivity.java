package com.zhaodongdb.wireless;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zhaodongdb.wireless.router.ZDRouter;

public class OuterRouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            ZDRouter.navigation(uri);
        }
        finish();
    }

}
