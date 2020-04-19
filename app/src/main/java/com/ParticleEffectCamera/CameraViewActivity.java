package com.ParticleEffectCamera;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.FaceDetection.Box;
import com.FaceDetection.MTCNN;
import com.EffectSystem.Process;
import com.ParticleSystem.ParticleSystem;
import java.util.Vector;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;
import static org.opencv.imgproc.Imgproc.resize;

public class CameraViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener{
    String TAG="CameraViewActivity";
    private JavaCameraView mcameraView;
    private static int cameraIndex = 1;//前置1，后置0
    public static int option = 0;
    static int t = 0;//计数器，60一个循环
    //static int resizefactor = 2;
    MTCNN mtcnn;
    BitmapFactory.Options options = new BitmapFactory.Options();
    public static boolean recordpermission;
    //Test particle effect template
    public static int[][] config = new int[10][23];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        initLoadOpenCVLibs();//调用opencv库
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
        //初始化MTCNN
        mtcnn=new MTCNN(getAssets());
    }

    private void initLoadOpenCVLibs() {
        boolean success= OpenCVLoader.initDebug();
        if(success) {
            Log.i(TAG,"load library successfully");
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
                   t = 0;
                   for(int i = 0; i <10; i++){
                       config[i][22] = 0;
                   }
                   switch (id) {
                       case R.id.p1:
                           option = 1;
                           effecttype(0,3,0,1,2, 10,
                                   60,45,2,2,1,8,1,1,
                                   255,0,0,0,80,220,0,125,150);
                           effecttype(1,3,0,1,2, 7,
                                   60,45,2,1,1,4,1,1,
                                   50,170,50,100,80,120,70,200,200);
                           effecttype(2,3,0,1,2, 4,
                                   60,45,1,1,1,4,1,1,
                                   50,170,50,100,80,120,70,200,200);
                           effecttype(3,3,0,1,2, 10,
                                   60,45,2,2,1,8,1,1,
                                   255,0,0,0,80,220,0,170,190);
                           ParticleSystem.Configuration(config,2);
                           break;
                       case R.id.p2:
                           option = 2;
                           effecttype(0,3,0,1,1, 10,
                                   60,120,3,2,1,4,1,1,
                                   255,0,0,220,220,220,255,255,255);
                           ParticleSystem.Configuration(config,4);
                           break;
                       case R.id.p3:
                           option = 3;
                           effecttype(0,0,1,2,2, 20,
                                   120,60,2,3,1,10,0,0,
                                   255,0,0,255,255,255,255,255,255);
                           ParticleSystem.Configuration(config,2);
                           break;
                       case R.id.p4:
                           option = 4;
                           effecttype(0,2,0,1,0,4,
                                   120,60,2,10,0,5,0,0,
                                   255,255,0,255,255,255,255,255,255);
                           ParticleSystem.Configuration(config,2);
                           break;
                       case R.id.p5:
                           option = 5;
                           effecttype(0,4,2,1,2,8,
                                   120,30,2,20,1,8,1,2,
                                   255,255,0,255,255,255,255,255,255);
                           effecttype(1,4,2,1,2,8,
                                   120,30,2,25,1,8,1,2,
                                   255,255,255,0,0,0,30,20,80);
                           effecttype(2,4,2,0,2,8,
                                   120,30,2,15,1,8,1,2,
                                   255,255,255,170,30,180,60,120,90);
                           effecttype(3,4,2,1,2,16,
                                   120,30,2,15,1,8,1,2,
                                   0,50,50,230,190,100,233,199,90);
                           ParticleSystem.Configuration(config,1);
                           break;
                       case R.id.p6:
                           effecttype(0,5,2,1,2,100,
                                   120,120,2,0,1,8,1,2,
                                   0,50,50,230,190,100,233,199,90);
                           effecttype(1,5,2,1,2,200,
                                   120,120,2,0,1,8,1,2,
                                   0,50,50,230,190,100,233,199,90);
                           effecttype(2,5,2,2,2,400,
                                   120,120,2,1,1,8,1,2,
                                   0,50,50,230,190,100,233,199,90);
                           ParticleSystem.Configuration(config,4);
                           option = 6;
                           break;
                       case R.id.p7:
                           option = 7;
                           break;
                       case R.id.p8:
                           option = 8;
                           break;
                       case R.id.p9:
                           option = 9;
                           break;
                       case R.id.p10:
                           option = 10;
                           break;
                       default:
                           option = 0;
                           break;
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
//        方案一：缩放处理帧大小提升检测效率—— 前置FPS：25，后置FPS：30 方案二：无缩放保证图片质量—— 前置FPS：21，后置FPS：27
//        resize(frame,frame, new Size(frame.height() / resizefactor, frame.width()/ resizefactor),0,0, INTER_LINEAR);
        process(frame);
//        resize(frame,frame, new Size(frame.height()* resizefactor, frame.width()* resizefactor),0,0, INTER_LINEAR);
        transpose(frame,frame);//转置

        if(t>=60){
            t=0;
        } else {
            t++;
        }
        return frame;
    }

    private void effecttype(int groupNo,int shape_type,int velocity_type,int color_type,int keyposition_type,int vibration_type,
                            int initial_size,int duration,int particle_size,int velocity,int trajectory,int trajectory_length,int halo,int halo_size,
                            int particlecolor0,int particlecolor1, int particlecolor2,int trajectorycolor0,int trajectorycolor1,int trajectorycolor2,int halocolor0,int halocolor1,int halocolor2){
        //remove the last operation's data

            //Motion config
            config[groupNo][0] = shape_type;
            config[groupNo][1] = velocity_type;
            config[groupNo][2] = color_type;
            config[groupNo][3] = keyposition_type;
            config[groupNo][4] = vibration_type;
            //Particle config
            config[groupNo][5] = initial_size;
            config[groupNo][6] = duration;
            config[groupNo][7] = particle_size;
            config[groupNo][8] = velocity;
            config[groupNo][9] = trajectory;
            config[groupNo][10] = trajectory_length;
            config[groupNo][11] = halo;
            config[groupNo][12] = halo_size;
            //Particle color
            config[groupNo][13] = particlecolor0;
            config[groupNo][14] = particlecolor1;
            config[groupNo][15] = particlecolor2;
            //Trajectory color
            config[groupNo][16] = trajectorycolor0;
            config[groupNo][17] = trajectorycolor1;
            config[groupNo][18] = trajectorycolor2;
            //Halo color
            config[groupNo][19] = halocolor0;
            config[groupNo][20] = halocolor1;
            config[groupNo][21] = halocolor2;
            //Whether activated group?
            config[groupNo][22] = 1;
    }
    //处理帧图片逻辑
    private void process(Mat frame) {
        if(option == 10){
        } else {
            Bitmap bitmap = WelcomeActivity.localmap;
            bitmap=Bitmap.createScaledBitmap(bitmap,frame.width(),frame.height(),true);
            Utils.matToBitmap(frame,bitmap);
            Vector<Box> boxes=mtcnn.detectFaces(bitmap,150);//mtcnn()的作用结果为生成一系列Box类（结构）
            if(option == 0){
                Process.pureProcess(frame,bitmap,boxes);
            }
            Process.particleSystemProcess(frame,bitmap,boxes,t);
        }
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
            recordpermission = true;

        }
        mcameraView.setCameraIndex(cameraIndex);
        if(mcameraView != null) {
            mcameraView.disableView();
        }
        mcameraView.enableView();
    }
}
