package com.ParticleEffectCamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import android.util.Log;
import android.widget.Toast;
import com.EffectSystem.RecordFileUtils;
import com.EffectSystem.RecordService;
import com.EffectSystem.RecordUtils;
import com.EffectSystem.RepositoryUtil;
import com.FaceDetection.Box;
import com.FaceDetection.MTCNN;
import com.ParticleSystem.ParticleSystem;
import java.util.Vector;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;

import static org.opencv.imgproc.Imgproc.circle;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import android.widget.Button;
import android.widget.LinearLayout;

public class CameraViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener{
    String TAG="CameraViewActivity";
    private JavaCameraView mcameraView;
    private static int cameraIndex = 1;//前置1，后置0
    public static int option = 0;
    static int t = 0;//计数器，60一个循环
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
    Button effectbtn[] = new Button[15];
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //启动service
            RecordService.RecordBinder recordBinder = (RecordService.RecordBinder) service;
            RecordService recordService = recordBinder.getRecordService();
            //这个其实是传值在使用的activity中拿到Service
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
        initLoadOpenCVLibs();//调用opencv库
        setEffectMenu();
        //三行 摄像头权限相关
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mcameraView = findViewById(R.id.cv_camera_id);
        mcameraView.setVisibility(SurfaceView.VISIBLE);
        mcameraView.setCvCameraViewListener(this); // setup frame listener
        mcameraView.setCameraIndex(0);
        mcameraView.enableView();
        mcameraView.enableFpsMeter();
        //前置、后置摄像头按钮初始化
        RadioButton backOption = findViewById(R.id.backCameraOption);
        RadioButton frontOption = findViewById(R.id.frontCameraOption);
        backOption.setOnClickListener(this);
        frontOption.setOnClickListener(this);
        backOption.setSelected(true);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap=Bitmap.createScaledBitmap(bitmap,720,960,true);
        //初始化MTCNN
        mtcnn=new MTCNN(getAssets());
        //获取机型数据
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        width = display.widthPixels;
        height = display.heightPixels;
        DPI = display.densityDpi;
        startService();
    }

    private void setEffectMenu(){
        Log.v(TAG,"mark1"+WelcomeActivity.effectList);
        int effectsize = WelcomeActivity.effectList.size();
        LinearLayout linear = (LinearLayout)findViewById(R.id.scroll);
        for(int i=0;i<effectsize;i++){
            Button btn = new Button(this);
            effectbtn[i] = btn;
            btn.setText(WelcomeActivity.effectList.get(i));
            Drawable drawable = Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/download/" + WelcomeActivity.effectList.get(i) + ".png");
            btn.setBackground(drawable);
            btn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    option = 2;
                    cacheEffect(btn.getText().toString());
                }
            });
            linear.addView(btn);
        }
    }

    //开启录制服务
    private void startService() {
        Intent intent = new Intent(CameraViewActivity.this, RecordService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    //装载openCV
    private void initLoadOpenCVLibs() {
        boolean success= OpenCVLoader.initDebug();
        if(success) {
            Log.i(TAG,"load library successfully");
        }
    }

    //处理帧图片逻辑
    private void process(Mat frame) {
        if(option == 0){
            //Pure
        } else {
            //Face detect
            Utils.matToBitmap(frame,bitmap);
            Vector<Box> boxes=mtcnn.detectFaces(bitmap,150+cameraIndex*150);//mtcnn()的作用结果为生成一系列Box类（结构）
            if(option == 1){
                //Draw Face ROI
                pureProcess(frame,bitmap,boxes);
            } else {
                //Draw Particle Effect
                //circle(frame,new Point(300,300),1000,new Scalar(255,255,255),-1);
                particleSystemProcess(frame,boxes,t);
            }
        }
    }

    //Download particle effect templates from web server
    private void cacheEffect(String effectName){
        if(effectName.startsWith("C")){
            Toast.makeText(CameraViewActivity.this, "敬请期待！", Toast.LENGTH_SHORT).show();
        } else {
            String effectFullName = effectName + ".txt";
            if(RepositoryUtil.fileIsExists(Environment.getExternalStorageDirectory()+"/download/" + effectFullName)){
                loadEffect(RepositoryUtil.ReadTxtFile(Environment.getExternalStorageDirectory()+"/download/" + effectFullName));
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://q91np8f4n.bkt.clouddn.com/template/" + effectFullName));
                request.setDestinationInExternalPublicDir("/download/", effectFullName);
                DownloadManager downloadManager= (DownloadManager)getSystemService(CameraViewActivity.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
                Toast.makeText(CameraViewActivity.this, "开始下载:" + effectName + ",请再次点击启动模板", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void effectLoad(int groupNo,int size){
            //Motion config
            config[groupNo][0] = 6;
            config[groupNo][1] = 0;
            config[groupNo][2] = 0;
            config[groupNo][3] = 2;
            config[groupNo][4] = 4;
            //Particle config
            config[groupNo][5] = 60;
            config[groupNo][6] = 55;
            config[groupNo][7] = 2;
            config[groupNo][8] = size;
            config[groupNo][9] = 1;
            config[groupNo][10] = 8;
            config[groupNo][11] = 1;
            config[groupNo][12] = 2;
            //Particle color
            config[groupNo][13] = 255-size*15;
            config[groupNo][14] = 192-size*15;
            config[groupNo][15] = 203-size*15;
            //Trajectory color
            config[groupNo][16] = 220-size*15;
            config[groupNo][17] = 140-size*15;
            config[groupNo][18] = 130-size*15;
            //Halo color
            config[groupNo][19] = 0;
            config[groupNo][20] = 0;
            config[groupNo][21] = 0;
            //Whether activated group?
            config[groupNo][22] = 1;
            config_time = 2;

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

        //Delete former effect template data
        t = 0;
        config_time = 0;
        for(int i = 0; i <10; i++){
            config[i][22] = 0;
        }

        //Set effect template data
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

        //Configure particle system
        ParticleSystem.Configuration(config,config_time);
    }

    private void pureProcess(Mat frame,Bitmap bm,Vector<Box> boxes){
        try {
            for (int i=0;i<boxes.size();i++){
                com.FaceDetection.Utils.drawRect(bm,boxes.get(i).transform2Rect());
                com.FaceDetection.Utils.drawPoints(bm,boxes.get(i).landmark);
            }
            Utils.bitmapToMat(bm,frame);
        }catch (Exception e){
        }
    }

    private void particleSystemProcess(Mat frame,Vector<Box> boxes,int t){
        try {
            for (int i=0;i<boxes.size();i++) {
                ParticleSystem.runSystem(frame,boxes.get(i).landmark,t);
                Log.v(TAG,"picture width"+ frame.height() + "picture height"+ frame.width());
                Log.v(TAG,"Box Width"+ boxes.get(i).width()+"Box Height"+ boxes.get(i).height());
            }
        }catch (Exception e){
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALLOW && data != null) {
            try {
                //设置数据，在用户允许后 调用了开始录屏的方法
                RecordUtils.setUpData(resultCode, data);
                //拿到路径
                String screenRecordFilePath = RecordUtils.getScreenRecordFilePath();
                if (screenRecordFilePath == null) {
                    Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CameraViewActivity.this, "" + screenRecordFilePath, Toast.LENGTH_SHORT).show();
                Log.i("zlq", "onClick: " + screenRecordFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "禁止录屏", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_view_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();
        if(id == R.id.PureCamera){
            option = 0;
        } else if (id == R.id.MTCNN) {
            option = 1;
        } else if (id == R.id.Test){
            t = 0;
            config_time = 0;
            for(int i = 0; i <10; i++){
                config[i][22] = 0;
            }
            option = 2;
            effectLoad(0,1);
            effectLoad(1,2);
            effectLoad(2,3);
            effectLoad(3,4);
            ParticleSystem.Configuration(config,config_time);
        } else {
            option = 2;
            cacheEffect(title);
        }
        return super.onOptionsItemSelected(item);
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
        if(cameraIndex == 1) { // 前置摄像头
            flip(frame,frame, 1);//Y轴翻转
        }
        transpose(frame,frame);//转置
        process(frame);
        transpose(frame,frame);//转置
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
        //在对用户可见不可交互的时候防止异常
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
        if(id == R.id.frontCameraOption) {
            cameraIndex = 1;
        } else if(id == R.id.backCameraOption) {
            cameraIndex = 0;
        } else if(id == R.id.record_btn) {
            if(recording){
                RecordUtils.stopScreenRecord(CameraViewActivity.this);//停止
                Log.i(TAG, "onClick: " + RecordUtils.getScreenRecordFilePath());
                recording = false;
            } else {
                if (RecordFileUtils.getFreeMem(CameraViewActivity.this) < 100) {
                    Toast.makeText(CameraViewActivity.this, "手机内存不足,请清理后再进行录屏", Toast.LENGTH_SHORT).show();
                    return;
                }
                //开始录屏录音
                RecordUtils.startScreenRecord(CameraViewActivity.this, REQUEST_ALLOW);
                recording = true;
            }
        }
        mcameraView.setCameraIndex(cameraIndex);
        if(mcameraView != null) {
            mcameraView.disableView();
        }
        mcameraView.enableView();
    }
}
