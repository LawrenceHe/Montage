package com.zhaodongdb.common.network;

import java.io.IOException;

public interface ZDHttpCallback {

    void onFailure(ZdHttpFailure failure);

    void onResponse(ZDHttpResponse response) throws IOException;
}
