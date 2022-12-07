package com.example.daily_cashbook.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.daily_cashbook.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDBService;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.SelectImg;
import com.example.daily_cashbook.util.T;
import com.example.daily_cashbook.view.NoScrollGridView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class CashFragment extends Fragment implements View.OnClickListener {
    //cash 界面分别加载不同的fragment
    private int flag;   //0 支出， 1 收入
    private String content;     //显示内容

    //当前用户
    private User user;


    String[] typeList = {"交通,日用,其他", "工资,奖金,其他"};

    NoScrollGridView gv;
    ArrayList<String> list;

    TextView tvType, tvTime;
    EditText etMoney, etRemark;
    Calendar calendar;
    int y, m, d;
    String time = "";
    String img = "";
    SelectImg selectImg;
    ImageView ivImg;

    public CashFragment(int flag, String content) {
        this.flag = flag;
        this.content = content;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前用户,从全局变量中获取
        MetaApplication metaApplication = (MetaApplication) getActivity().getApplicationContext();
        user = metaApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        list = new ArrayList<>();
        String[] types = typeList[flag].split(",");
        selectImg = new SelectImg(getActivity());

        calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        time = y + "-" + ((m + 1) >= 10 ? (m + 1) : "0" + (m + 1)) + "-" + (d >= 10 ? d : "0" + d);


        if (this.flag == 0) {    //支出页面
            //支出
            view = inflater.inflate(R.layout.cash_content_outcash, container, false);

        } else if (this.flag == 1) {  //收入页面
            //收入
            view = inflater.inflate(R.layout.cash_content_incash, container, false);
        }

        tvType = view.findViewById(R.id.tvType);
        tvTime = view.findViewById(R.id.tvTime);
        etMoney = view.findViewById(R.id.etMoney);
        etRemark = view.findViewById(R.id.etRemark);
        gv = view.findViewById(R.id.gv);
        for (int i = 0; i < types.length; i++) {
            list.add(types[i]);
        }
        tvTime.setText(time);
        tvType.setText(types[0]);
        view.findViewById(R.id.llTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        y = year;
                        m = monthOfYear;
                        d = dayOfMonth;
                        time = y + "-" + ((m + 1) >= 10 ? (m + 1) : "0" + (m + 1)) + "-" + (d >= 10 ? d : "0" + d);
                        tvTime.setText(time);
                    }
                }, y, m, d);
                dialog.show();
            }
        });
        ivImg = view.findViewById(R.id.ivImg);
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etRemark.getText().toString();
                String money = etMoney.getText().toString();
                String type = tvType.getText().toString();
                if (money.equals("")) {
                    T.show("金额不能为空");
                } else {
                    if (comment.equals("")) {
                        comment = type;
                    }


                    CashbookDBService.getInstence().save(S.getU().getId(), type, flag == 0 ? "支出" : "收入", time, comment, money, img, flag == 0 ? "output" : "input");
                    T.show("记账成功");
                    img = "";
                    etMoney.setText("");
                    etRemark.setText("");
                    ivImg.setImageResource(R.mipmap.addd);

                }
            }
        });


        initGV();


        return view;
    }

    @Override
    public void onClick(View v) {

    }

    void initGV() {
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = list.get(position);
                tvType.setText(name);

                // getActivity().finish();

            }
        });


        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.item_gv, null);
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(list.get(position));
                return view;
            }
        });
    }

    /**
     * 拍照和图库选完后结果在此处理
     */
    public void onActivityResult1(int requestCode, int resultCode, Intent data) throws JSONException {
        if (!(resultCode == RESULT_OK)) {
            return;
        }
        if (requestCode == SelectImg.CAMARA) {
            img = selectImg.getCamaraPath();


        } else if (requestCode == SelectImg.IMG) {
            img = selectImg.getPhotoPath(data.getData());
        }
        Picasso.with(getActivity()).load(new File(img)).resize(100, 100).centerInside()
                .placeholder(R.mipmap.def_not_login_avatar).error(R.mipmap.def_not_login_avatar)
                .into(ivImg);


    }
}