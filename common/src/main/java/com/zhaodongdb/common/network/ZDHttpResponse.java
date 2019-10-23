package com.zhaodongdb.common.network;

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
            if(getResponse()!=null)
                return getResponse().body().string();
            else
                return null;
        }catch (Exception ex){

        }
        return null;
    }
}
