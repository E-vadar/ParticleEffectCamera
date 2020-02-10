package com.ParticleEffectCamera;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import static org.opencv.imgproc.Imgproc.circle;

public class Particle {

    public static Mat drawParticle(Mat background,int t) {
        //width=400,height=500
        int x1=0,x2=0,y1=0,y2=0;
        Mat effect = drawdot(background,t,x1,x2,y1,y2);
        return effect;
    }

    private static Mat drawdot(Mat image,int t,int x1,int x2,int y1,int y2){
        Scalar color1 = new Scalar( 0, 50, 0+7*t);
        Scalar color2 = new Scalar( 255, 25, 0+7*t);
        Scalar color3 = new Scalar( 255, 0+7*t, 0);
        Scalar color4 = new Scalar( 0, 0+7*t,0 );
        int i = image.width();
        int j = image.height()*1/7;
        int th = i/64;

        Point le = new Point(i/4,j);
        Point re = new Point(i/4*3,j);

        if(t<=15){
            int r = i/64*t;
            circle(image,le,r,color1,th);
            circle(image,le,r+th*4,color2,th);
            circle(image,re,r,color1,th);
            circle(image,re,r+th*4,color2,th);
        }else{
            int r = i/64*(31-t);
            circle(image,le,r,color3,th);
            circle(image,le,r+th*4,color4,th);
            circle(image,re,r,color3,th);
            circle(image,re,r+th*4,color4,th);
        }
        return image;
    }
}
