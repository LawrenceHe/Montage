package com.zhaodongdb.common.network;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Response;

public class ZDHttpResponse {

    private Response response;

    private Call call;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public String getResponseString(){
        try {
            String body = getResponse().body().string();
//            Log.i(ZDHttpClient.TAG, "http request response body:" + body);
            return body;
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
