package com.zhaodongdb.common.network;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhaodongdb.common.user.UserInfo;
import com.zhaodongdb.common.utils.DeviceUtil;

import java.util.Map;

public class HttpRequestHelper {

    public static CommonRequestHeader buildCommonRequestHeader(Map<String, String> ext) {
        CommonRequestHeader header = new CommonRequestHeader();
        header.setUserId(UserInfo.getInstance().getUserId());
        header.setToken(UserInfo.getInstance().getAccessToken());
        header.setVersion(10000);
        header.setPlatformId("a");
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

    public static <T> Result<T> parseHttpResponse(String resp, Class<T> clazz) {
        try {
            JSONObject respJsonObj = (JSONObject)JSON.parse(resp);
            String code = respJsonObj.getString("respCode");
            String msg = respJsonObj.getString("respMsg");
            Result<T> result = new Result<>();
            result.setCode(code);
            result.setMsg(msg);
            JSONObject dataJsonObj = respJsonObj.getJSONObject("data");
            if (dataJsonObj == null) {
                result.setData(null);
                return result;
            } else {
                T data = dataJsonObj.toJavaObject(clazz);
                result.setData(data);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }
}
