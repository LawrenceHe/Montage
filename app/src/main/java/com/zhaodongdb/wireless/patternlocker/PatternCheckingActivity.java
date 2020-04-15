package com.zhaodongdb.wireless.patternlocker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.Result;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.user.UserInfo;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.wireless.R;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.patternlocker.DefaultLockerNormalCellView;
import com.zhaodongdb.common.patternlocker.DefaultStyleDecorator;
import com.zhaodongdb.common.patternlocker.OnPatternChangeListener;
import com.zhaodongdb.common.patternlocker.PatternIndicatorView;
import com.zhaodongdb.common.patternlocker.PatternLockerView;
import com.zhaodongdb.common.patternlocker.RippleLockerHitCellView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/base/patternlocker/checking")
public class PatternCheckingActivity extends BaseActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_pattern_checking);
        ButterKnife.bind(this);

        DefaultStyleDecorator decorator = ((DefaultLockerNormalCellView) this.patternLockerView.getNormalCellView()).getStyleDecorator();

        this.patternLockerView.setHitCellView(new RippleLockerHitCellView()
                .setHitColor(decorator.getHitColor())
                .setErrorColor(decorator.getErrorColor()));

        this.patternLockerView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NotNull PatternLockerView view) {

            }

            @Override
            public void onChange(@NotNull PatternLockerView view, @NotNull List<Integer> hitIndexList) {

            }

            @Override
            public void onComplete(@NotNull PatternLockerView view, @NotNull List<Integer> hitIndexList) {
                boolean isError = !isPatternOk(hitIndexList);
                if (isError) {
                    view.updateStatus(isError);
                    patternIndicatorView.updateState(hitIndexList, isError);
                    updateMsg();
                } else {
                    finishIfNeeded(view, hitIndexList);
                }
            }

            @Override
            public void onClear(@NotNull PatternLockerView view) {

            }
        });

        this.textMsg.setText("输入解锁图案");
        this.patternHelper = new PatternHelper();
    }

    private boolean isPatternOk(List<Integer> hitIndexList) {
        this.patternHelper.validateForChecking(hitIndexList);
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

    private void finishIfNeeded(final PatternLockerView view, final List<Integer> hitIndexList) {
        Map<String, String> body = new HashMap<>();
        body.put("gesture", this.patternHelper.getGesture());
        body.put("verifyUserId", verifyUserId);
        ZDHttpClient.getInstance().asyncPost(
                RequestUrlsEnum.LOGIN_BY_GESTURE.getEnvUrl(),
                HttpRequestHelper.buildJsonRequest(body),
                new ZDHttpCallback() {

                    @Override
                    public void onFailure(ZDHttpFailure failure) {
                        Log.d(TAG, "check user gesture failure");
                    }

                    @Override
                    public void onResponse(final ZDHttpResponse response) throws IOException {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String resp = response.getResponseString();
                                Result<PatternRespData> result = HttpRequestHelper.parseHttpResponse(resp, PatternRespData.class);
                                boolean isOk = result.isSuccess();
                                patternHelper.validateForChecking(isOk);
                                view.updateStatus(!isOk);
                                patternIndicatorView.updateState(hitIndexList, !isOk);
                                updateMsg();
                                if (result.isSuccess()) {
                                    Toast.makeText(PatternCheckingActivity.this, "验证手势密码成功！", Toast.LENGTH_SHORT).show();
                                    UserInfo.getInstance().setUserName(result.getData().getUserName());
                                    UserInfo.getInstance().setUserId(result.getData().getUserId());
                                    UserInfo.getInstance().setAccessToken(result.getData().getAccessToken());
                                    UserInfo.getInstance().setRefreshToken(result.getData().getRefreshToken());
                                    PatternCheckingActivity.this.finish();
                                } else {
                                    Toast.makeText(PatternCheckingActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
}
