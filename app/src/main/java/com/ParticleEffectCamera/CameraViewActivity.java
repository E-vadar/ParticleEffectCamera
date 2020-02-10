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
import java.util.Vector;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.resize;

public class CameraViewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener{
    String TAG="CameraViewActivity";
    private JavaCameraView mcameraView;
    private static int cameraIndex = 0;//前置1，后置0
    int option = 0;
    int t = 0;
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

        //前置、后置摄像头按钮初始化
        RadioButton backOption = findViewById(R.id.backCameraOption);
        RadioButton frontOption = findViewById(R.id.frontCameraOption);
        backOption.setOnClickListener(this);
        frontOption.setOnClickListener(this);
        backOption.setSelected(true);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
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
                           break;
                       case R.id.circle:
                           option = 2;
                           break;
                       case R.id.mouth:
                           option = 3;
                           break;
                       case R.id.eye:
                           option = 4;
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
        Mat temp = new Mat();
        Mat clockFrame=new Mat();
        Mat clockFrame1=new Mat();
        if(cameraIndex == 1) { // 前置摄像头
            flip(frame, frame, 1);
        }
        transpose(frame, temp);
        flip(temp,clockFrame,1);
        resize(clockFrame,clockFrame1, new Size(clockFrame.height() / 4, clockFrame.width()/ 4),0,0, INTER_LINEAR);
        process(clockFrame1);
        resize(clockFrame1,clockFrame, new Size(clockFrame1.height()* 4, clockFrame1.width()* 4),0,0, INTER_LINEAR);
        transpose(clockFrame, temp);
        flip(temp,frame,0);
        mtcnn=new MTCNN(getAssets());
        return frame;

    }

   //目前先不考虑菜单问题，默认option为0
    private void process(Mat frame) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.a36799,options);
        bitmap=Bitmap.createScaledBitmap(bitmap,frame.width(),frame.height(),true);
        Utils.matToBitmap(frame,bitmap);
        if (option==0)
            pureProcess(frame,bitmap);
        else if (option==1)
            glassProcess(frame,bitmap);
        else if (option==2)
            particleProcess(frame,bitmap);
        else if (option==3)
            mouthProcess(frame,bitmap);
        else if (option==4)
            eyeProcess(frame,bitmap);
    }

    public void glassProcess(Mat frame,Bitmap bm){//两张图片的内容一样，格式不一样
        try {
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);//mtcnn()的作用结果为生成一系列Box类（结构）
            Bitmap glass = BitmapFactory.decodeResource(this.getResources(),R.drawable.glass,options);
            Mat glass1=new Mat();
            Utils.bitmapToMat(glass,glass1);//现在的背景图和素材图分别是frame和glass1两个Mat

            for (int i=0;i<boxes.size();i++){//对于检测到的i张脸中的每一张脸分别戴上眼镜

                int glassWidth=boxes.get(i).width();
                int glassHeight=glassWidth*glass1.height()/glass1.width();
                Size s = new Size(glassWidth,glassHeight);
                resize(glass1,glass1, s,0,0, INTER_LINEAR);//把眼镜贴图等距缩放为   与面部矩形同宽,高度等比例缩小

                int faceHeight=boxes.get(i).height();


               Mat faceROI;

               faceROI = frame.submat( (int) (boxes.get(i).box[1]+0.25*faceHeight),(int) (boxes.get(i).box[1]+0.25*faceHeight)+glassHeight,boxes.get(i).left(), boxes.get(i).left()+glassWidth);

                Mat glass2=new Mat();

                glass2=blend(glass1,faceROI);
                glass2.copyTo(faceROI);
            }
        }
        catch (Exception e){
        }
    }

    public void particleProcess(Mat frame,Bitmap bm){//两张图片的内容一样，格式不一样
        try {
            Bitmap background = BitmapFactory.decodeResource(this.getResources(),R.drawable.white,options);
            Mat backgroundmat=new Mat();
            Utils.bitmapToMat(background,backgroundmat);//现在的背景图和素材图分别是frame和glass1两个Mat
            Mat effect1 = Particle.drawParticle(backgroundmat,t);
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);//mtcnn()的作用结果为生成一系列Box类（结构）

            for (int i=0;i<boxes.size();i++){//对于检测到的i张脸中的每一张脸分别戴上眼镜

                int effectWidth=boxes.get(i).width();
                int effectHeight=effectWidth*effect1.height()/effect1.width();
                Size s = new Size(effectWidth,effectHeight);
                resize(effect1,effect1, s,0,0, INTER_LINEAR);//把眼镜贴图等距缩放为   与面部矩形同宽,高度等比例缩小

                int faceHeight=boxes.get(i).height();

                Mat faceROI;

                faceROI = frame.submat( (int) (boxes.get(i).box[1]+0.25*faceHeight),(int) (boxes.get(i).box[1]+0.25*faceHeight)+effectHeight,boxes.get(i).left(), boxes.get(i).left()+effectWidth);

                Mat effect2=new Mat();
                effect2=blend(effect1,faceROI);//混合特效图和原图。
                effect2.copyTo(faceROI);
                if(t>=30){
                    t=1;
                }else{
                    t++;
                }
            }
        }
        catch (Exception e){
        }
    }


    public void mouthProcess(Mat frame,Bitmap bm){//两张图片的内容一样，格式不一样
        try {
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);//mtcnn()的作用结果为生成一系列Box类（结构）
            for (int i=0;i<boxes.size();i++) {//对于检测到的i张脸中的每一张脸分别戴上眼镜
                int effectWidth=boxes.get(i).width()/2;
                int effectHeight=boxes.get(i).height()/3;
                Blend.draw(bm, WelcomeActivity.listmouth.get(t), boxes.get(i).landmark,effectWidth,effectHeight,option);
                Utils.bitmapToMat(bm, frame);
            }
            if(t>=11){
                t=0;
            }else{
                t++;
            }
        }
        catch (Exception e){
        }
    }
    public void eyeProcess(Mat frame,Bitmap bm){//两张图片的内容一样，格式不一样
        try {
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);//mtcnn()的作用结果为生成一系列Box类（结构）
            for (int i=0;i<boxes.size();i++) {//对于检测到的i张脸中的每一张脸分别戴上眼镜
                int effectWidth=boxes.get(i).width()/2;
                int effectHeight=boxes.get(i).height()/3;
                Blend.draw(bm,  WelcomeActivity.listeye.get(t), boxes.get(i).landmark,effectWidth,effectHeight,option);
                Utils.bitmapToMat(bm, frame);
            }
            if(t>=20){
                t=0;
            }else{
                t++;
            }
        }
        catch (Exception e){
        }
    }

    public void pureProcess(Mat frame,Bitmap bm){
        try {
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);
            for (int i=0;i<boxes.size();i++){
                com.ParticleEffectCamera.Utils.drawRect(bm,boxes.get(i).transform2Rect());
                com.ParticleEffectCamera.Utils.drawPoints(bm,boxes.get(i).landmark);
            }
            Utils.bitmapToMat(bm,frame);
        }catch (Exception e){
        }
    }


    Mat blend(Mat glass1,Mat faceROI){
        Mat glass=new Mat(glass1.rows(),glass1.cols(),glass1.type());
        for (int i = 0; i < glass1.rows(); i++) {

            for (int j = 0; j < glass1.cols(); j++) {
                if(glass1.get(i,j)[0]>240&&glass1.get(i,j)[1]>240&&glass1.get(i,j)[2]>240){
                    glass.put(i,j,faceROI.get(i,j));
                }
                else{
                    glass.put(i,j,glass1.get(i,j));
                }
            }
        }
        return glass;
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
