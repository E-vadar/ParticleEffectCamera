package com.ParticleEffectCamera;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;
import static org.opencv.imgproc.Imgproc.circle;

public class Particle {

    int x,y;//粒子的二维坐标
    int v ;//速度
    int[] col = new int[3];//颜色
    int direction;//四向方向，上右下左
    int size;//粒子大小,实际大小为4 * size
    boolean life;
    Scalar color = new Scalar(col[0],col[1],col[2]);

    public Particle() {
       super();
    }

    public void activate(){
        life = true;
    }

    public void update(){
        if(life){
            x = x + v * direction * 1;
            y = y - v * direction * 1;
            v = v+1;
            size = size + 1;
            color = new Scalar(col[0],col[1],col[2]);
        }
    }
    public void draw(Mat frame,int x0,int y0){
        if(life){
            int xp = x + x0;
            int yp = y + y0;
            circle(frame,new Point(xp,yp),size,color,-1);
        }
    }
}
