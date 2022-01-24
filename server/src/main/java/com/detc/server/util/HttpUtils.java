package com.detc.server.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @author szqsy17
 */
public class HttpUtils {

    private static final JSONObject JSON = new JSONObject();

    public static String setMessage(String code, String message) {
        JSON.put("code", code);
        JSON.put("msg", message);
        return JSONObject.toJSONString(JSON, (ValueFilter) (o, s, o1) -> o1 == null ? "" : o1);
    }

    public static String setMessage(String code, JSONObject message) {
        JSON.put("code", code);
        JSON.put("msg", message);
        return JSONObject.toJSONString(JSON, (ValueFilter) (o, s, o1) -> o1 == null ? "" : o1);
    }

    public static String setMessage(String code, Object obj) {
        JSON.put("code", code);
        JSON.put("msg", obj);
        return JSONObject.toJSONString(JSON, (ValueFilter) (o, s, o1) -> o1 == null ? "" : o1);
    }

}
