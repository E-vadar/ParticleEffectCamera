package com.ParticleEffectCamera;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Color;
import org.opencv.core.Mat;

public class Blend {

    public static void draw(Bitmap background, Bitmap effect, Point[] landmark,int effectwidth,int effectheight,int opt){
        if(opt == 4){
            int x1 = landmark[0].x;
            int y1 = landmark[0].y;
            int x2 = landmark[1].x;
            int y2 = landmark[1].y;
            effect = Bitmap.createScaledBitmap(effect,effectwidth,effectheight,true);
            for (int i = 0; i < effect.getWidth(); i++) {
                for (int j = 0; j < effect.getHeight(); j++) {
                    if(Color.green(effect.getPixel(i, j))>40){//effect中该像素点不为黑色
                        background.setPixel(x1-effectwidth/2+i,y1-effectheight/2+j,effect.getPixel(i, j));
                        background.setPixel(x2-effectwidth/2+i,y2-effectheight/2+j,effect.getPixel(i, j));
                    }
                }
            }
        }else if(opt == 3){
            int x = (landmark[3].x+landmark[4].x)/2;
            int y = (landmark[3].y+landmark[4].y)/2;
            effect = Bitmap.createScaledBitmap(effect,effectwidth,effectheight,true);
            for (int i = 0; i < effect.getWidth(); i++) {
                for (int j = 0; j < effect.getHeight(); j++) {
                    if(Color.green(effect.getPixel(i, j))>40){//effect中该像素点不为黑色
                        background.setPixel(x-effectwidth/2+i,y-effectheight/2+j,effect.getPixel(i, j));;
                    }
                }
            }
        }
    }

    public static Mat blendblackbg(Mat glass1, Mat faceROI){
        Mat glass=new Mat(glass1.rows(),glass1.cols(),glass1.type());
        for (int i = 0; i < glass1.rows(); i++) {
            for (int j = 0; j < glass1.cols(); j++) {
                if(glass1.get(i,j)[0]<40 && glass1.get(i,j)[1]<40 && glass1.get(i,j)[2]<40){
                    glass.put(i,j,faceROI.get(i,j));
                }
                else{
                    glass.put(i,j,glass1.get(i,j));
                }
            }
        }
        return glass;
    }

    public static Mat blendwhitebg(Mat glass1, Mat faceROI){
        Mat glass=new Mat(glass1.rows(),glass1.cols(),glass1.type());
        for (int i = 0; i < glass1.rows(); i++) {
            for (int j = 0; j < glass1.cols(); j++) {
                if(glass1.get(i,j)[0]>240 && glass1.get(i,j)[1]>240 && glass1.get(i,j)[2]>240){
                    glass.put(i,j,faceROI.get(i,j));//画上原图
                }
                else{
                    glass.put(i,j,glass1.get(i,j));//画上素材
                }
            }
        }
        return glass;
    }
}
