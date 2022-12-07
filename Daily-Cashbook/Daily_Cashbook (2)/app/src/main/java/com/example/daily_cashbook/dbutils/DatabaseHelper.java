package com.example.daily_cashbook.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String name = "daily_cashbook.db";   //数据库名称
    private static int version = 2;

    public DatabaseHelper(@Nullable Context context) {
        super(context, name, null, version);
    }

    //当数据库创建的时候，是第一次被执行，完成对数据库的表的创建
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table user(" +
                "id integer primary key," +
                "username varchar(50) not null," +
                "password varchar(50) not null," +
                "email varchar(50)," +
                "tel varchar(50)," +
                "icon varchar(100)," +
                "profile varchar(50)"+
                ")";    //创建user表
        db.execSQL(sql);
        sql = "create table cashbook(" +
                "id integer primary key," +
                "title varchar(50)," +
                "time varchar(50)," +
                "money varchar(50)," +
                "type varchar(50)," +
                "comment varchar(50)," +
                "image varchar(50)," +
                "inorout varchar(5)," +
                "user_id integer," +
                "foreign key(user_id) references user(id)" +
                ")";    //创建cashbook表，并设置外键
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
