package com.zhaodongdb.wireless.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
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
import com.zhaodongdb.common.utils.BroadcastConstants;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.wireless.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/base/bindMobile")
public class BindMobileActivity extends BaseActivity {

    static final String TAG = BindMobileActivity.class.getSimpleName();

    @Autowired String authChannel;
    @Autowired String openId;

    @BindView(R.id.topbar) QMUITopBarLayout topBarLayout;
    @BindView(R.id.mobile) EditText mobileEdit;
    @BindView(R.id.messageToken) EditText messageTokenEdit;
    @BindView(R.id.sendMessageToken) QMUIRoundButton sendMessageTokenButton;

    final int TOTAL_SECONDS = 5;
    final int ONCE_SECONDS = 1;

    @OnClick(R.id.sendMessageToken) void sendMessageToken() {
        String mobile = mobileEdit.getText().toString();
        if (mobile.length() != 11) {
            Toast.makeText(BindMobileActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        // 开启短信验证码倒计时
        new CountDownTimer(TOTAL_SECONDS * 1000, ONCE_SECONDS * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendMessageTokenButton.setEnabled(false);
                sendMessageTokenButton.setText(String.format(Locale.getDefault(), "(%02d) 短信验证码", (int)(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                sendMessageTokenButton.setEnabled(true);
                sendMessageTokenButton.setText("发送短信验证码");
            }
        }.start();

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
                                    Toast.makeText(BindMobileActivity.this, "短信验证码发送成功", Toast.LENGTH_SHORT).show();
                                    messageTokenEdit.setText(result.getData().getMessageToken());
                                } else {
                                    Toast.makeText(BindMobileActivity.this, "短信验证码发送失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    @OnClick(R.id.bindMobile) void bindMobile() {
        String mobile = mobileEdit.getText().toString();
        String messageToken = messageTokenEdit.getText().toString();
        if (mobile.length() != 11) {
            Toast.makeText(BindMobileActivity.this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> body = new HashMap<>();
        body.put("mobile", mobile);
        body.put("messageToken", messageToken);
        body.put("authChannel", authChannel);
        body.put("authChannelId", openId);
        ZDHttpClient.getInstance().asyncPost(
                RequestUrlsEnum.LOGIN_BY_MOBILE.getEnvUrl(),
                HttpRequestHelper.buildJsonRequest(body),
                new ZDHttpCallback() {

                    @Override
                    public void onFailure(ZDHttpFailure failure) {
                        Log.d(TAG, "bind mobile request failure");
                    }

                    @Override
                    public void onResponse(final ZDHttpResponse response) throws IOException {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String resp = response.getResponseString();
                                Result<LoginRegisterRespData> result = HttpRequestHelper.parseHttpResponse(resp, LoginRegisterRespData.class);
                                if (result.isSuccess()) {
                                    Toast.makeText(BindMobileActivity.this, "短信验证码正确", Toast.LENGTH_SHORT).show();
                                    //UserInfo.getInstance().clearUserId();
                                    if (result.getData().getHasSetGesture()) {
                                        ZDRouter.navigation(String.format("/base/patternlocker/checking?verifyUserId=%s", result.getData().getVerifyUserId()));
                                    } else {
                                        ZDRouter.navigation(String.format("/base/patternlocker/setting?verifyUserId=%s", result.getData().getVerifyUserId()));
                                    }
                                    sendBroadcast(new Intent(BroadcastConstants.ZD_TEXT_MSG_VERIFICATION_SUCCESS));
                                    BindMobileActivity.this.finish();
                                } else {
                                    Toast.makeText(BindMobileActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mobile);
        ButterKnife.bind(this);

        topBarLayout.setTitle("绑定手机号");
        topBarLayout.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
