package com.zhaodongdb.wireless;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alibaba.android.vlayout.Range;
import com.libra.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.core.adapter.GroupBasicAdapter;
import com.tmall.wireless.tangram.dataparser.concrete.Card;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.support.async.AsyncLoader;
import com.tmall.wireless.tangram.support.async.AsyncPageLoader;
import com.tmall.wireless.tangram.support.async.CardLoadSupport;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zhaodongdb.common.network.ZDHttpCallback;
import com.zhaodongdb.common.network.ZDHttpClient;
import com.zhaodongdb.common.network.ZDHttpResponse;
import com.zhaodongdb.common.network.ZdHttpFailure;
import com.zhaodongdb.common.utils.PermissionHelper;
import com.zhaodongdb.montage.ImageTarget;
import com.zhaodongdb.montage.PageData;
import com.zhaodongdb.montage.VirtualViewEventProcessor;
import com.zhaodongdb.common.router.ZDRouter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private Handler mMainHandler;
    TangramEngine engine;
    RecyclerView recyclerView;
    private String mTemplateName = "HelloWorld";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_montage_standard);
        recyclerView = (RecyclerView) findViewById(R.id.main_view);

        //Tangram.switchLog(true);
        mMainHandler = new Handler(getMainLooper());

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

            private List<com.zhaodongdb.montage.ImageTarget> cache = new ArrayList<com.zhaodongdb.montage.ImageTarget>();

            @Override
            public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                RequestCreator requestCreator = Picasso.with(MainActivity.this).load(uri);
                Log.d(TAG, "bindImage request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                com.zhaodongdb.montage.ImageTarget imageTarget = new com.zhaodongdb.montage.ImageTarget(imageBase);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }

            @Override
            public void getBitmap(String uri, int reqWidth, int reqHeight, final ImageLoader.Listener lis) {
                RequestCreator requestCreator = Picasso.with(MainActivity.this).load(uri);
                Log.d(TAG, "getBitmap request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                com.zhaodongdb.montage.ImageTarget imageTarget = new ImageTarget(lis);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }
        });
        engine.getService(VafContext.class).getEventManager().register(EventManager.TYPE_Click, new VirtualViewEventProcessor());
        Utils.setUedScreenWidth(720);

        //Step 5: add card load support if you have card that loading cells async
        engine.addCardLoadSupport(new CardLoadSupport(
                new AsyncLoader() {
                    @Override
                    public void loadData(Card card, @NonNull final LoadedCallback callback) {
                        Log.w("Load Card", card.load);

                        mMainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // do loading
                                JSONArray cells = new JSONArray();

                                for (int i = 0; i < 10; i++) {
                                    try {
                                        JSONObject obj = new JSONObject();
                                        obj.put("type", 1);
                                        obj.put("msg", "async loaded");
                                        JSONObject style = new JSONObject();
                                        //style.put("bgColor", "#FF1111");
                                        obj.put("style", style.toString());
                                        cells.put(obj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // callback.fail(false);
                                callback.finish(engine.parseComponent(cells));
                            }
                        }, 200);
                    }
                },

                new AsyncPageLoader() {
                    @Override
                    public void loadData(final int page, @NonNull final Card card, @NonNull final LoadedCallback callback) {
                        mMainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("Load page", card.load + " page " + page);
                                JSONArray cells = new JSONArray();
                                for (int i = 0; i < 9; i++) {
                                    try {
                                        JSONObject obj = new JSONObject();
                                        obj.put("type", 1);
                                        obj.put("msg", "async page loaded, params: " + card.getParams().toString());
                                        cells.put(obj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                List<BaseCell> cs = engine.parseComponent(cells);

                                if (card.page == 1) {
                                    GroupBasicAdapter<Card, ?> adapter = engine.getGroupBasicAdapter();

                                    card.setCells(cs);
                                    adapter.refreshWithoutNotify();
                                    Range<Integer> range = adapter.getCardRange(card);

                                    adapter.notifyItemRemoved(range.getLower());
                                    adapter.notifyItemRangeInserted(range.getLower(), cs.size());

                                } else
                                    card.addCells(cs);

                                //mock load 6 pages
                                callback.finish(card.page != 6);
                                card.notifyDataChange();
                            }
                        }, 400);
                    }
                }));

        //Step 6: enable auto load more if your page's data is lazy loaded
        engine.enableAutoLoadMore(true);

        //Step 7: bind recyclerView to engine
        engine.bindView(recyclerView);

        //Step 8: listener recyclerView onScroll event to trigger auto load more
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                engine.onScrolled();
            }
        });

        //Step 9: set an offset to fix card
        engine.getLayoutManager().setFixOffset(0, 40, 0, 0);

        refreshByName(mTemplateName);

        requestAllPermissions();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (engine != null) {
            engine.destroy();
        }
    }

    public static byte[] getAssertsFile(Context context, String fileName) {
        InputStream inputStream = null;
        AssetManager assetManager = context.getAssets();
        try {
            inputStream = assetManager.open(fileName);
            if (inputStream == null) {
                return null;
            }

            BufferedInputStream bis = null;
            int length;
            try {
                bis = new BufferedInputStream(inputStream);
                length = bis.available();
                byte[] data = new byte[length];
                bis.read(data);

                return data;
            } catch (IOException e) {

            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {

                    }
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getUrl(String name) {
        return "http://10.0.2.2:7788/" + name + "/data.json";
    }

    private void refreshByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            refreshByUrl(getUrl(name));
        }
    }

    private void refreshByUrl(final String url) {

        ZDHttpClient.getInstance().asyncGet(url, new ZDHttpCallback() {

            @Override
            public void onFailure(ZdHttpFailure failure) {
                Log.d("Montage", "failure");
            }

            @Override
            public void onResponse(ZDHttpResponse response) throws IOException {

                final String json = response.getResponseString();
                final com.zhaodongdb.montage.PageData pageData = com.alibaba.fastjson.JSON.parseObject(json, com.zhaodongdb.montage.PageData.class);

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
        // Step 10: get tangram data and pass it to engine
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
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshByName(mTemplateName);
                return true;
            case R.id.menu_rtl:
                ZDRouter.navigation("zhaodong://native/montage/standard?pageName=temp");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestAllPermissions() {
        PermissionHelper.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}
                , false, new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionCallback(String[] permissions, PermissionHelper.PermissionResult[] grantResults) {
                        try {
                            if (grantResults != null) {
                                StringBuilder sb = new StringBuilder();
                                for (PermissionHelper.PermissionResult pResult : grantResults) {
                                    sb.append(pResult.grantResult);
                                    sb.append("--");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionsError(String errMsg, String[] permissions, PermissionHelper.PermissionResult[] grantResults) {
                    }
                });
    }
}
