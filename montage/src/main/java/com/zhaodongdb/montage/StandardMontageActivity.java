package com.zhaodongdb.montage;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.libra.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.core.R;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zhaodongdb.common.network.HttpRequestHelper;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpFailure;
import com.zhaodongdb.common.network.ZDHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(path = "/montage/standard")
public class StandardMontageActivity extends AppCompatActivity {

    final String TAG = StandardMontageActivity.class.getSimpleName();

    @Autowired
    String pageName;

    private RecyclerView recyclerView;
    private TangramEngine engine;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_montage_standard);
        recyclerView = (RecyclerView) findViewById(R.id.main_view);

        final Context appContext = this.getApplicationContext();
        //Step 1: init tangram
        TangramBuilder.init(appContext, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view,
                                                                 @Nullable String url) {
                Picasso.with(appContext).load(url).into(view);
            }
        }, ImageView.class);
        //Step 2: register build=in cells and cards
        TangramBuilder.InnerBuilder montageBuilder = TangramBuilder.newInnerBuilder(this);
        engine = montageBuilder.build();
        engine.getService(VafContext.class).setImageLoaderAdapter(new ImageLoader.IImageLoaderAdapter() {

            private List<ImageTarget> cache = new ArrayList<ImageTarget>();

            @Override
            public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                RequestCreator requestCreator = Picasso.with(StandardMontageActivity.this).load(uri);
                Log.d(TAG, "bindImage request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                ImageTarget imageTarget = new ImageTarget(imageBase);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }

            @Override
            public void getBitmap(String uri, int reqWidth, int reqHeight, final ImageLoader.Listener lis) {
                RequestCreator requestCreator = Picasso.with(StandardMontageActivity.this).load(uri);
                Log.d(TAG, "getBitmap request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                ImageTarget imageTarget = new ImageTarget(lis);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }
        });
        engine.getService(VafContext.class).getEventManager().register(EventManager.TYPE_Click, new VirtualViewEventProcessor());
//        engine.addSimpleClickSupport(new SampleClickSupport());
        engine.enableAutoLoadMore(true);
//        engine.register(InternalErrorSupport.class, new SampleErrorSupport());
        engine.bindView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                engine.onScrolled();
            }
        });
        engine.getLayoutManager().setFixOffset(0, 40, 0, 0);
        Utils.setUedScreenWidth(720);
        refreshByName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
    }

    private String getUrl() {
        return "http://192.168.54.2:18080/page/getPageInfo.json";
    }

    private void refreshByName() {
        refreshByUrl(getUrl());
    }

    private void refreshByUrl(final String url) {

        Map<String, String> body = new HashMap<>();
        body.put("pageName", pageName);

        ZDHttpClient.getInstance().asyncPost(url, HttpRequestHelper.buildJsonRequest(body), new ZDHttpCallback() {

            @Override
            public void onFailure(ZDHttpFailure failure) {
                Log.d("Montage", "failure");
            }

            @Override
            public void onResponse(ZDHttpResponse response) throws IOException {

                final String json = response.getResponseString();
                final JSONObject obj = JSON.parseObject(json)
                        .getJSONObject("data")
                        .getJSONObject("pageInfo");
                final PageData pageData = obj.toJavaObject(PageData.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pageData != null) {
                            loadTemplates(pageData);
                        }
                    }
                });
            }
        });

    }

    private void loadTemplates(PageData data){
        for (Map.Entry<String, String> template : data.getTemplates().entrySet()) {
            engine.registerVirtualViewTemplate(
                    template.getKey(),
                    Base64.decode(template.getValue(), Base64.DEFAULT));
        }
        // get tangram data and pass it to engine
        try {
            engine.setData(new JSONArray(data.getData()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_refresh) {
            refreshByName();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
