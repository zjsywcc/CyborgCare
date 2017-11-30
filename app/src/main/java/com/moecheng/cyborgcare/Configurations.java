package com.moecheng.cyborgcare;

import java.util.UUID;

/**
 * Created by wangchengcheng on 2017/11/22.
 */

public class Configurations {

    /**
     * 是否进行debug输出日志信息
     */
    public static final boolean DEBUG = true;
    /**
     * 数据库及表名
     */
    public final static String DATABASE_NAME = "cyborgcare.db";
    public final static String USER_TABLE = "user_tb";
    public final static String MEASURE_TABLE = "measure_tb";
    public final static String ALARM_TABLE = "alarm_tb";
    public final static String VIBRATION_TABLE = "vibration_tb";
    public final static String SRC_TABLE = "src_tb";
    /**
     * 网络请求基址
     */
    public static final String REQUEST_URL = "http://192.168.198.244:8080";
    public static final String USER_URL = "/appuser";
    public static final String UPLOAD_URL = "/bluetooth";
    public static final String INFO_URL = "";
    /**
     * 蓝牙UUID
     */
    public static final UUID btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * 蓝牙服务名
     */
    public static final String btServerName = "CyborgCare_Android_Client";
    /**
     * shared preference的名字
     */
    public static final String PREFERENCE_NAME = "com.moecheng.cyborgcare.preference";
    /**
     * 用户头像本地文件路径
     */
    public static final String AVATAR_FILE_PATH = "/user/avatar.png";
    /**
     * 本地存储用户数据的文件夹名字
     */
    public static final String USER_DIR = "/user";

    public static final int SUCCESS = 200;
}
