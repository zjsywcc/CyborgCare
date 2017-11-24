package com.moecheng.cyborgcare.util;

import com.moecheng.cyborgcare.Configurations;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class Log {

    public static void i(String tag, String msg) {
        if (Configurations.DEBUG)   android.util.Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (Configurations.DEBUG)   android.util.Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (Configurations.DEBUG)   android.util.Log.v(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Configurations.DEBUG)   android.util.Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (Configurations.DEBUG)   android.util.Log.w(tag, msg);
    }
}
