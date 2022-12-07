package com.example.daily_cashbook.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.daily_cashbook.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.activity.ChangePasswordActivity;
import com.example.daily_cashbook.activity.LoginActivity;
import com.example.daily_cashbook.dbutils.UserDBService;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.SelectImg;
import com.example.daily_cashbook.util.T;
import com.example.daily_cashbook.view.RoundImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeCenterFragment extends Fragment implements View.OnClickListener {


    View view;
    RoundImageView iv;
    User object;
    SelectImg selectImg;
    TextView tvUser, tvPhone, tvEmail, tvProfire;
    EditText et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        object = ((MetaApplication) getActivity().getApplication()).getUser();
        selectImg = new SelectImg(getActivity());
        view = inflater.inflate(R.layout.fragment_me_center, container, false);
        tvUser = view.findViewById(R.id.tvUser);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvProfire = view.findViewById(R.id.tvProfire);
        iv = view.findViewById(R.id.iv);
        iv.setOnClickListener(this);
        view.findViewById(R.id.rlEmail).setOnClickListener(this);
        view.findViewById(R.id.rlPhone).setOnClickListener(this);
        view.findViewById(R.id.rlProfire).setOnClickListener(this);
        view.findViewById(R.id.rlPswd).setOnClickListener(this);
        view.findViewById(R.id.tvExit).setOnClickListener(this);

        String icon = object.getIcon();
        if (!icon.equals("")) {
            Picasso.with(getActivity()).load(new File(icon)).resize(100, 100).centerInside()
                    .placeholder(R.mipmap.def_not_login_avatar).error(R.mipmap.def_not_login_avatar)
                    .into(iv);
        }
        tvUser.setText(object.getUserName());
        tvPhone.setText(object.getTel());
        tvEmail.setText(object.getEmail());
        tvProfire.setText(object.getProfile());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:
                new android.app.AlertDialog.Builder(// 实例化了一个对话框
                        getActivity()).setTitle("添加图片").setItems(new String[]{"拍照", "图库"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    selectImg.takeCamara();

                                } else if (which == 1) {
                                    selectImg.takePhoto();

                                }

                            }

                        }).setNegativeButton("取消", null).show();
                break;

            case R.id.tvExit:
                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("您确定要退出此账号吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        S.setLogin(false);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).setNegativeButton("取消", null).show();

                break;

            case R.id.rlEmail:
                et = new EditText(getActivity());
                et.setText(object.getEmail());
                et.setSelection(et.getText().length());
                new AlertDialog.Builder(getActivity()).setTitle("修改邮箱").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = et.getText().toString();
                        if (str.equals("")) {
                            T.show("输入框的内容不能为空");
                        } else {
                            object.setEmail(str);
                            tvEmail.setText(str);
                            UserDBService.getInstence().update(object);

                        }
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.rlPhone:
                et = new EditText(getActivity());
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(object.getTel());
                et.setSelection(et.getText().length());
                new AlertDialog.Builder(getActivity()).setTitle("修改手机号").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = et.getText().toString();
                        if (str.length() != 11) {
                            T.show("手机号的长度为11位");
                        } else {
                            object.setTel(str);
                            tvPhone.setText(str);
                            UserDBService.getInstence().update(object);

                        }
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.rlProfire:
                et = new EditText(getActivity());
                et.setText(object.getProfile());
                et.setSelection(et.getText().length());
                new AlertDialog.Builder(getActivity()).setTitle("修改简介").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = et.getText().toString();
                        if (str.equals("")) {
                            T.show("输入框的内容不能为空");
                        } else {
                            object.setProfile(str);
                            tvProfire.setText(str);
                            UserDBService.getInstence().update(object);

                        }
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.rlPswd:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;


        }
    }

    /**
     * 拍照和图库选完后结果在此处理
     */
    public void onActivityResult1(int requestCode, int resultCode, Intent data) throws JSONException {
        if (!(resultCode == RESULT_OK)) {
            return;
        }
        if (requestCode == SelectImg.CAMARA) {
            String path = selectImg.getCamaraPath();
            object.setIcon(path);
            Picasso.with(getActivity()).load(new File(path)).resize(100, 100).centerInside()
                    .placeholder(R.mipmap.def_not_login_avatar).error(R.mipmap.def_not_login_avatar)
                    .into(iv);
            UserDBService.getInstence().update(object);

        } else if (requestCode == SelectImg.IMG) {
            String path = selectImg.getPhotoPath(data.getData());
            object.setIcon(path);
            Picasso.with(getActivity()).load(new File(path)).resize(100, 100).centerInside()
                    .placeholder(R.mipmap.def_not_login_avatar).error(R.mipmap.def_not_login_avatar)
                    .into(iv);
            UserDBService.getInstence().update(object);
        }
        ((MetaApplication) getActivity().getApplication()).setUser(object);


    }
}
