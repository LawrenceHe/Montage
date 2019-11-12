package com.zhaodongdb.common.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.zhaodongdb.common.R;
import com.zhaodongdb.common.R2;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.network.BaseSender;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.utils.ThreadUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/common/login")
public class LoginRegisterActivity extends BaseActivity implements View.OnClickListener {

    static final String TAG = LoginRegisterActivity.class.getSimpleName();

    @BindView(R2.id.topbar) QMUITopBarLayout topBarLayout;
    @BindView(R2.id.messageToken) QMUIRoundButton sendMessageToken;
    @BindView(R2.id.loginMobile) EditText loginMobile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);

        topBarLayout.setTitle("登录/注册");

        sendMessageToken.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.messageToken) {
            String mobile = loginMobile.getText().toString();
            if (mobile.length() != 11) {
                Toast.makeText(LoginRegisterActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> body = new HashMap<>();
            body.put("mobile", mobile);
            ZDHttpClient.getInstance().asyncPost(
                    RequestUrlsEnum.LOGIN_MESSAGE_TOKEN.getEnvUrl(),
                    BaseSender.buildJsonRequest(body),
                    new ZDHttpCallback() {

                        @Override
                        public void onFailure(ZDHttpFailure failure) {
                            Log.d(TAG, "send message token failure");
                        }

                        @Override
                        public void onResponse(final ZDHttpResponse response) throws IOException {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginRegisterActivity.this, "短信验证码发送成功！", Toast.LENGTH_SHORT).show();
                                    String resp = response.getResponseString();
                                    Log.d(TAG, resp);
                                }
                            });
                        }
                    });
        }
    }
}
