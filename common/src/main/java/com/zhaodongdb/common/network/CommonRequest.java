package com.zhaodongdb.common.network;

import java.io.Serializable;

public class CommonRequest<T> implements Serializable {

    // 公共请求头部
    private CommonRequestHeader header;

    // 公共请求数据体
    private T data;

    public CommonRequestHeader getHeader() {
        return header;
    }

    public void setHeader(CommonRequestHeader header) {
        this.header = header;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

