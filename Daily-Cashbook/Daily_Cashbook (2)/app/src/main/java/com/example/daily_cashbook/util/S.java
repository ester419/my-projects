package com.example.daily_cashbook.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.daily_cashbook.entity.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class S {


    public static boolean isLogin() {
        SharedPreferences preferences = T.context.getSharedPreferences("isLogin", T.context.MODE_PRIVATE);
        return preferences.getBoolean("isLogin", false);

    }

    public static void setLogin(boolean isLogin) {
        SharedPreferences preferences = T.context.getSharedPreferences("isLogin", T.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", isLogin).commit();

    }


    public static void setU(User u) {
        SharedPreferences preferences = T.context.getSharedPreferences("user", T.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", new Gson().toJson(u)).commit();

    }

    public static User getU() {
        SharedPreferences preferences = T.context.getSharedPreferences("user", T.context.MODE_PRIVATE);
        return new Gson().fromJson(preferences.getString("user", ""), User.class);


    }


}
