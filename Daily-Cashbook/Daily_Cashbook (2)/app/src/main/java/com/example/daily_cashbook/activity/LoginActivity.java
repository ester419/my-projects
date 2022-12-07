package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.MainActivity;
import com.example.daily_cashbook.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserDBService;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.T;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUser, etPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btnRegster).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        etUser = findViewById(R.id.etUser);
        etPswd = findViewById(R.id.etPswd);
        if (S.isLogin()) {
            Login(S.getU().getUserName(), S.getU().getPassword());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:


                String user = etUser.getText().toString();
                String pswd = etPswd.getText().toString();
                if (user.equals("") || pswd.equals("")) {
                    T.show("输入框的内容不能为空");
                    return;
                }
                Login(user, pswd);

                break;
            case R.id.btnRegster:
                startActivity(new Intent(LoginActivity.this, RegsterActivity.class));
                break;

        }
    }

    void Login(String user, String pswd) {

        User object = UserDBService.getInstence().search(user, pswd);
        if (object == null) {
            T.show("账号或密码输入有误");
        } else {
            MetaApplication metaApplication = (MetaApplication) getApplicationContext();
            metaApplication.setUser(object);
            S.setU(object);//缓存登录信息
            S.setLogin(true);//缓存登录状态
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }


    }

}
