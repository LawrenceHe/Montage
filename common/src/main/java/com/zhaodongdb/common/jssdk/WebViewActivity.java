package com.zhaodongdb.common.jssdk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.zhaodongdb.common.R;
import com.zhaodongdb.common.R2;
import com.zhaodongdb.common.component.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/common/webview")
public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    @BindView(R2.id.topbar)
    QMUITopBarLayout topBar;

    @BindView(R2.id.webview)
    BridgeWebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        webView.loadUrl("file:///android_asset/demo.html");

        topBar.setTitle("H5 Demo");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb, callback data from native");
            }

        });
    }
}
