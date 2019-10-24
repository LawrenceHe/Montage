package com.zhaodongdb.wireless;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/montage/standard")
public class StandardMontageActivity extends AppCompatActivity {

    @Autowired
    String pageName;

    RecyclerView recyclerView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);

        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.main_view);

        Toast.makeText(this, pageName, Toast.LENGTH_SHORT).show();
    }
}
