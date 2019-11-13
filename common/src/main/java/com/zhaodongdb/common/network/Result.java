package com.zhaodongdb.common.network;

import java.io.Serializable;
import java.time.Instant;

public class Result<T> implements Serializable {

    public static final String SUCCESSFUL_CODE = "000000";
    public static final String SUCCESSFUL_MSG = "处理成功";

    // 处理结果代码
    private String code;
    // 处理结果描述信息
    private String msg;
    //请求结果生成时间戳
    private Instant timestamp;
    //处理结果数据信息
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return SUCCESSFUL_CODE.equals(code);
    }
}
