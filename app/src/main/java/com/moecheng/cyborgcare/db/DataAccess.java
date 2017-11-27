package com.moecheng.cyborgcare.db;


import android.support.annotation.NonNull;

import com.moecheng.cyborgcare.AppContext;
import com.moecheng.cyborgcare.db.entity.User;
import com.moecheng.cyborgcare.greendao.UserDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by wangchengcheng on 2017/11/27.
 */

public class DataAccess {


    public static void initUser() {
        UserDao userDao = AppContext.getInstance().getUserDao();
        User user = new User();
        user.setUid(-1);
        user.setUsername("user_-1");
        user.setAge(0);
        user.setSexEnum(User.UserSex.USER_ALIEN);
        user.setEmail("");
        user.setFatigueIndex(0);
        user.setToken("-1");
        userDao.save(user);
    }

    public static void updateUser(User user) {
        UserDao userDao = AppContext.getInstance().getUserDao();
        userDao.update(user);
    }


    @NonNull
    public static User getUser() {
        UserDao userDao = AppContext.getInstance().getUserDao();
        Query<User> query = userDao.queryBuilder().orderAsc(UserDao.Properties.Id).build();
        List<User> userList = query.list();
        return userList.get(0);
    }

}
