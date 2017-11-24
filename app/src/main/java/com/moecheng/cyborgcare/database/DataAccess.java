package com.moecheng.cyborgcare.database;

import com.moecheng.cyborgcare.AppContext;
import com.moecheng.cyborgcare.Configurations;
import com.moecheng.cyborgcare.local.User;

/**
 * Created by wangchengcheng on 2017/11/23.
 */

public class DataAccess {

    /**
     * 获取当前用户的uid
     */
    public static int getUid() {
        Result result = null;
        try {
            result = AppContext.getDB().query()
                    .table(Configurations.USER_TABLE)
                    .field("uid")
                    .first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            User.getInstance().setSex(User.UserSex.USER_ALIEN);
            int ok = AppContext.getDB().query()
                    .table(Configurations.USER_TABLE)
                    .add("uid", -1)
                    .add("username", "user_-1")
                    .add("age", 0)
                    .add("sex", User.getInstance().getSexInt())
                    .add("email", "")
                    .add("fatigue_index", 0)
                    .add("token", "-1")
                    .insert();
            return ok>0?-1:User.UID_UNDEFINE;
        } else {
            return Integer.valueOf(result.getString("uid"));
        }
    }

    /**
     * 根据数据库信息初始化用户
     */
    public static boolean initUserInfo() {
        Result result = AppContext.getDB().query()
                .table(Configurations.USER_TABLE)
                .field("uid")
                .field("username")
                .field("age")
                .field("sex")
                .field("email")
                .field("fatigue_index")
                .field("token")
                .first();
        if (result != null) {
            User.getInstance().setUid(Integer.valueOf(result.getString("uid")));
            User.getInstance().setUserName(result.getString("username"));
            User.getInstance().setAge(result.getInt("age"));
            User.getInstance().setSexInt(result.getInt("sex"));
            User.getInstance().setEmail(result.getString("email"));
            User.getInstance().setFatigueIndex(result.getDouble("fatigue_index"));
            User.getInstance().setToken(result.getString("token"));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新用户数据库信息
     */
    public static boolean updateUserInfo() {
        int result = AppContext.getDB().query()
                .table(Configurations.USER_TABLE)
                .add("username", User.getInstance().getUserName())
                .add("age", User.getInstance().getAge())
                .add("sex", User.getInstance().getSexInt())
                .add("email", User.getInstance().getEmail())
                .add("fatigue_index", User.getInstance().getFatigueIndex())
                .add("token", User.getInstance().getToken())
                .where("uid").equal(String.valueOf(User.getInstance().getUid()))
                .update();
        if (result == 0) {
            AppContext.getDB().query().table(Configurations.USER_TABLE).delete();
            result = AppContext.getDB().query()
                    .table(Configurations.USER_TABLE)
                    .add("uid", String.valueOf(User.getInstance().getUid()))
                    .add("username", User.getInstance().getUserName())
                    .add("age", User.getInstance().getAge())
                    .add("sex", User.getInstance().getSexInt())
                    .add("email", User.getInstance().getEmail())
                    .add("fatigue_index", User.getInstance().getFatigueIndex())
                    .add("token", User.getInstance().getToken())
                    .insert();
        }
        return result != 0;
    }
}
