package com.moecheng.cyborgcare.network.bean.response;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class BaseResponse {

    private String msg;

    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
