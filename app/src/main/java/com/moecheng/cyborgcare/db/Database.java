package com.moecheng.cyborgcare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.greendao.DaoMaster;
import com.moecheng.cyborgcare.greendao.DaoSession;
import com.moecheng.cyborgcare.greendao.UserDao;

/**
 * Created by wangchengcheng on 2017/11/27.
 */

public class Database {

    private volatile static Database instance;

    private DaoMaster daoMaster;

    private DaoSession daoSession;

    private Database() {

    }

    public static Database getInstance() {
        if(instance == null) {
            synchronized (Database.class) {
                if(instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public void initDatabase(Context context) {
        /**初始化数据库*/
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.
                DevOpenHelper(context, Configurations.USER_TABLE, null);

        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }


    public DaoSession getDaoSession() {
        return daoSession;
    }

    public UserDao getUserDao() {
        return daoSession.getUserDao();
    }

}
