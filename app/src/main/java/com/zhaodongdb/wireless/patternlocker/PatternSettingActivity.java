package com.zhaodongdb.wireless.patternlocker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.Result;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.patternlocker.DefaultLockerNormalCellView;
import com.zhaodongdb.common.patternlocker.DefaultStyleDecorator;
import com.zhaodongdb.common.patternlocker.OnPatternChangeListener;
import com.zhaodongdb.common.patternlocker.PatternHelper;
import com.zhaodongdb.common.patternlocker.PatternIndicatorView;
import com.zhaodongdb.common.patternlocker.PatternLockerView;
import com.zhaodongdb.common.patternlocker.RippleLockerHitCellView;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.wireless.R;
import com.zhaodongdb.wireless.login.LoginRegisterRespData;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/app/patternlocker/setting")
public class PatternSettingActivity extends BaseActivity {

    static final String TAG = PatternSettingActivity.class.getSimpleName();

    @BindView(R.id.patternLockerView)
    PatternLockerView patternLockerView;

    @BindView(R.id.patternIndicatorView)
    PatternIndicatorView patternIndicatorView;

    @BindView(R.id.textMsg)
    TextView textMsg;

    @Autowired
    String verifyUserId;

    private PatternHelper patternHelper = null;
    private String gesture = "";
    public void setHitIndexList(List<Integer> hitIndexList) {
        if (this.patternHelper.isFinish() ) {
            StringBuilder sb = new StringBuilder();
            for (Integer i : hitIndexList) {
                sb.append(i);
            }
            this.gesture = sb.toString();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_pattern_setting);
        ButterKnife.bind(this);

        DefaultStyleDecorator decorator = ((DefaultLockerNormalCellView)patternLockerView.getNormalCellView()).getStyleDecorator();

        patternLockerView.setHitCellView(new RippleLockerHitCellView()
                .setHitColor(decorator.getHitColor())
                .setErrorColor(decorator.getErrorColor()));

        patternLockerView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NotNull PatternLockerView view) {

            }

            @Override
            public void onChange(@NotNull PatternLockerView view, @NotNull List<Integer> hitIndexList) {

            }

            @Override
            public void onComplete(@NotNull PatternLockerView view, @NotNull List<Integer> hitIndexList) {
                boolean isOk = isPatternOk(hitIndexList);
                view.updateStatus(!isOk);
                patternIndicatorView.updateState(hitIndexList, !isOk);
                setHitIndexList(hitIndexList);
                updateMsg();
                patternLockerView.clearHitState();
            }

            @Override
            public void onClear(@NotNull PatternLockerView view) {
                finishIfNeeded();
            }
        });

        this.textMsg.setText("设置解锁图案");
        this.patternHelper = new PatternHelper();
    }

    private boolean isPatternOk(List<Integer> hitIndexList) {
        this.patternHelper.validateForSetting(hitIndexList);
        return this.patternHelper.isOk();
    }

    private void updateMsg() {
        this.textMsg.setText(this.patternHelper.getMessage());
        int color;
        if (this.patternHelper.isOk()) {
            color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        } else {
            color = ContextCompat.getColor(this, R.color.color_red);
        }
        this.textMsg.setTextColor(color);
    }

    private void finishIfNeeded() {
        if (this.patternHelper.isFinish()){
            Map<String, String> body = new HashMap<>();
            body.put("gesture", gesture);
            body.put("verifyUserId", verifyUserId);
            ZDHttpClient.getInstance().asyncPost(
                    RequestUrlsEnum.SET_GESTURE.getEnvUrl(),
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
                                        Toast.makeText(PatternSettingActivity.this, "设置手势密码成功！", Toast.LENGTH_SHORT).show();
                                        PatternSettingActivity.this.finish();
                                    } else {
                                        Toast.makeText(PatternSettingActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
        }
    }
}
