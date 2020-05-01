package com.MainSystem;

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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;
import com.ProcessModule.RepositoryUtil;
import org.opencv.android.OpenCVLoader;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    BitmapFactory.Options options = new BitmapFactory.Options();
    public static Bitmap localmap;
    public static ArrayList<String> effectList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initEffectList();
            Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
            startActivities(new Intent[]{intent});
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initEffectList();
        localmap = BitmapFactory.decodeResource(getResources(),R.drawable.a36799,options);
        ActionBar actionBar = getSupportActionBar();     //取消标题头actionbar
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA}, 1);
        }
        initLoadOpenCVLibs();
        handler.sendMessageDelayed(Message.obtain(),5000);
    }

    private void initLoadOpenCVLibs() {
        boolean success= OpenCVLoader.initDebug();
    }

    private void initEffectList(){
        if(RepositoryUtil.download(0, "List", this)){
            RepositoryUtil.ReadListFile(Environment.getExternalStorageDirectory()+"/download/List.txt");
            for(int i=0;i<WelcomeActivity.effectList.size();i++){
                downloadEffectImage(WelcomeActivity.effectList.get(i));
            }
            Toast.makeText(WelcomeActivity.this, "更新粒子特效目录完成！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WelcomeActivity.this, "正在更新粒子特效目录！", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadEffectImage(String effectName){
        RepositoryUtil.download(1, effectName, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RepositoryUtil.delete(Environment.getExternalStorageDirectory()+"/download/","List.txt");
        handler.removeCallbacksAndMessages(null);
    }
}
