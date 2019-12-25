package com.zhaodongdb.common.patternlocker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zhaodongdb.common.R;
import com.zhaodongdb.common.R2;
import com.zhaodongdb.common.component.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/common/patternlocker/setting")
public class PatternSettingActivity extends BaseActivity {

    @BindView(R2.id.patternLockerView)
    PatternLockerView patternLockerView;

    @BindView(R2.id.patternIndicatorView)
    PatternIndicatorView patternIndicatorView;

    @BindView(R2.id.textMsg)
    TextView textMsg;

    private PatternHelper patternHelper = null;

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
            finish();
        }
    }
}
