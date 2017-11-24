package com.moecheng.cyborgcare.network.bean.response;

/**
 * Created by wangchengcheng on 2017/11/24.
 */

public class LoginResponse extends BaseResponse {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public static class Data {
        /**
         * id
         */
        private String id;

        /**
         * 客户端用户id
         */
        private int uid;

        /**
         * 用户名
         */
        private String username;


        /**
         * 密码
         */
        private String password;


        /**
         * 年龄
         */
        private int age;


        /**
         * 性别
         */
        private int sex;

        /***
         * 邮箱
         */
        private String email;


        /**
         * 疲劳指数
         */
        private double fatigue_index;

        /**
         * Token
         * @return
         */
        private String token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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
}
