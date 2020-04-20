package com.EffectSystem;

import android.graphics.Bitmap;
import android.util.Log;
import com.FaceDetection.Box;
import com.ParticleSystem.ParticleSystem;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.util.Vector;

public class Process {

    static String TAG="Process";

    public static void pureProcess(Mat frame,Bitmap bm,Vector<Box> boxes){
        try {
            for (int i=0;i<boxes.size();i++){
                com.FaceDetection.Utils.drawRect(bm,boxes.get(i).transform2Rect());
                com.FaceDetection.Utils.drawPoints(bm,boxes.get(i).landmark);
            }
            Utils.bitmapToMat(bm,frame);
        }catch (Exception e){
        }
    }

    public static void particleSystemProcess(Mat frame,Vector<Box> boxes,int t){
        try {
            for (int i=0;i<boxes.size();i++) {
                ParticleSystem.runSystem(frame,boxes.get(i).landmark,t);
                Log.i(TAG,"picture width"+ frame.height() + "picture height"+ frame.width());
                Log.i(TAG,"Box Width"+ boxes.get(i).width()+"Box Height"+ boxes.get(i).height());
            }
        }catch (Exception e){
        }
    }
}
