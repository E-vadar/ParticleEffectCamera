package com.ParticleEffectCamera;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Color;

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
}
