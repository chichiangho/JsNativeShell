package com.seeyon.cmp.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class GsonUtil {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    public static <T> String toJson(T t) {
        return gson.toJson(t);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || "".equals(json)) {
            return null;
        }
        T t = null;
        try {
            t = gson.fromJson(json, clazz);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T fromJson(String json, Type type) {
        if (json == null || "".equals(json)) {
            return null;
        }
        T t = null;
        try {
            t = gson.fromJson(json, type);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T fromJson(JSONObject json, Class<T> clazz) {
        return fromJson(json.toString(), clazz);
    }

    public static <T> T fromJson(JSONObject json, Type type) {
        return fromJson(json.toString(), type);
    }
}
