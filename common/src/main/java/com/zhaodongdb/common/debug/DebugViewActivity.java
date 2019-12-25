package com.zhaodongdb.common.debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.zhaodongdb.common.R2;
import com.zhaodongdb.common.component.BaseActivity;
import com.zhaodongdb.common.router.ZDRouter;
import com.zhaodongdb.common.user.User;
import com.zhaodongdb.common.utils.FoundationContextHolder;
import com.zhaodongdb.common.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

@Route(path = "/common/debug")
public class DebugViewActivity extends BaseActivity {

    final static String TAG = DebugViewActivity.class.getSimpleName();

    SharedPreferences debugInfoSP = FoundationContextHolder.getContext().getSharedPreferences("DebugInfo", Context.MODE_PRIVATE);

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
    }

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_view);
        ButterKnife.bind(this);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();

        debugInfoSP.edit().putString("pageUrl", pageUrlEdit.getText().toString()).apply();
    }
}
