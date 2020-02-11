package com.ParticleEffectCamera;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import org.opencv.android.OpenCVLoader;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    //初始化加载一些常用素材资源
    BitmapFactory.Options options = new BitmapFactory.Options();
    public static ArrayList<Bitmap> listeye = new ArrayList<Bitmap>();
    public static ArrayList<Bitmap> listmouth = new ArrayList<Bitmap>();
    public static Bitmap glass,white,localmap;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //界面转载
            Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
            startActivities(new Intent[]{intent});  //start跳转
            finish();//结束欢迎界面活动
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //读取素材数据
        glass = BitmapFactory.decodeResource(getResources(),R.drawable.glass,options);
        white = BitmapFactory.decodeResource(getResources(),R.drawable.white,options);
        localmap = BitmapFactory.decodeResource(getResources(),R.drawable.a36799,options);
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f0));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f1));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f2));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f3));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f4));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f5));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f6));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f7));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f8));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f9));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f10));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f11));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f12));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f13));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f14));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f15));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f16));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f17));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f18));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f19));
        listeye.add(BitmapFactory.decodeResource(getResources(),R.drawable.f20));

        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s0));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s1));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s2));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s3));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s4));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s5));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s6));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s7));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s8));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s9));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s10));
        listmouth.add(BitmapFactory.decodeResource(getResources(),R.drawable.s11));
        //读取完成

        ActionBar actionBar = getSupportActionBar();     //取消标题头actionbar
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取相机拍摄读写权限
        if (Build.VERSION.SDK_INT >= 23) {
            //版本判断
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        initLoadOpenCVLibs();//调用opencv库
        //延迟发送信息2000Ms即2秒
        handler.sendMessageDelayed(Message.obtain(), 2500);

    }
    private void initLoadOpenCVLibs() {
        boolean success= OpenCVLoader.initDebug();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
