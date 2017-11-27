package com.moecheng.cyborgcare.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by wangchengcheng on 2017/11/27.
 */
@Entity
public class User {

    //主键
    @Id(autoincrement = true)
    private Long id;

    private int uid;

    private String username;

    private int age;

    private int sex;

    private String email;

    private double fatigueIndex;

    private String token;

    @Generated(hash = 2045943459)
    public User(Long id, int uid, String username, int age, int sex, String email, double fatigueIndex, String token) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.age = age;
        this.sex = sex;
        this.email = email;
        this.fatigueIndex = fatigueIndex;
        this.token = token;
    }


    @Generated(hash = 586692638)
    public User() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", email='" + email + '\'' +
                ", fatigueIndex=" + fatigueIndex +
                ", token='" + token + '\'' +
                '}';
    }

    public enum UserSex {
        USER_MALE,
        USER_FEMALE,
        USER_ALIEN,
        Undefine
    }

    public void setSexEnum(UserSex userSex) {
        switch (userSex) {
            case USER_MALE:
                this.setSex(1);
            case USER_FEMALE:
                this.setSex(0);
            case USER_ALIEN:
                this.setSex(2);
            case Undefine:
                this.setSex(-1);
            default:
                this.setSex(-1);
        }
    }

    public UserSex getSexEnum() {
        switch (this.getSex()) {
            case 1:
                return UserSex.USER_MALE;
            case 0:
                return UserSex.USER_FEMALE;
            case 2:
                return UserSex.USER_ALIEN;
            case -1:
                return UserSex.Undefine;
            default:
                return UserSex.Undefine;
        }
    }
}
