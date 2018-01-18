package com.moecheng.cyborgcare.bluetooth.frame;

/**
 * Created by wangchengcheng on 2017/12/21.
 */

public class Biosignal {

    private byte[] frameHeader; // 帧头1字节 0xFF
    private int frameNum; // 帧序号1字节 防止乱序
    private byte[] breathArray; // 呼吸信号2字节
    private byte[] emgArray; // 肌电信号2字节
    private float bodyTemp; // 体温1字节
    private byte[] eegArray; // 脑电信号24字节 包含8种波段信号 每种3字节
    private byte[] frameTail; // 帧尾1字节 最后1bit为奇偶校验位

}
