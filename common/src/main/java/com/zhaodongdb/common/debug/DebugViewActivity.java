package com.zhaodongdb.common.debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhaodongdb.common.R2;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.config.AppConfig;
import com.zhaodongdb.common.router.ZDRouter;
import com.zhaodongdb.common.user.User;
import com.zhaodongdb.common.utils.FoundationContextHolder;
import com.zhaodongdb.common.R;
import com.zhaodongdb.common.utils.WXConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = "/common/debug")
public class DebugViewActivity extends BaseActivity {

    final static String TAG = DebugViewActivity.class.getSimpleName();

    private SharedPreferences debugInfoSP = FoundationContextHolder.getContext().getSharedPreferences("DebugInfo", Context.MODE_PRIVATE);
    private Realm realm;

    @BindView(R2.id.topbar)
    QMUITopBarLayout topBar;

    @OnClick(R2.id.testRealmDatabase)
    void writeUserData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.createObject(User.class);
                user.setUserName("Lawrence");
                user.setMobile("15021666888");
            }
        });
        RealmResults<User> users = realm.where(User.class).findAll();
        users.deleteAllFromRealm();

        User myDog = users.last();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User myPuppy = realm.where(User.class).findFirst();
                myPuppy.setMobile("15033777777");
            }
        });

        String mobile = myDog.getMobile();
        Log.d(TAG, mobile);
    }

    @BindView(R2.id.pageUrl)
    EditText pageUrlEdit;

    @OnClick(R2.id.openCertainPage)
    void openCertainPage() {
        ZDRouter.navigation(pageUrlEdit.getText().toString());
        debugInfoSP.edit().putString("pageUrl", pageUrlEdit.getText().toString()).apply();
    }

    @BindView(R2.id.radioEnvGroup)
    RadioGroup radioEnvGroup;
    @BindView(R2.id.radioEnvDev)
    RadioButton radioEnvDev;
    @BindView(R2.id.radioEnvSit)
    RadioButton radioEnvSit;
    @BindView(R2.id.radioEnvUat)
    RadioButton radioEnvUat;
    @BindView(R2.id.radioEnvPrd)
    RadioButton radioEnvPrd;

    private IWXAPI api;

    @OnClick(R2.id.testWeixinLogin)
    void testWinxinLogin() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_zhaodongdb_alpha";
        api.sendReq(req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_view);
        ButterKnife.bind(this);

        api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID,false);
        realm = Realm.getDefaultInstance();

        topBar.setTitle("调试工具");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String pageUrl = debugInfoSP.getString("pageUrl", "");
        pageUrlEdit.setText(pageUrl);

        switch(AppConfig.getEnv()) {
            case DEV:
                radioEnvDev.setChecked(true);
                break;
            case SIT:
                radioEnvSit.setChecked(true);
                break;
            case UAT:
                radioEnvUat.setChecked(true);
                break;
            case PRD:
                radioEnvPrd.setChecked(true);
                break;
        }

        radioEnvGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioEnvDev) {
                    AppConfig.setEnv(AppConfig.EnvType.DEV);
                } else if (checkedId == R.id.radioEnvSit) {
                    AppConfig.setEnv(AppConfig.EnvType.SIT);
                } else if (checkedId == R.id.radioEnvUat) {
                    AppConfig.setEnv(AppConfig.EnvType.UAT);
                } else if (checkedId == R.id.radioEnvPrd) {
                    AppConfig.setEnv(AppConfig.EnvType.PRD);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
