package com.moecheng.cyborgcare.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class Database {

    private SQLiteDatabase db;

    public Database(SQLiteDatabase db) {
        this.db = db;
    }

    public QueryBuilder query() {
        return new QueryBuilder(db);
    }

    public SQLiteDatabase getSQLDataBase() {
        return db;
    }
}
