package com.moecheng.cyborgcare;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.AndroidRuntimeException;

import com.moecheng.cyborgcare.database.Database;
import com.moecheng.cyborgcare.database.DatabaseOpenHelper;
import com.moecheng.cyborgcare.local.Preference;
import com.moecheng.cyborgcare.local.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class AppContext extends Application {

    private static Application instance;
    private static Database DB;
    private static Preference preference;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
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

    public static Database getDB() {
        if (DB != null) {
            return DB;
        }
        if (instance == null) {
            throw new AndroidRuntimeException("The application context is not initialized!");
        }
        PackageInfo info;
        PackageManager pm = instance.getPackageManager();
        try {
            info = pm.getPackageInfo(instance.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new AndroidRuntimeException("The application context is not initialized!");
        }

        File dbFile = instance.getDatabasePath(Configurations.DATABASE_NAME);
        String dbFilePath = dbFile.getAbsolutePath();
        String DATABASES_DIR = dbFilePath.substring(0, dbFilePath.length() - Configurations.DATABASE_NAME.length() - 1);
        File dir = new File(DATABASES_DIR);
        if (!dir.exists()) {
            try {
                if (!dir.mkdir()) {
                    throw new AndroidRuntimeException("The database can not be copied to the /data directory!");
                }
            } catch (Exception e) {
                throw new AndroidRuntimeException("The database can not be copied to the /data directory!");
            }
        }
        try {
            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    InputStream is = instance.getResources().openRawResource(R.raw.cyborgcare_db); //欲导入的数据库
                    FileOutputStream fos = new FileOutputStream(dbFile);
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    is.close();
                } else {
                    throw new AndroidRuntimeException("The database can not be copied to the /data directory!");
                }
            }
        } catch (IOException e) {
            throw new AndroidRuntimeException("The database can not be copied to the /data directory!");
        }
        DB = new DatabaseOpenHelper(instance, info.versionCode).getDatabase();
        if (DB == null) {
            throw new AndroidRuntimeException("Can not open or create the database!");
        }
        return DB;
    }


}
