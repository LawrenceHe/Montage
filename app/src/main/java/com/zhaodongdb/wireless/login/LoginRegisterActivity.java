package com.zhaodongdb.wireless.login;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.Result;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.router.ZDRouter;
import com.zhaodongdb.common.user.UserInfo;
import com.zhaodongdb.common.utils.SafeHandler;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.wireless.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/app/loginOrRegister")
public class LoginRegisterActivity extends BaseActivity {

    static final String TAG = LoginRegisterActivity.class.getSimpleName();

    @BindView(R.id.topbar) QMUITopBarLayout topBarLayout;
    @BindView(R.id.mobile) EditText mobileEdit;
    @BindView(R.id.messageToken) EditText messageTokenEdit;
    @BindView(R.id.sendMessageToken) QMUIRoundButton sendMessageTokenButton;

    final int COUNT_DOWN = 60;
    int count = COUNT_DOWN;
    SafeHandler handler = new SafeHandler(this);
    Timer timer = null;
    TimerTask task = null;

    @Override
    public void safeHandleMessage(Message msg) {
        super.safeHandleMessage(msg);

        if (msg.what == 1) {
            count--;
            sendMessageTokenButton.setEnabled(false);
            sendMessageTokenButton.setText(String.format(Locale.getDefault(), "(%02d) 短信验证码", count));

            if (count == 0) {
                sendMessageTokenButton.setEnabled(true);
                sendMessageTokenButton.setText("发送短信验证码");
                timer.cancel();
            }
        }
    }

    void startMessageTokenCountDown() {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        count = COUNT_DOWN;
        timer.schedule(task,0,1000);

    }

    @OnClick(R.id.sendMessageToken) void sendMessageToken() {
        String mobile = mobileEdit.getText().toString();
        if (mobile.length() != 11) {
            Toast.makeText(LoginRegisterActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        // 开启短信验证码倒计时，60秒只能发一次
        startMessageTokenCountDown();

        Map<String, String> body = new HashMap<>();
        body.put("mobile", mobile);
        ZDHttpClient.getInstance().asyncPost(
                RequestUrlsEnum.LOGIN_MESSAGE_TOKEN.getEnvUrl(),
                HttpRequestHelper.buildJsonRequest(body),
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
                                String resp = response.getResponseString();
                                Result<MessageTokenRespData> result = HttpRequestHelper.parseHttpResponse(resp, MessageTokenRespData.class);
                                if (result.isSuccess()) {
                                    Toast.makeText(LoginRegisterActivity.this, "短信验证码发送成功！", Toast.LENGTH_SHORT).show();
                                    messageTokenEdit.setText(result.getData().getMessageToken());
                                }
                            }
                        });
                    }
                });
    }

    @OnClick(R.id.loginOrRegister) void loginOrRegister() {
        String mobile = mobileEdit.getText().toString();
        String messageToken = messageTokenEdit.getText().toString();
        if (mobile.length() != 11) {
            Toast.makeText(LoginRegisterActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> body = new HashMap<>();
        body.put("mobile", mobile);
        body.put("messageToken", messageToken);
        ZDHttpClient.getInstance().asyncPost(
                RequestUrlsEnum.LOGIN_BY_MOBILE.getEnvUrl(),
                HttpRequestHelper.buildJsonRequest(body),
                new ZDHttpCallback() {

                    @Override
                    public void onFailure(ZDHttpFailure failure) {
                        Log.d(TAG, "login or register failure");
                    }

                    @Override
                    public void onResponse(final ZDHttpResponse response) throws IOException {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String resp = response.getResponseString();
                                Result<LoginRegisterRespData> result = HttpRequestHelper.parseHttpResponse(resp, LoginRegisterRespData.class);
                                if (result.isSuccess()) {
                                    Toast.makeText(LoginRegisterActivity.this, "登录/注册成功！", Toast.LENGTH_SHORT).show();
                                    UserInfo.getInstance().setUserId(result.getData().getUserId());
                                    UserInfo.getInstance().setAccessToken(result.getData().getAccessToken());
                                    UserInfo.getInstance().setRefreshToken(result.getData().getRefreshToken());
                                    ZDRouter.navigation("/app/patternlocker/setting");
                                } else {
                                    Toast.makeText(LoginRegisterActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        ButterKnife.bind(this);

        topBarLayout.setTitle("登录/注册");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
