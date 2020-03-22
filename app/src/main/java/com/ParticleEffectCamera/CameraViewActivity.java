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
import org.opencv.core.Size;
import android.util.Log;
import com.FaceDetection.Box;
import com.FaceDetection.MTCNN;
import com.EffectSystem.Process;
import com.ParticleSystem.ParticleSystem;
import java.util.Vector;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.resize;



public class CameraViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener{
    String TAG="CameraViewActivity";
    private JavaCameraView mcameraView;
    private static int cameraIndex = 0;//前置1，后置0
    public static int option = 0;
    static int t = 0;//计数器，60一个循环
    //static int resizefactor = 2;
    MTCNN mtcnn;
    BitmapFactory.Options options = new BitmapFactory.Options();


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
                   switch (id) {
                       case R.id.glasses:
                           option = 1;
                           t = 0;
                           break;
                       case R.id.circle:
                           option = 2;
                           t = 0;
                           break;
                       case R.id.mouth:
                           option = 3;
                           t = 0;
                           break;
                       case R.id.eye:
                           option = 4;
                           t = 0;
                           break;
                       case R.id.test1:
                           option = 5;
                           t = 0;
                           break;
                       case R.id.particlefire:
                           option = 6;
                           t = 0;
                           //初始化粒子系统
                           ParticleSystem.initialize(270,30);
                           ParticleSystem.ptcConfig();
                           break;
                       case R.id.particlemouth:
                           option = 7;
                           t = 0;
                           //初始化粒子系统
                           ParticleSystem.initialize(600,40);
                           ParticleSystem.ptcConfig();
                           break;
                       case R.id.raw:
                           option = 8;
                           t = 0;
                           break;
                       default:
                           option = 0;
                           t = 0;
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

    //处理帧图片逻辑
    private void process(Mat frame) {
        if(option == 8){

        } else {
            Bitmap bitmap = WelcomeActivity.localmap;
            bitmap=Bitmap.createScaledBitmap(bitmap,frame.width(),frame.height(),true);
            Utils.matToBitmap(frame,bitmap);
            Vector<Box> boxes=mtcnn.detectFaces(bitmap,150);//mtcnn()的作用结果为生成一系列Box类（结构）
            if (option==0)
                Process.pureProcess(frame,bitmap,boxes);
            else if (option==1)
                Process.glassProcess(frame,boxes);
            else if (option==2)
                Process.particleProcess(frame,boxes,t);
            else if (option==3)
                Process.mouthProcess(frame,bitmap,boxes,t);
            else if (option==4)
                Process.eyeProcess(frame,bitmap,boxes,t);
            else if (option==5)
                Process.noneProcess(frame,bitmap,boxes,t);
            else if (option==6 || option == 7)
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
        }
        mcameraView.setCameraIndex(cameraIndex);
        if(mcameraView != null) {
            mcameraView.disableView();
        }
        mcameraView.enableView();
    }
}
