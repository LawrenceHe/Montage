package com.zhaodongdb.wireless.home;

import android.content.Context;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.libra.TextUtils;
import com.libra.Utils;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.RequestUrlsEnum;
import com.zhaodongdb.common.network.Result;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.utils.ThreadUtils;
import com.zhaodongdb.montage.ImageTarget;
import com.zhaodongdb.montage.VirtualViewEventProcessor;
import com.zhaodongdb.wireless.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public abstract class HomeController extends QMUIWindowInsetLayout {

    static final String TAG = HomeController.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String pageName;
    private TangramEngine engine;

    private HomeControlListener mHomeControlListener;
    private int mDiffRecyclerViewSaveStateId = QMUIViewHelper.generateViewId();

    public HomeController(Context context, String pageName, TangramEngine engine) {
        super(context);

        this.pageName = pageName;
        this.engine = engine;

        LayoutInflater.from(context).inflate(R.layout.home_layout, this);
        ButterKnife.bind(this);
        initTopBar();
        initMontage();
        initData();
    }

    protected TangramEngine getEngine() {
        return engine;
    }

    protected void initMontage() {
        final Context appContext = this.getContext();

        engine.getService(VafContext.class).setImageLoaderAdapter(new ImageLoader.IImageLoaderAdapter() {

            private List<ImageTarget> cache = new ArrayList<ImageTarget>();

            @Override
            public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                if (TextUtils.isEmpty(uri)) {
                    return;
                }
                try {
                    RequestCreator requestCreator;
                    if (uri.startsWith("http")) {
                        requestCreator = Picasso.with(appContext).load(uri);
                    } else {
                        int resId = getResources().getIdentifier(uri, "drawable" , appContext.getPackageName());
                        requestCreator = Picasso.with(appContext).load(resId);
                    }
                    Log.d(TAG, "bindImage request width:" + reqWidth + " height:" + reqHeight + " src:" + (uri == null ? "" : uri));
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(imageBase);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "bind image error." + e.getMessage() + "src:" + uri);
                }
            }

            @Override
            public void getBitmap(String uri, int reqWidth, int reqHeight, final ImageLoader.Listener lis) {
                if (TextUtils.isEmpty(uri)) {
                    return;
                }
                try {
                    RequestCreator requestCreator;
                    if (uri.startsWith("http")) {
                        requestCreator = Picasso.with(appContext).load(uri);
                    } else {
                        int resId = getResources().getIdentifier(uri, "drawable" , appContext.getPackageName());
                        requestCreator = Picasso.with(appContext).load(resId);
                    }
                    Log.d(TAG, "getBitmap request width:" + reqWidth + " height:" + reqHeight + " src:" + (uri == null ? "" : uri));
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(lis);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "get bitmap error." + e.getMessage() + "src:" + uri);
                }
            }
        });
        engine.getService(VafContext.class).getEventManager().register(EventManager.TYPE_Click, new VirtualViewEventProcessor());
        engine.enableAutoLoadMore(true);
        engine.bindView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                engine.onScrolled();
            }
        });
        engine.getLayoutManager().setFixOffset(0, 40, 0, 0);
        Utils.setUedScreenWidth(720);
    }

    protected void initData() {

        Map<String, String> body = new HashMap<>();
        body.put("pageName", pageName);
        ZDHttpClient.getInstance().asyncPost(
                RequestUrlsEnum.MONTAGE_PAGE.getEnvUrl(),
                HttpRequestHelper.buildJsonRequest(body),
                new ZDHttpCallback() {

                    @Override
                    public void onFailure(ZDHttpFailure failure) {
                        Log.d(TAG, "get page info failure");
                    }

                    @Override
                    public void onResponse(final ZDHttpResponse response) throws IOException {

                        final Result<JSONObject> result = HttpRequestHelper.parseHttpResponse(response.getResponseString(), JSONObject.class);
                        final JSONObject data = result.getData().getJSONObject("data");

                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                JSONObject templates = data.getJSONObject("templates");
                                for (Map.Entry<String, Object> template : templates.entrySet()) {
                                    Log.d(TAG, "key:" + template.getKey() + " value:" + template.getValue().toString());
                                    engine.registerVirtualViewTemplate(
                                            template.getKey(),
                                            Base64.decode(template.getValue().toString(), Base64.DEFAULT));
                                }

                                try {
                                    JSONArray array = new JSONArray(data.getJSONArray("cards").toString());
                                    engine.setData(array);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
    }

    protected void startFragment(BaseFragment fragment) {
        if (mHomeControlListener != null) {
            mHomeControlListener.startFragment(fragment);
        }
    }

    public void setHomeControlListener(HomeControlListener homeControlListener) {
        mHomeControlListener = homeControlListener;
    }

    protected abstract String getTitle();

    private void initTopBar() {
        mTopBar.setTitle(getTitle());

//        mTopBar.addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                QDAboutFragment fragment = new QDAboutFragment();
//                startFragment(fragment);
//            }
//        });
    }

    public interface HomeControlListener {
        void startFragment(BaseFragment fragment);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchSaveInstanceState(container);
        mRecyclerView.setId(id);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchRestoreInstanceState(container);
        mRecyclerView.setId(id);
    }

}
