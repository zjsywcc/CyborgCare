package com.moecheng.cyborgcare.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moecheng.cyborgcare.Configurations;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context, int version) {
        super(context, Configurations.DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 返回一个数据库操作工具类
     * @return 数据库操作工具类
     */
    public Database getDatabase() {
        return new Database(getWritableDatabase());
    }
}
