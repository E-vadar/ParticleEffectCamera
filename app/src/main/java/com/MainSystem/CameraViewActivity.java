package com.MainSystem;

/**
 * Author: He Jingze
 * Description: Main camera view page and basic video processing logic
 */

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.KeyPointModule.FaceUtils;
import com.ProcessModule.RecordFileUtils;
import com.ProcessModule.RecordService;
import com.ProcessModule.RecordUtils;
import com.ProcessModule.RepositoryUtils;
import com.KeyPointModule.Box;
import com.KeyPointModule.MTCNN;
import com.ParticleModule.ParticleSystem;
import java.util.Vector;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;
import android.widget.Button;
import android.widget.LinearLayout;

public class CameraViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener{
    String TAG="CameraViewActivity";
    private JavaCameraView mcameraView;
    private static int cameraIndex = 1;
    public static int option = 0;
    static int t = 0;
    MTCNN mtcnn;
    BitmapFactory.Options options = new BitmapFactory.Options();
    public static boolean recording;
    public static int[][] config = new int[10][23];
    public static int config_time;
    Bitmap bitmap = WelcomeActivity.localmap;
    public static int width;
    public static int height;
    public static int DPI;
    private final int REQUEST_ALLOW = 100;
    Button effectbtn[] = new Button[20];
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordService.RecordBinder recordBinder = (RecordService.RecordBinder) service;
            RecordService recordService = recordBinder.getRecordService();
            RecordUtils.setScreenService(recordService);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        setEffectMenu();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mcameraView = findViewById(R.id.cv_camera_id);
        mcameraView.setVisibility(SurfaceView.VISIBLE);
        mcameraView.setCvCameraViewListener(this);
        mcameraView.setCameraIndex(0);
        mcameraView.enableView();
        mcameraView.enableFpsMeter();
        RadioButton backOption = findViewById(R.id.backCameraOption);
        RadioButton frontOption = findViewById(R.id.frontCameraOption);
        backOption.setOnClickListener(this);
        frontOption.setOnClickListener(this);
        backOption.setSelected(true);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap=Bitmap.createScaledBitmap(bitmap,720,960,true);
        mtcnn=new MTCNN(getAssets());
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        width = display.widthPixels;
        height = display.heightPixels;
        DPI = display.densityDpi;
        startService();
    }

    private void setEffectMenu(){
        int effectsize = WelcomeActivity.effectList.size();
        LinearLayout linear = (LinearLayout)findViewById(R.id.scroll);
        for(int i=0;i<effectsize;i++){
            Button btn = new Button(this);
            effectbtn[i] = btn;
            btn.setId(i);
            Drawable drawable = Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/download/" + WelcomeActivity.effectList.get(i) + ".png");
            btn.setBackground(drawable);
            btn.setHeight(200);
            btn.setWidth(75);
            btn.setText(WelcomeActivity.effectList.get(i));
            btn.setTextSize(12);
            btn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    option = 2;
                    cacheEffect(WelcomeActivity.effectList.get(btn.getId()));
                }
            });
            linear.addView(btn);
        }
    }

    private void startService() {
        Intent intent = new Intent(CameraViewActivity.this, RecordService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void process(Mat frame) {
        if(option == 0){
        } else {
            Utils.matToBitmap(frame,bitmap);
            Vector<Box> boxes=mtcnn.detectFaces(bitmap,150+cameraIndex*150);
            if(option == 1){
                try {
                    for (int i=0;i<boxes.size();i++){
                        FaceUtils.drawRect(bitmap,boxes.get(i).transform2Rect());
                        FaceUtils.drawPoints(bitmap,boxes.get(i).landmark);
                    }
                    Utils.bitmapToMat(bitmap,frame);
                }catch (Exception e){
                }
            } else {
                //The way to design effect image:
                //circle(frame,new Point(300,300),1000,new Scalar(0,0,0),-1);
                try {
                    for (int i=0;i<boxes.size();i++) {
                        ParticleSystem.runSystem(frame,boxes.get(i).landmark,t);
                        Log.v(TAG,"picture width"+ frame.height() + "picture height"+ frame.width());
                        Log.v(TAG,"Box Width"+ boxes.get(i).width()+"Box Height"+ boxes.get(i).height());
                    }
                }catch (Exception e){
                }
            }
        }
    }

    private void cacheEffect(String effectName){
        if(effectName.startsWith("C")){
            Toast.makeText(CameraViewActivity.this, "The new effect is coming soon！", Toast.LENGTH_SHORT).show();
            option = 0;
        } else if(effectName.startsWith("P")) {
            option = 0;
        } else if(effectName.startsWith("F")) {
            option = 1;
        } else if(effectName.startsWith("E")) {
            option = 2;
            t = 0;
            config_time = 0;
            for(int i = 0; i <10; i++){
                config[i][22] = 0;
            }
            option = 2;
            effectTest(0,1);
            effectTest(1,2);
            effectTest(2,3);
            effectTest(3,4);
            ParticleSystem.Configuration(config,config_time);
        } else {
            if(RepositoryUtils.download(2, effectName, this)){
                loadEffect(RepositoryUtils.ReadTxtFile(Environment.getExternalStorageDirectory()+"/download/" + effectName + ".txt"));
            } else {
                Toast.makeText(CameraViewActivity.this, "Downloading:" + effectName + ", reclick to start", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void effectTest(int groupNo, int size){
        int factor = 30;
            //Motion config
            config[groupNo][0] = 7;
            config[groupNo][1] = 0;
            config[groupNo][2] = 2;
            config[groupNo][3] = 0;
            config[groupNo][4] = 4 ;
            //Particle config
            config[groupNo][5] = 60;
            config[groupNo][6] = 70;
            config[groupNo][7] = 2;
            config[groupNo][8] = 2;
            config[groupNo][9] = 1;
            config[groupNo][10] = 7;
            config[groupNo][11] = 1;
            config[groupNo][12] = 1;
            //Particle color
            config[groupNo][13] = 255-size*factor;
            config[groupNo][14] = 23+size*factor;
            config[groupNo][15] = 0;
            //Trajectory color
            config[groupNo][16] = 220-size*factor;
            config[groupNo][17] = 95-size*factor;
            config[groupNo][18] = 0;
            //Halo color
            config[groupNo][19] = 220-size*factor;
            config[groupNo][20] = 215-size*factor;
            config[groupNo][21] = 0;
            //Whether activated group?
            config[groupNo][22] = 1;
            config_time = 1;

        //            Motion config
//            config[groupNo][0] = shape_type;
//            config[groupNo][1] = velocity_type;
//            config[groupNo][2] = color_type;
//            config[groupNo][3] = keyposition_type;
//            config[groupNo][4] = vibration_type;
//            Particle config
//            config[groupNo][5] = initial_size;
//            config[groupNo][6] = duration;
//            config[groupNo][7] = particle_size;
//            config[groupNo][8] = velocity;
//            config[groupNo][9] = trajectory;
//            config[groupNo][10] = trajectory_length;
//            config[groupNo][11] = halo;
//            config[groupNo][12] = halo_size;
//            Particle color
//            config[groupNo][13] = particlecolor0;
//            config[groupNo][14] = particlecolor1;
//            config[groupNo][15] = particlecolor2;
//            Trajectory color
//            config[groupNo][16] = trajectorycolor0;
//            config[groupNo][17] = trajectorycolor1;
//            config[groupNo][18] = trajectorycolor2;
//            Halo color
//            config[groupNo][19] = halocolor0;
//            config[groupNo][20] = halocolor1;
//            config[groupNo][21] = halocolor2;
//            Whether activated group?
//            config[groupNo][22] = 1;
    }

    private void loadEffect(String content){
        t = 0;
        config_time = 0;
        for(int i = 0; i <10; i++){
            config[i][22] = 0;
        }
        config_time = Integer.parseInt(content.substring(0,1));
        content = content.substring(2);
        String[] column = content.split("\n");
        for(int i = 0;i < column.length;i++){
            String[] line = column[i].split(",");
            for(int z = 0;z < line.length-1;z++){
                config[i][z]= Integer.valueOf(line[z+1]);
            }
            config[i][22] = 1;
        }
        ParticleSystem.Configuration(config,config_time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALLOW && data != null) {
            try {
                RecordUtils.setUpData(resultCode, data);
                String screenRecordFilePath = RecordUtils.getScreenRecordFilePath();
                if (screenRecordFilePath == null) {
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
                }
                Log.i("zlq", "onClick: " + screenRecordFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Record not allowed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        if(cameraIndex == 1) {
            flip(frame,frame, 1);
        }
        transpose(frame,frame);
        process(frame);
        transpose(frame,frame);
        if(t>=60){
            t=0;
        } else {
            t++;
        }
        return frame;
    }

    @Override
    protected void onStop() {
        super.onStop();
        RecordUtils.clear();
    }

    public void onPause() {
        super.onPause();
        if(mcameraView != null) {
            mcameraView.disableView();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if(mcameraView != null) {
            mcameraView.disableView();
        }
        unbindService(mConnection);
    }

    public void onResume() {
        super.onResume();
        if(mcameraView != null) {
            mcameraView.setCameraIndex(cameraIndex);
            mcameraView.enableFpsMeter();
            mcameraView.enableView();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        LinearLayout linear = (LinearLayout)findViewById(R.id.scroll);
        RadioGroup radio = (RadioGroup)findViewById(R.id.radioGroup);
        Button btn = (Button)findViewById(R.id.record_btn);
        if(id == R.id.frontCameraOption) {
            cameraIndex = 1;
        } else if(id == R.id.backCameraOption) {
            cameraIndex = 0;
        } else if(id == R.id.record_btn) {
            if(recording){
                RecordUtils.stopScreenRecord(CameraViewActivity.this);
                Log.i(TAG, "onClick: " + RecordUtils.getScreenRecordFilePath());
                Toast.makeText(CameraViewActivity.this, "Record stop", Toast.LENGTH_SHORT).show();
                recording = false;
                linear.setVisibility(View.VISIBLE);
                radio.setVisibility(View.VISIBLE);
                btn.setText("Record");
            } else {
                if (RecordFileUtils.getFreeMem(CameraViewActivity.this) < 100) {
                    Toast.makeText(CameraViewActivity.this, "Not enough space", Toast.LENGTH_SHORT).show();
                    return;
                }
                RecordUtils.startScreenRecord(CameraViewActivity.this, REQUEST_ALLOW);
                Toast.makeText(CameraViewActivity.this, "Record start", Toast.LENGTH_SHORT).show();
                recording = true;
                linear.setVisibility(View.INVISIBLE);
                radio.setVisibility(View.INVISIBLE);
                btn.setText("Stop");
            }
        }
        mcameraView.setCameraIndex(cameraIndex);
        if(mcameraView != null) {
            mcameraView.disableView();
        }
        mcameraView.enableView();
    }

}
