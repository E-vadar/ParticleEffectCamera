package com.ParticleEffectCamera;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;
import com.EffectSystem.RepositoryUtil;
import org.opencv.android.OpenCVLoader;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    //初始化加载一些常用素材资源
    BitmapFactory.Options options = new BitmapFactory.Options();
    public static Bitmap localmap;
    public static ArrayList<String> effectList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initEffectList();
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
        localmap = BitmapFactory.decodeResource(getResources(),R.drawable.a36799,options);
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA}, 1);
        }
        initLoadOpenCVLibs();//调用opencv库
        initEffectList();
        //延迟发送信息2000Ms即2秒
        handler.sendMessageDelayed(Message.obtain(), 2500);
    }
    private void initLoadOpenCVLibs() {
        boolean success= OpenCVLoader.initDebug();
    }

    private void initEffectList(){
        if(RepositoryUtil.fileIsExists(Environment.getExternalStorageDirectory()+"/download/List.txt")){
            RepositoryUtil.ReadListFile(Environment.getExternalStorageDirectory()+"/download/List.txt");
            Toast.makeText(WelcomeActivity.this, "更新粒子特效目录完成！", Toast.LENGTH_SHORT).show();
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://q91np8f4n.bkt.clouddn.com/List.txt"));
            request.setDestinationInExternalPublicDir("/download/","List.txt");
            DownloadManager downloadManager= (DownloadManager)getSystemService(CameraViewActivity.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            Toast.makeText(WelcomeActivity.this, "正在更新粒子特效目录！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RepositoryUtil.delete(Environment.getExternalStorageDirectory()+"/download/","List.txt");
        handler.removeCallbacksAndMessages(null);
    }
}
