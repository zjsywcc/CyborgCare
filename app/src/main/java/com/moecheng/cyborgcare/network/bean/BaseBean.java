package com.moecheng.cyborgcare.network.bean;

/**
 * 用于Gson解析的基础Bean
 * Created by wangchengcheng on 2017/11/22.
 */

public class BaseBean {

    private int errorCode;
    private String error;

    public int getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setError(String error) {
        this.error = error;
    }

}
