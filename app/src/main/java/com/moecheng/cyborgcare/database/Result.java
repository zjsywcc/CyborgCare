package com.moecheng.cyborgcare.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class Result {

    private Map<String, Object> resultMap;

    public Result() {
        resultMap = new HashMap<>();
    }

    public void put(String key, Object value) {
        resultMap.put(key, value);
    }

    public int getInt(String key) {
        return (int) resultMap.get(key);
    }

    public long getLong(String key) {
        return (long) resultMap.get(key);
    }

    public short getShort(String key) {
        return (short) resultMap.get(key);
    }

    public float getFloat(String key) {
        return (float) resultMap.get(key);
    }

    public double getDouble(String key) {
        return (double) resultMap.get(key);
    }

    public String getString(String key) {
        return (String) resultMap.get(key);
    }
}
