package com.example.daily_cashbook.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserDBService;
import com.example.daily_cashbook.entity.User;


/**
 * 修改密码界面
 *
 * @author Administrator
 */
public class ChangePasswordActivity extends AppCompatActivity {

    LinearLayout ll_back;
    EditText et_yuan_password;
    EditText et_new_password;
    EditText et_new_password_again;
    TextView tv_xg;
    String yuan_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        yuan_password = ((MetaApplication) getApplication()).getUser().getPassword();
        et_yuan_password = (EditText) findViewById(R.id.change_password_editText_yuan_password);
        et_new_password = (EditText) findViewById(R.id.change_password_editText_new_password);
        et_new_password_again = (EditText) findViewById(R.id.change_password_editText_new_password_again);
        ll_back = (LinearLayout) findViewById(R.id.layout_back);
        ll_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        tv_xg = (TextView) findViewById(R.id.tv_xg);
        tv_xg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String new_password = et_new_password.getText().toString();//获取输入框输入的内容
                String new_password_again = et_new_password_again.getText().toString();
                String yuan = et_yuan_password.getText().toString();
                if (!yuan_password.equals(yuan)) {//各种判断
                    Toast.makeText(ChangePasswordActivity.this, "原密码输入不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new_password.length() < 1 || new_password_again.length() < 1) {
                    Toast.makeText(ChangePasswordActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!new_password.equals(new_password_again)) {
                    Toast.makeText(ChangePasswordActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                User object = ((MetaApplication) getApplication()).getUser();
                object.setPassword(new_password);
                UserDBService.getInstence().update(object);
                ((MetaApplication) getApplication()).setUser(object);
                finish();


            }
        });

    }

}
