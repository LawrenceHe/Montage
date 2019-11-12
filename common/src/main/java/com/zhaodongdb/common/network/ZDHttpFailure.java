package com.zhaodongdb.common.network;

import okhttp3.Call;
import okhttp3.Response;

public class ZDHttpFailure {

    private Call call;
    private Exception exception;
    private Response response;

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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
