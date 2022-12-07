package com.example.daily_cashbook.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class LogoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);


        getPermission();
    }

    private void go() {
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(LogoActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }.start();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        go();
    }


    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    //获取权限
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            go();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
        }

    }
}
