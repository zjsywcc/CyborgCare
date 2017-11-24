package com.moecheng.cyborgcare.network.bean;

/**
 * 用于Gson解析的用户请求登陆返回数据
 * Created by wangchengcheng on 2017/11/22.
 */

public class UserInfoBean extends BaseBean {

    private String uid;

    private String name;

    private int age;

    private int sex;

    private String email;

    private double fatigue_index;

    private String token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getFatigue_index() {
        return fatigue_index;
    }

    public void setFatigue_index(double fatigue_index) {
        this.fatigue_index = fatigue_index;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
