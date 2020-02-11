package com.ParticleEffectCamera;

import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import java.util.Vector;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.resize;

public class Process {

    //渲染的具体操作过程+1
    public static void mouthProcess(Mat frame, Bitmap bm,Vector<Box> boxes,int t){//两张图片的内容一样，格式不一样
        try {
            for (int i=0;i<boxes.size();i++) {//对于检测到的i张脸中的每一张脸分别戴上眼镜
                int effectWidth=boxes.get(i).width()/2;
                int effectHeight=boxes.get(i).height()/3;
                Blend.draw(bm, WelcomeActivity.listmouth.get(t), boxes.get(i).landmark,effectWidth,effectHeight,3);
                Utils.bitmapToMat(bm, frame);
            }
            if(t>=11){
                CameraViewActivity.t=0;
            }else{
                CameraViewActivity.t++;
            }
        }
        catch (Exception e){
        }
    }

    public static void eyeProcess(Mat frame,Bitmap bm,Vector<Box> boxes,int t){//两张图片的内容一样，格式不一样
        try {
            for (int i=0;i<boxes.size();i++) {//对于检测到的i张脸中的每一张脸分别戴上眼镜
                int effectWidth=boxes.get(i).width()/2;
                int effectHeight=boxes.get(i).height()/3;
                Blend.draw(bm,  WelcomeActivity.listeye.get(t), boxes.get(i).landmark,effectWidth,effectHeight,4);
                Utils.bitmapToMat(bm, frame);
            }
            if(t>=20){
                CameraViewActivity.t=0;
            }else{
                CameraViewActivity.t++;
            }
        }
        catch (Exception e){
        }
    }

    public static void pureProcess(Mat frame,Bitmap bm,Vector<Box> boxes){
        try {
            for (int i=0;i<boxes.size();i++){
                com.ParticleEffectCamera.Utils.drawRect(bm,boxes.get(i).transform2Rect());
                com.ParticleEffectCamera.Utils.drawPoints(bm,boxes.get(i).landmark);
            }
            Utils.bitmapToMat(bm,frame);
        }catch (Exception e){
        }
    }

    public static void glassProcess(Mat frame,Vector<Box> boxes){//两张图片的内容一样，格式不一样
        try {
            Mat glass1=new Mat();
            Utils.bitmapToMat(WelcomeActivity.glass,glass1);//现在的背景图和素材图分别是frame和glass1两个Mat
            for (int i=0;i<boxes.size();i++){
                //对于检测到的i张脸中的每一张脸分别戴上眼镜
                int glassWidth=boxes.get(i).width();
                int glassHeight=glassWidth*glass1.height()/glass1.width();
                Size s = new Size(glassWidth,glassHeight);
                resize(glass1,glass1, s,0,0, INTER_LINEAR);//把眼镜贴图等距缩放为   与面部矩形同宽,高度等比例缩小
                int faceHeight=boxes.get(i).height();
                Mat faceROI;
                faceROI = frame.submat( (int) (boxes.get(i).box[1]+0.25*faceHeight),(int) (boxes.get(i).box[1]+0.25*faceHeight)+glassHeight,boxes.get(i).left(), boxes.get(i).left()+glassWidth);
                Mat glass2=new Mat();
                glass2=Blend.blendblackbg(glass1,faceROI);
                glass2.copyTo(faceROI);
            }
        }
        catch (Exception e){
        }
    }

    public static void particleProcess(Mat frame,Vector<Box> boxes,int t){
        try {
            for (int i=0;i<boxes.size();i++) {
                Mat effect = ParticleDraw.drawParticle(frame,t);
                frame = Blend.blendwhitebg(effect,frame);
                if(t>=30){
                    CameraViewActivity.t=0;
                }else{
                    CameraViewActivity.t++;
                }
            }
        }
        catch (Exception e){
        }
    }
}
