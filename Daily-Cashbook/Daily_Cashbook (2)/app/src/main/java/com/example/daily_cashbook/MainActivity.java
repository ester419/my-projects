package com.example.daily_cashbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.daily_cashbook.dbutils.CashbookDBService;
import com.example.daily_cashbook.fragment.BaoBiaoFragment;
import com.example.daily_cashbook.fragment.CashListFragment;
import com.example.daily_cashbook.fragment.MeCenterFragment;
import com.example.daily_cashbook.fragment.MyFragment;
import com.example.daily_cashbook.util.S;
import com.example.daily_cashbook.util.T;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 阿里图标库：https://www.iconfont.cn/collections/index?spm=a313x.7781069.1998910419.7&type=4

    private TextView txt_topbar;
    private TextView txt_home;
    private TextView txt_dash;
    private TextView txt_cash;
    private TextView txt_mine;
    private FrameLayout ly_content;
    private CashListFragment fragment1;
    private BaoBiaoFragment fragment2;
    private MyFragment fragment3;
    private MeCenterFragment fragment4;
    private FragmentManager fragmentManager;
    private int cur = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bindViews();
        txt_home.performClick();    //默认进去选择第一项

        if (!CashbookDBService.getInstence().isZhang(S.getU().getId(), T.getDate())) {
            notification();
        }
    }

    private void bindViews() {
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_home = (TextView) findViewById(R.id.rb_home);
        txt_dash = (TextView) findViewById(R.id.rb_dash);
        txt_cash = (TextView) findViewById(R.id.rb_cash);
        txt_mine = (TextView) findViewById(R.id.rb_mine);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_home.setOnClickListener(this);
        txt_dash.setOnClickListener(this);
        txt_cash.setOnClickListener(this);
        txt_mine.setOnClickListener(this);
    }

    private void setSelected() {
        txt_home.setSelected(false);
        txt_dash.setSelected(false);
        txt_cash.setSelected(false);
        txt_mine.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (fragment1 != null) {
            fragmentTransaction.hide(fragment1);
        }
        if (fragment2 != null) {
            fragmentTransaction.hide(fragment2);
        }
        if (fragment3 != null) {
            fragmentTransaction.hide(fragment3);
        }
        if (fragment4 != null) {
            fragmentTransaction.hide(fragment4);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //首先隐藏所有的fragment
        hideAllFragment(fragmentTransaction);
        //判断点击的是哪个按钮
        switch (v.getId()) {
            case R.id.rb_home:
                cur = 1;
                setSelected();
                txt_home.setSelected(true);
                if (fragment1 == null) {
                    fragment1 = new CashListFragment();
                    fragmentTransaction.add(R.id.ly_content, fragment1);
                } else {
                    fragment1.getData();
                    fragmentTransaction.show(fragment1);
                }
                break;
            case R.id.rb_dash:
                cur = 2;
                setSelected();
                txt_cash.setSelected(true);
                if (fragment2 == null) {
                    fragment2 = new BaoBiaoFragment();
                    fragmentTransaction.add(R.id.ly_content, fragment2);
                } else {
                    fragmentTransaction.show(fragment2);
                }
                break;
            case R.id.rb_cash:  // 支出和收入的情况
                cur = 3;
                setSelected();
                txt_dash.setSelected(true);
                if (fragment3 == null) {
                    fragment3 = new MyFragment(2, "第三个fragment");
                    fragmentTransaction.add(R.id.ly_content, fragment3);
                } else {
                    fragmentTransaction.show(fragment3);
                }
                break;
            case R.id.rb_mine:
                cur = 4;
                setSelected();
                txt_mine.setSelected(true);
                if (fragment4 == null) {
                    fragment4 = new MeCenterFragment();
                    fragmentTransaction.add(R.id.ly_content, fragment4);
                } else {
                    fragmentTransaction.show(fragment4);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (cur == 3) {
                fragment3.onActivityResult1(requestCode, resultCode, data);
            } else if (cur == 4) {
                fragment4.onActivityResult1(requestCode, resultCode, data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    MediaPlayer mPlayer;

    void notification() {
        Notification notification;
        Intent intent = new Intent(this, MainActivity.class);
        //点击通知栏消息跳转页
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //创建通知消息管理类
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建渠道
            String id = getPackageName() + "my_channel" + System.currentTimeMillis();
            String name = "app";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            //设置图片,通知标题,发送时间,提示方式等属性
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, id);
            builder.setContentTitle("温馨提示")  //标题
                    .setContentText("您今日还没有记账")   //内容
                    .setSubText("您今日还没有记账")     //内容下面的一小段文字
                    .setTicker("您今日还没有记账")      //收到信息后状态栏显示的文字信息
                    .setWhen(System.currentTimeMillis())    //系统显示时间
                    .setSmallIcon(R.mipmap.logo)     //收到信息后状态栏显示的小图标
                    .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                    // .setLargeIcon(LargeBitmap)//大图标
                    .setDefaults(Notification.DEFAULT_ALL)    //设置默认的三色灯与振动器
                    .setAutoCancel(true);       //设置点击后取消Notification
            builder.setContentIntent(pendingIntent);    //绑定PendingIntent对象

            notification = builder.build();
            manager.notify(1, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)//创建通知消息实例
                    .setContentTitle("温馨提示")
                    .setContentText("您今日还没有记账")
                    .setWhen(System.currentTimeMillis())//通知栏显示时间
                    .setSmallIcon(R.mipmap.logo)//通知栏小图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))//通知栏下拉是图标
                    .setContentIntent(pendingIntent)//关联点击通知栏跳转页面
                    .setPriority(NotificationCompat.PRIORITY_MAX)//设置通知消息优先级
                    .setAutoCancel(true)//设置点击通知栏消息后，通知消息自动消失

                    .setDefaults(NotificationCompat.DEFAULT_ALL); //通知栏提示音、震动、闪灯等都设置为默认

            notification = builder.setStyle(new NotificationCompat.BigTextStyle().bigText("您今日还没有记账"))
                    .build();
            manager.notify(1, notification);
        }


        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.d);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        });


    }
}