package com.zhaodongdb.common.network;

import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSON;
import com.zhaodongdb.common.utils.DeviceUtil;

import org.json.JSONObject;

import java.util.Map;

public class BaseSender {

    public static CommonRequestHeader buildCommonRequestHeader(Map<String, String> ext) {
        CommonRequestHeader header = new CommonRequestHeader();
        header.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjM3OTY5MzYzNjgwOTc5NzYzMiwidXNlcl9uYW1lIjoiMTc2MjE2ODY5MzEiLCJzY29wZSI6WyJzZWxlY3QiXSwiZXhwIjoxNTcwNzM1NjgzLCJhdXRob3JpdGllcyI6WyJhZG1pbiJdLCJqdGkiOiIxM2Y3M2I3My1mMWJjLTQyNzgtYjZjMi1jYmIwZmI0NTE1NzMiLCJjbGllbnRfaWQiOiJjbGllbnQyIn0.Pn9AsDa5DMueVW3nO9-3mZGUsfHH55V0tQwdBEo0dnQ");
        header.setUserId("364104831335804928");
        header.setVersion(10000);
        header.setPlatformId("i");
        header.setLang("cn");
        header.setDeviceId(DeviceUtil.getDeviceID());
        if (ext != null) {
            header.setExt(ext);
        }

        return header;
    }

    public static String buildJsonRequest(@NonNull Map<String, String> body) {
        CommonRequestHeader header = buildCommonRequestHeader(null);
        CommonRequest<Map<String, String>> request = new CommonRequest<>();
        request.setHeader(header);
        request.setData(body);

        return JSON.toJSONString(request);
    }

    public static String buildJsonRequest() {
        CommonRequestHeader header = buildCommonRequestHeader(null);
        CommonRequest<Map<String, String>> request = new CommonRequest<>();
        request.setHeader(header);

        return JSON.toJSONString(request);
    }
}
