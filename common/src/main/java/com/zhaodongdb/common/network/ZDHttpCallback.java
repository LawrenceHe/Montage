package com.zhaodongdb.common.network;

import java.io.IOException;

public interface ZDHttpCallback {

    void onFailure(ZDHttpFailure failure);

    void onResponse(ZDHttpResponse response) throws IOException;
}
