package com.moecheng.cyborgcare.bluetooth.frame;

/**
 * Created by wangchengcheng on 2017/12/21.
 */

public class Metadata {

    private String bluetoothAddress; // 蓝牙模块MAC地址
    private String userId;           // 用户ID
    private String username;         // 用户名
    private String sessionId;        // 用户登录session
    private int biosignalLen;        // 蓝牙数据包个数
    private long timestamp;          // 上发时间
}
