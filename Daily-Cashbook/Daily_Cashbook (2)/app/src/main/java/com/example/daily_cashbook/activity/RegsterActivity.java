package com.example.daily_cashbook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserDBService;
import com.example.daily_cashbook.util.T;


public class RegsterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUser, etPswd, etTel, etEmail, etProfire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regster);
        findViewById(R.id.regster_layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTel = findViewById(R.id.etTel);
        etUser = findViewById(R.id.etUser);
        etPswd = findViewById(R.id.etPswd);
        etEmail = findViewById(R.id.etEmail);
        etProfire = findViewById(R.id.etProfire);
        findViewById(R.id.btnRegster).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String user = etUser.getText().toString();
        String pswd = etPswd.getText().toString();
        String tel = etTel.getText().toString();
        String email = etEmail.getText().toString();
        String profire = etProfire.getText().toString();
        if (user.equals("") || pswd.equals("") || profire.equals("") || tel.equals("") || email.equals("")) {
            T.show("输入框的内容不能为空");
            return;
        }
        if (tel.length() != 11) {
            T.show("手机号的长度为11位");
            return;
        }

        regster(user, pswd, email, tel, profire);


    }

    /**
     * 注册
     *
     * @param user
     * @param pswd
     * @param email
     * @param tel
     * @param profile
     */
    void regster(String user, String pswd, String email, String tel, String profile) {
        if (UserDBService.getInstence().isExit(user)) {
            T.show("账号已注册");
        } else {
            UserDBService.getInstence().save(user, pswd, email, tel, profile);
            T.show("注册成功");
            finish();
        }

    }


}
