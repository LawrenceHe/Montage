package com.zhaodongdb.common.network;

import android.net.Uri;
import android.text.TextUtils;

import com.zhaodongdb.common.utils.ThreadUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZDHttpClient {

    public static final int kMinTimeout = 5 * 1000;
    public static final int kMaxTimeout = 120 * 1000;
    public static final int kDefaultTimeout = 15 * 1000;

    private OkHttpClient okClient;
    private static ZDHttpClient instance;
    private static final MediaType MediaType_JSON = MediaType.parse("application/json;charset=utf-8");
    private static final IOException timeoutException = new IOException("网络请求超时,超过设定timeout(-110)");

    private ZDHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(kMaxTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(kMaxTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(kMaxTimeout, TimeUnit.MILLISECONDS);
        builder.connectionPool(new ConnectionPool(5, 60000L, TimeUnit.MILLISECONDS));
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                try {
                    Response response = chain.proceed(chain.request());
                    return response;
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Other Exception:" + e.getMessage());
                }
            }
        });

        okClient = builder.build();
    }

    /**
     * 获取ZDHttpClient单例
     *
     * @return
     */
    public static ZDHttpClient getInstance() {
        if (instance == null) {
            instance = new ZDHttpClient();
        }
        return instance;
    }

    /**
     * 获取实际的OkHttpClient
     *
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return okClient;
    }

    /**
     * 通过tag取消请求
     *
     * @param tag tag是发送请求之后的返回值
     */
    public void cancelRequest(final String tag) {
        if (!TextUtils.isEmpty(tag) && okClient != null) {
            ThreadUtils.runOnBackgroundThread(new Runnable() {
                @Override
                public void run() {
                    cancelCallsWithTag(tag);
                }
            });
        }
    }

    private void cancelCallsWithTag(Object tag) {
        if (tag == null)
            return;

        if (okClient == null)
            return;


        synchronized (okClient.dispatcher().getClass()) {
            for (Call call : okClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag()))
                    call.cancel();
            }

            for (Call call : okClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag()))
                    call.cancel();
            }
        }
    }

    public String asyncGet(String url, final ZDHttpCallback responseCallback) {
        return asyncGetWithTimeout(url, null, responseCallback, kDefaultTimeout);
    }

    public String asyncGetWithTimeout(String url, Map<String, String> paramMap, final ZDHttpCallback responseCallback, final int timeoutMillis) {
        return asyncGetWithTimeout(url, paramMap, responseCallback, timeoutMillis, null);
    }

    /**
     * 异步调用HTTP的get方法
     *
     * @param url              目标URL
     * @param paramMap         需要加在queryString上的参数和值
     * @param responseCallback 回调
     * @param timeoutMillis    超时，单位毫秒, 如果超时时间>kMaxTimeout(200*1000ms)或者小于kMinTimeout(2*1000ms),都会使用默认超时时间kDefaultTimeout(15*1000ms)
     * @param headers          自定义HTTP Header
     * @return 返回request的Tag，取消请求的时候需要使用
     */
    public String asyncGetWithTimeout(String url, Map<String, String> paramMap, final ZDHttpCallback responseCallback, final int timeoutMillis, Map<String, String> headers) {

        Uri.Builder urlBuilder = Uri.parse(url).buildUpon();
        if (paramMap != null) {
            Iterator<Map.Entry<String, String>> iterator = paramMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                urlBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        String realUrl = urlBuilder.build().toString();
        String retTag = getRequestTagByURL(realUrl);

        Request.Builder builder = new Request.Builder()
                .url(realUrl)
                .get()
                .tag(retTag);

        if (headers != null) {
            for (String header : headers.keySet()) {
                builder.header(header, headers.get(header));
            }
        }

        final Request request = builder.build();
        final Call call = okClient.newCall(request);

        CallbackWithTimeout cbWithTimeout = wrapCallbackWithTimeout(call, request, responseCallback, timeoutMillis);
        call.enqueue(cbWithTimeout);

        return retTag;
    }

    public String asyncPost(String url, String json, final ZDHttpCallback responseCallback) {
        return asyncPostWithTimeout(url, json, responseCallback, kDefaultTimeout);
    }

    public String asyncPostWithTimeout(String url, String json, final ZDHttpCallback responseCallback, final int timeoutMillis) {
        return asyncPostWithTimeout(url, json, responseCallback, timeoutMillis, null);
    }

    public String asyncPost(String url, String json, final ZDHttpCallback responseCallback, final int timeoutMillis, Map<String, String> headers, boolean isFromCRN) {
        return asyncPostWithTimeout(url, json, responseCallback, timeoutMillis, headers );
    }

    public String asyncPost(String url, String json, final ZDHttpCallback responseCallback, final int timeoutMillis, Map<String, String> headers, boolean isFromCRN, boolean encrpt) {
        return asyncPostWithTimeout(url, json, responseCallback, timeoutMillis, headers);
    }

    /**
     * 异步调用HTTP的post方法
     *
     * @param url              目标url
     * @param json           JSON字符串
     * @param responseCallback 回调
     * @param timeoutMillis    超时，单位毫秒, 如果超时时间>kMaxTimeout(120*1000ms)或者小于kMinTimeout(5*1000ms),都会使用默认超时时间kDefaultTimeout(15*1000ms)
     * @param headers          自定义HTTP Headers
     * @return 返回request的Tag，取消请求的时候需要使用
     */
    public String asyncPostWithTimeout(String url, String json, final ZDHttpCallback responseCallback, final int timeoutMillis, Map<String, String> headers) {
        String retTag = getRequestTagByURL(url);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(retTag);
        RequestBody jsonBody = null;
        try{
            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            jsonBody = RequestBody.create(MediaType_JSON, body);
            if (headers != null) {
                for (String header : headers.keySet()) {
                    if (TextUtils.equals("Content-Type", header)) {
                        jsonBody = RequestBody.create(MediaType.parse(headers.get(header)), body);
                    }
                    builder.header(header, headers.get(header));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

//        if(autoSetCookie && CookieManager.getInstance().getCookie(url) != null) {
//            builder.header("cookie", CookieManager.getInstance().getCookie(url));
//        }
//
//        LogUtil.d("url="+url+"cookie="+CookieManager.getInstance().getCookie(url));

        final Request request = builder.post(jsonBody).build();

        final Call call = okClient.newCall(request);
        CallbackWithTimeout cbWithTimeout = wrapCallbackWithTimeout(call, request, responseCallback, timeoutMillis);
        call.enqueue(cbWithTimeout);
        return retTag;
    }

    private CallbackWithTimeout wrapCallbackWithTimeout(final Call call,
                                                        final Request request,
                                                        final ZDHttpCallback responseCallback,
                                                        int timeoutMillis) {

        if (call == null || request == null) {
            return null;
        }

        CallbackWithTimeout CallbackWithTimeout = new CallbackWithTimeout() {

            @Override
            public void onFailure(final Call call, final IOException e) {
                if (call.isCanceled()) {
                    return;
                }

                if (!mTimeout) {
                    if (timeoutException.equals(e)) {
                        mTimeout = true;
                    }
                    ThreadUtils.runOnBackgroundThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseCallback != null) {
                                    ZDHttpFailure failure = new ZDHttpFailure();
                                    failure.setCall(call);
                                    failure.setException(e);
                                    responseCallback.onFailure(failure);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }

                if (!mTimeout) {

                    if (response != null && response.isSuccessful()) {
                        if (responseCallback != null) {
                            final ZDHttpResponse httpResponse = new ZDHttpResponse();
                            httpResponse.setCall(call);
                            httpResponse.setResponse(response);
                            ThreadUtils.runOnBackgroundThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        responseCallback.onResponse(httpResponse);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    } else {
                        final ZDHttpFailure failure = new ZDHttpFailure();
                        failure.setCall(call);
                        failure.setResponse(response);
                        String detailMessage = "HTTP Response Error";
                        if (response != null && !TextUtils.isEmpty(response.message())) {
                            detailMessage = response.message();
                        }
                        failure.setException(new Exception(detailMessage));
                        ThreadUtils.runOnBackgroundThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (responseCallback != null) {
                                        responseCallback.onFailure(failure);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        };

        return CallbackWithTimeout;
    }

    private static class CallbackWithTimeout implements Callback {
        protected boolean mTimeout = false;

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    }

    private String getRequestTagByURL(String url) {
        String path = "--";
        if (!TextUtils.isEmpty(url)) {
            Uri.Builder urlBuilder = Uri.parse(url).buildUpon();
            path = urlBuilder.build().getPath();
        }
        String retTag = "RequestTag:" + path + ":" + System.currentTimeMillis();
        return retTag;
    }
}
