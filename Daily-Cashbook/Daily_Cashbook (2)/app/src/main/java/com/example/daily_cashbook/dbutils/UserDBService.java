package com.example.daily_cashbook.dbutils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.T;


/**
 * 数据库操作类
 *
 * @author Administrator
 */
public class UserDBService {

    private static UserDBService mInstence;
    private DatabaseHelper helper;
    private String TABLE_NAME = "user";

    /**
     * 登录
     *
     * @return
     */
    public User search(String user, String pswd) {
        Cursor cursor = helper.getReadableDatabase().query(TABLE_NAME, null, "username = ? and password = ?", new String[]{user, pswd}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            User object = new User();
            object.setId(cursor.getInt(cursor.getColumnIndex("id")));
            object.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            object.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            object.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            object.setTel(cursor.getString(cursor.getColumnIndex("tel")));
            object.setProfile(cursor.getString(cursor.getColumnIndex("profile")));
            object.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
            return object;


        }
        return null;
    }


    /**
     * 判断是否已注册
     *
     * @param username
     * @return
     */
    public boolean isExit(String username) {
        Cursor cursor = helper.getReadableDatabase().query(TABLE_NAME, null, "username = ?", new String[]{username}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            return true;

        }
        return false;
    }


    /**
     * 保存
     */
    public boolean save(String username, String password, String email, String tel, String profile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("tel", tel);
        contentValues.put("profile", profile);
        contentValues.put("icon", "");
        long result = helper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }


    public boolean update(User object) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", object.getPassword());
        contentValues.put("email", object.getEmail());
        contentValues.put("tel", object.getTel());
        contentValues.put("profile", object.getProfile());
        contentValues.put("icon", object.getIcon());
        long result = helper.getWritableDatabase().update(TABLE_NAME, contentValues, "id = ?", new String[]{object.getId() + ""});
        T.show("修改成功");
        if (result > 0) {
            S.setU(object);
            return true;
        } else {
            return false;
        }
    }


    public static UserDBService getInstence() {
        if (mInstence == null) {
            synchronized (UserDBService.class) {
                if (mInstence == null) {
                    mInstence = new UserDBService(T.context);
                }
            }
        }
        return mInstence;
    }

    public UserDBService(Context context) {
        close();
        helper = new DatabaseHelper(context);
    }

    private synchronized void close() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

}
