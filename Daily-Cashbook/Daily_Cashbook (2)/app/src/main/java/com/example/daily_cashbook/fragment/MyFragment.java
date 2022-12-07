package com.example.daily_cashbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.daily_cashbook.R;

import org.json.JSONException;


public class MyFragment extends Fragment {
    private int flag;    //表示这是第几个fragment:0 1 2 3
    private String content;

    // fragment
    private CashFragment outFragment, inFragment;
    private FragmentManager fragmentManager;
    private FragmentManager childFragmentManager;

    //cash 中的两个框子
    private View test01;
    private View test02;
    //cash中的两个textView
    private TextView textView01;
    private TextView textView02;
    int page=1;
    public MyFragment(int flag, String content) {
        this.flag = flag;
        this.content = content;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (this.flag == 0){
            //home
            view = inflater.inflate(R.layout.fg_content, container, false);
            TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
            txt_content.setText(content);
        }else if (this.flag == 1){
            //bash
            view = inflater.inflate(R.layout.fg_content, container, false);
            //设置view当中控件的属性数据
            TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
            txt_content.setText(content);
        }else if (this.flag == 2){
            fragmentManager = getFragmentManager();
            //cash 将支出和收入的页面 加载到container中
            view = inflater.inflate(R.layout.cash_content, container, false);
            test01 = view.findViewById(R.id.id_tab_outcash);
            test02 = view.findViewById(R.id.id_tab_incash);
            textView01 = view.findViewById(R.id.id_tab_outcash_tv);
            textView02 = view.findViewById(R.id.id_tab_incash_tv);

            page=1;
            //设置字体颜色
            textView01.setTextColor(getResources().getColor(R.color.text_blue));
            textView02.setTextColor(getResources().getColor(R.color.text_black));
            //将fragment 填充进去
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (inFragment !=null){
                fragmentTransaction.hide(inFragment);
            }
            if (outFragment == null){
                outFragment = new CashFragment(0, "这是支出页面啊！");
                fragmentTransaction.add(R.id.cash_content_fragment, outFragment);
            }else {
                fragmentTransaction.show(outFragment);
            }
            fragmentTransaction.commit();
        }else if (this.flag == 3){
            //mine
            view = inflater.inflate(R.layout.fg_content, container, false);
            TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
            txt_content.setText(content);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();
        //两个fragment
        //支出
        fragmentManager = getFragmentManager();


        if (test01 !=null){
            test01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    page=1;
                    //设置字体颜色
                    textView01.setTextColor(getResources().getColor(R.color.text_blue));
                    textView02.setTextColor(getResources().getColor(R.color.text_black));
                    //将fragment 填充进去
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (inFragment !=null){
                        fragmentTransaction.hide(inFragment);
                    }
                    if (outFragment == null){
                        outFragment = new CashFragment(0, "这是支出页面啊！");
                        fragmentTransaction.add(R.id.cash_content_fragment, outFragment);
                    }else {
                        fragmentTransaction.show(outFragment);
                    }
                    fragmentTransaction.commit();

                }
            });
        }
        if (test02 != null){
            //收入
            test02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    page=2;
                    //设置字体颜色
                    textView01.setTextColor(getResources().getColor(R.color.text_black));
                    textView02.setTextColor(getResources().getColor(R.color.text_blue));
                    //设置fragment
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (outFragment !=null){
                        fragmentTransaction.hide(outFragment);
                    }
                    if (inFragment == null){
                        inFragment = new CashFragment(1, "这是收入页面啊！");
                        fragmentTransaction.add(R.id.cash_content_fragment, inFragment);
                    }else {
                        fragmentTransaction.show(inFragment);
                    }
                    fragmentTransaction.commit();
                }
            });
        }
    }


    public void onActivityResult1(int requestCode, int resultCode, @Nullable Intent data) {

        try {
            if(page==1){
                outFragment.onActivityResult1(requestCode, resultCode, data);
            }else if(page==2){
                inFragment.onActivityResult1(requestCode, resultCode, data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}