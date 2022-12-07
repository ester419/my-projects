package com.example.daily_cashbook.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.activity.ShowZhangActivity;
import com.example.daily_cashbook.dbutils.CashbookDBService;
import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.T;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.ArrayList;
import java.util.Calendar;


public class CashListFragment extends Fragment implements View.OnClickListener, OnDateSetListener {


    View view;
    RecyclerView lv;
    TextView tvTime, tvOutput, tvInput, tvYu;
    Calendar calendar;
    int y, m, d;
    String time;
    ArrayList<Cashbook> array;
    int uid;
    MyAdapter adapter;
    TimePickerDialog mDialogAll;
    int type = 0;
    long times;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cash_list, container, false);
        lv = view.findViewById(R.id.lv);
        lv.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvYu = view.findViewById(R.id.tvYu);
        tvTime = view.findViewById(R.id.tvTime);
        tvTime.setOnClickListener(this);
        tvOutput = view.findViewById(R.id.tvOutput);
        tvInput = view.findViewById(R.id.tvInput);
        calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        time = y + "-" + ((m + 1) >= 10 ? (m + 1) : "0" + (m + 1));
        tvTime.setText("日期：" + time);
        uid = S.getU().getId();

        times = 1000 * 60 * 60 * 24 * 365L * 10;


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    //根据日期获取记账数据
    public void getData() {
        array = CashbookDBService.getInstence().search(uid, time);
        double in = 0, out = 0;
        for (int i = 0; i < array.size(); i++) {
            String inorout = array.get(i).getInorout();
            double money = Double.parseDouble(array.get(i).getMoney());
            if (inorout.equals("input")) {
                in += money;
            } else {
                out += money;
            }
        }
        tvInput.setText(in + "");
        tvOutput.setText(out + "");
        tvYu.setText("本月结余：" + (in - out));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MyAdapter();
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {


        new AlertDialog.Builder(getActivity()).setTitle("选择时间").setItems(new String[]{"按年查询", "按月查询", "日天查询"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                type = i;
                switch (i) {
                    case 0:
                        mDialogAll = new TimePickerDialog.Builder()
                                .setCallBack(CashListFragment.this)
                                .setCancelStringId("取消")
                                .setSureStringId("确定")
                                .setTitleStringId("TimePicker")
                                .setYearText("年")
                                .setMonthText("月")
                                .setDayText("日")
                                .setHourText("小时")
                                .setMinuteText("分钟")
                                .setCyclic(false)
                                .setMinMillseconds(System.currentTimeMillis() - times)
                                .setMaxMillseconds(System.currentTimeMillis())
                                .setCurrentMillseconds(System.currentTimeMillis())
                                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                                .setType(Type.YEAR)
                                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                                .setWheelItemTextSize(12)
                                .build();
                        mDialogAll.show(getChildFragmentManager(), "all");
                        break;
                    case 1:
                        mDialogAll = new TimePickerDialog.Builder()
                                .setCallBack(CashListFragment.this)
                                .setCancelStringId("取消")
                                .setSureStringId("确定")
                                .setTitleStringId("TimePicker")
                                .setYearText("年")
                                .setMonthText("月")
                                .setDayText("日")
                                .setHourText("小时")
                                .setMinuteText("分钟")
                                .setCyclic(false)
                                .setMinMillseconds(System.currentTimeMillis() - times)
                                .setMaxMillseconds(System.currentTimeMillis())
                                .setCurrentMillseconds(System.currentTimeMillis())
                                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                                .setType(Type.YEAR_MONTH)
                                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                                .setWheelItemTextSize(12)
                                .build();
                        mDialogAll.show(getChildFragmentManager(), "all");
                        break;
                    case 2:
                        mDialogAll = new TimePickerDialog.Builder()
                                .setCallBack(CashListFragment.this)
                                .setCancelStringId("取消")
                                .setSureStringId("确定")
                                .setTitleStringId("TimePicker")
                                .setYearText("年")
                                .setMonthText("月")
                                .setDayText("日")
                                .setHourText("小时")
                                .setMinuteText("分钟")
                                .setCyclic(false)
                                .setMinMillseconds(System.currentTimeMillis() - times)
                                .setMaxMillseconds(System.currentTimeMillis())
                                .setCurrentMillseconds(System.currentTimeMillis())
                                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                                .setType(Type.YEAR_MONTH_DAY)
                                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorPrimary))
                                .setWheelItemTextSize(12)
                                .build();
                        mDialogAll.show(getChildFragmentManager(), "all");
                        break;
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {

        String[] arr = T.getTime(millseconds).split("-");
        y = Integer.parseInt(arr[0]);
        m = Integer.parseInt(arr[1]);
        d = Integer.parseInt(arr[2]);
        if (type == 0) {


            time = y + "";
            tvTime.setText("日期：" + time);

        } else if (type == 1) {
            time = y + "-" + ((m) >= 10 ? (m) : "0" + (m));
            tvTime.setText("日期：" + time);
        } else if (type == 2) {
            time = y + "-" + ((m) >= 10 ? (m) : "0" + (m)) + "-" + ((d) >= 10 ? (d) : "0" + (d));
            tvTime.setText("日期：" + time);
        }
        getData();


    }

    class MyAdapter extends RecyclerView.Adapter<Holder> {


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item1, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ShowZhangActivity.class).putExtra("obj", array.get(position)));
                }
            });
            Cashbook object = array.get(position);
            String inorout = object.getInorout();
            holder.tvName.setText(object.getType());
            if (position > 0) {
                if (array.get(position - 1).getTime().split("-")[2].equals(object.getTime().split("-")[2])) {
                    holder.tvTime.setText(object.getTime());
                    holder.tvTime.setVisibility(View.GONE);
                    holder.ll.setVisibility(View.GONE);
                } else {
                    holder.tvTime.setText(object.getTime());
                    holder.tvTime.setVisibility(View.VISIBLE);
                    holder.ll.setVisibility(View.VISIBLE);
                }
            } else {
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.ll.setVisibility(View.VISIBLE);
                holder.tvTime.setText(object.getTime());
            }

            holder.tvMoney.setText((inorout.equals("input") ? "+" : "-") + object.getMoney());
            holder.tvRemark.setText(object.getComment());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return array.size();
        }


    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvTime, tvName, tvRemark, tvMoney;
        View ll;

        public Holder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tvTime);
            tvName = v.findViewById(R.id.tvName);
            tvRemark = v.findViewById(R.id.tvRemark);
            tvMoney = v.findViewById(R.id.tvMoney);
            ll = v.findViewById(R.id.ll);
        }

    }

}
