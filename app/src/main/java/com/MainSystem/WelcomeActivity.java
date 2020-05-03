package com.MainSystem;

/**
 * Author: He Jingze
 * Description: Welcome page and basic resources or library loading
 */

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
import android.widget.Toast;
import com.ProcessModule.RepositoryUtils;

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
        initLoadOpenCVLibs();
        localmap = BitmapFactory.decodeResource(getResources(),R.drawable.white,options);
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA}, 1);
        }
        handler.sendMessageDelayed(Message.obtain(),3000);
    }

    private void initLoadOpenCVLibs() {
        OpenCVLoader.initDebug();
    }

    private void initEffectList(){
        if(RepositoryUtils.download(0, "List", this)){
            RepositoryUtils.ReadListFile(Environment.getExternalStorageDirectory()+"/download/List.txt");
            for(int i=0;i<WelcomeActivity.effectList.size();i++){
                downloadEffectImage(WelcomeActivity.effectList.get(i));
            }
            Toast.makeText(WelcomeActivity.this, "Updating finished！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WelcomeActivity.this, "Updating effects！", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadEffectImage(String effectName){
        RepositoryUtils.download(1, effectName, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RepositoryUtils.delete(Environment.getExternalStorageDirectory()+"/download/","List.txt");
        handler.removeCallbacksAndMessages(null);
    }

}
