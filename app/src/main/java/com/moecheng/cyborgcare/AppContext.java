package com.moecheng.cyborgcare;

import android.app.Application;
import android.util.AndroidRuntimeException;

import com.moecheng.cyborgcare.db.DataAccess;
import com.moecheng.cyborgcare.db.Database;
import com.moecheng.cyborgcare.greendao.UserDao;
import com.moecheng.cyborgcare.local.Preference;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class AppContext extends Application {

    private static AppContext instance;
    private static Preference preference;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Database.getInstance().initDatabase(this);
        DataAccess.initUser();
    }

    public static AppContext getInstance() {
        return instance;
    }

    public UserDao getUserDao() {
        return Database.getInstance().getUserDao();
    }

    public static Preference getPreference() {
        if (instance == null) {
            throw new AndroidRuntimeException("The application context is not initialized!");
        }
        if (preference == null) {
            preference = new Preference(instance);
        }
        return preference;
    }

    public static int dp2px(float dpvalue) {
        return (int) (dpvalue
                * getInstance().getResources().getDisplayMetrics().density + 0.5f);
    }




}
