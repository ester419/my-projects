package com.example.daily_cashbook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDBService;
import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.util.T;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ShowZhangActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvTime, tvType, tvName;
    TextView etRemark, etMoney;
    Cashbook object;
    ImageView ivImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_zhang);
        object = (Cashbook) getIntent().getSerializableExtra("obj");


        ivImg = findViewById(R.id.ivImg);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvTime = findViewById(R.id.tvTime);
        etRemark = findViewById(R.id.etRemark);
        etMoney = findViewById(R.id.etMoney);
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tvDel).setOnClickListener(this);

        updateUi();
    }


    private void updateUi() {
        tvName.setText(object.getType());
        tvType.setText(object.getInorout().equals("input") ? "收入" : "支出");
        etMoney.setText(object.getMoney());
        etRemark.setText(object.getComment());
        tvTime.setText(object.getTime());
        if (object.getImage().equals("")) {
            ivImg.setVisibility(View.GONE);
        } else {
            Picasso.with(ShowZhangActivity.this).load(new File(object.getImage())).into(ivImg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tvDel:
                CashbookDBService.getInstence().delete(object.getId());
                T.show("删除成功");
                finish();


                break;

        }
    }


}
