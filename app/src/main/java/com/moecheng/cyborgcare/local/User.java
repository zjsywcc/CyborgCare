package com.moecheng.cyborgcare.local;

import android.content.Context;

import com.moecheng.cyborgcare.database.DataAccess;
import com.moecheng.cyborgcare.network.bean.UserInfoBean;


/**
 * Created by wangchengcheng on 2017/11/22.
 */

public class User {

    public static final int UID_UNDEFINE = 1002;

    private static final String DEFAULT_USER_NAME_PREFIX = "User_";


    public enum UserSex {
        USER_MALE,
        USER_FEMALE,
        USER_ALIEN,
        Undefine
    }

    private int uid;
    private String userName;
    private int age;
    private UserSex sex;
    private String email;
    private double fatigueIndex;
    private String token;

    private User() {

    }

    private volatile static User user;

    public static User getInstance() {
        if(user == null) {
            synchronized (User.class) {
                if(user == null) {
                    user = new User();
                }
            }
        }
        return user;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserSex getSex() {
        return sex;
    }

    public int getSexInt() {
        switch (sex) {
            case USER_MALE:
                return 0;
            case USER_FEMALE:
                return 1;
            case USER_ALIEN:
                return 2;
            default:
                return -1;
        }
    }

    public void setSex(UserSex sex) {
        this.sex = sex;
    }

    public void setSexInt(int sexInt) {
        switch (sexInt) {
            case 0:
                sex = UserSex.USER_MALE;
                break;
            case 1:
                sex = UserSex.USER_FEMALE;
                break;
            case 2:
                sex = UserSex.USER_ALIEN;
                break;
            default:
                sex = UserSex.Undefine;
                break;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getFatigueIndex() {
        return fatigueIndex;
    }

    public void setFatigueIndex(double fatigueIndex) {
        this.fatigueIndex = fatigueIndex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void initUserInfo() {
        uid = DataAccess.getUid();
        if (uid == UID_UNDEFINE) {
            uid = -1;
            userName = DEFAULT_USER_NAME_PREFIX + uid;
            age = -1;
            sex = UserSex.Undefine;
            email = "";
            fatigueIndex = 0.0;
            token = "-1";
        } else {
            DataAccess.initUserInfo();
            if (userName == null || userName.length() == 0) {
                userName = DEFAULT_USER_NAME_PREFIX + uid;
            }
        }
    }


    public static void syncUserInfo(UserInfoBean bean, Context context) {
        User.UserSex sex;
        if (bean.getSex() == 0) {
            sex = User.UserSex.USER_MALE;
        } else if (bean.getSex() == 1) {
            sex = User.UserSex.USER_FEMALE;
        } else {
            sex = User.UserSex.USER_ALIEN;
        }
        edit()
                .setUid(bean.getUid()!=null?Integer.valueOf(bean.getUid()):User.getInstance().uid)
                .setUsername(bean.getName())
                .setAge(bean.getAge())
                .setSex(sex)
                .setEmail(bean.getEmail())
                .setFatigueIndex(bean.getFatigue_index())
                .setToken(bean.getToken())
                .commit();
    }

    public static Editor edit() {
        return new Editor();
    }

    public static class Editor {

        private int uid;
        private String userName;
        private int age;
        private UserSex sex;
        private String email;
        private double fatigueIndex;
        private String token;


        public Editor setUid(int uid) {
            this.uid = uid;
            return this;
        }

        public Editor setUsername(String userName) {
            this.userName = userName;
            return this;
        }

        public Editor setAge(int age) {
            this.age = age;
            return this;
        }

        public Editor setSex(UserSex userSex) {
            this.sex = userSex;
            return this;
        }

        public Editor setEmail(String email) {
            this.email = email;
            return this;
        }

        public Editor setFatigueIndex(double fatigueIndex) {
            this.fatigueIndex = fatigueIndex;
            return this;
        }


        public Editor setToken(String token) {
            this.token = token;
            return this;
        }

        public boolean commit() {
            User.getInstance().setUid(this.uid);
            User.getInstance().setUserName(this.userName);
            User.getInstance().setSex(this.sex);
            User.getInstance().setEmail(this.email);
            User.getInstance().setAge(this.age);
            User.getInstance().setToken(this.token);
            if (!DataAccess.updateUserInfo()) {
                User.getInstance().initUserInfo();
                return false;
            }
            return true;
        }
    }
}
