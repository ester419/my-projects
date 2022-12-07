package com.example.daily_cashbook.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDBService;
import com.example.daily_cashbook.util.MyValueFormatter;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.T;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class BaoBiaoFragment extends Fragment implements View.OnClickListener, OnDateSetListener {
    View view;


    TextView tvTime;
    String inorout = "output";
    JSONArray array2, arrTop;
    Calendar calendar;
    int y, m, d;
    String time;
    ListView lv;
    int uid;
    PieChart chart2;
    MyAdapter adapter;
    TextView tv1, tv2;

    TimePickerDialog mDialogAll;
    int type = 0;
    long times;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_baobiao, container, false);

        chart2 = view.findViewById(R.id.chart2);
        uid = S.getU().getId();
        tvTime = view.findViewById(R.id.tvTime);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        lv = view.findViewById(R.id.lv);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        time = y + "-" + ((m + 1) >= 10 ? (m + 1) : "0" + (m + 1));
        tvTime.setText("日期：" + time + "月");
        times = 1000 * 60 * 60 * 24 * 365L * 10;

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        get();
    }


    public void get() {

        arrTop = CashbookDBService.getInstence().searchMoney(uid, time, inorout);

        initPie();
        paihang(time);


    }

    void paihang(String time) {
        array2 = CashbookDBService.getInstence().searchPaihang(uid, time, inorout);
        if (adapter == null) {
            adapter = new MyAdapter();
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    void initPie() {
        chart2.clear();


        chart2.setUsePercentValues(false);
        chart2.getDescription().setEnabled(false);
        chart2.setTransparentCircleRadius(0f);


        chart2.setHoleRadius(0f);


        chart2.setRotationAngle(0);
        // enable rotation of the chart2 by touch
        chart2.setRotationEnabled(true);
        chart2.setHighlightPerTapEnabled(true);

        // chart2.setUnit(" €");
        // chart2.setDrawUnitsInChart(true);

        // add a selection listener
        // chart2.setOnChartValueSelectedListener(this);


        chart2.animateY(1400, Easing.EaseInOutQuad);
        // chart2.spin(2000, 0, 360);

        Legend l = chart2.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setEnabled(true);


        ArrayList<PieEntry> entries = new ArrayList<>();
        double totals = 0;
        for (int i = 0; i < arrTop.length(); i++) {
            JSONObject object = arrTop.optJSONObject(i);
            totals += object.optDouble("total");
        }


        //[{"total":"444","time":"2019-04-15"}]
        for (int i = 0; i < arrTop.length(); i++) {
            JSONObject object = arrTop.optJSONObject(i);

            PieEntry barEntry = new PieEntry((float) (object.optDouble("total") / totals * 100), object.optString("type") + " " + object.optDouble("total") + "元");
            entries.add(barEntry);

        }
        chart2.setEntryLabelColor(Color.BLACK);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(1f);
        // dataSet.setSelectionShift(5f);


        //设置初始旋转角度
        chart2.setRotationAngle(-15);


        // add a lot of colors


        int endColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark);
        int endColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_purple);
        int endColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark);
        int endColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark);
        int endColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark);
        int endColor6 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_light);
        int endColor7 = ContextCompat.getColor(getActivity(), android.R.color.tab_indicator_text);


        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(endColor1);
        colors.add(endColor2);
        colors.add(endColor3);
        colors.add(endColor4);
        colors.add(endColor5);
        colors.add(endColor6);
        colors.add(endColor7);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setUsingSliceColorAsValueLineColor(true);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);// 和四周相隔一段距离,显示数据

        chart2.setDrawEntryLabels(true);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);//
        chart2.setData(data);
        chart2.setDrawSliceText(false);
        // undo all highlights
        chart2.highlightValues(null);

        chart2.invalidate();
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv1) {
            inorout = "output";
            tv1.setTextColor(Color.WHITE);
            tv1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv2.setBackgroundColor(Color.WHITE);
            tv2.setTextColor(Color.BLACK);
            get();
        } else if (v.getId() == R.id.tv2) {
            inorout = "input";
            tv2.setTextColor(Color.WHITE);
            tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv1.setBackgroundColor(Color.WHITE);
            tv1.setTextColor(Color.BLACK);
            get();

        } else {


            new AlertDialog.Builder(getActivity()).setTitle("选择时间").setItems(new String[]{"按年查询", "按月查询", "日天查询"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    type = i;
                    switch (i) {
                        case 0:
                            mDialogAll = new TimePickerDialog.Builder()
                                    .setCallBack(BaoBiaoFragment.this)
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
                                    .setCallBack(BaoBiaoFragment.this)
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
                                    .setCallBack(BaoBiaoFragment.this)
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
        get();




    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return array2.length();
        }

        @Override
        public Object getItem(int position) {
            return array2.opt(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item2, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            JSONObject object = array2.optJSONObject(position);
            holder.tvName.setText(object.optString("type"));
            holder.tvTime.setText("第" + (position + 1) + "名");
            holder.tvMoney.setText(object.optString("total") + "元");
            holder.tvRemark.setVisibility(View.INVISIBLE);

            return convertView;
        }
    }

    class Holder {
        TextView tvTime, tvName, tvRemark, tvMoney;

        public Holder(View v) {
            tvTime = v.findViewById(R.id.tvTime);
            tvName = v.findViewById(R.id.tvName);
            tvRemark = v.findViewById(R.id.tvRemark);
            tvMoney = v.findViewById(R.id.tvMoney);
        }

    }
}
