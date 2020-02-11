package com.ParticleEffectCamera;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;
import static org.opencv.imgproc.Imgproc.circle;

public class Particle {

    int pNo;//粒子序号
    double x,y;//粒子的二维坐标
    double v ;//速度
    int[] col = new int[3];//颜色
    double[] direction = new double[2];//四向方向，上右下左
    int size;//粒子大小
    int lifetime;//粒子生命周期
    boolean life;//粒子是否存活
    Scalar color = new Scalar(0,0,0);//粒子颜色
    int group;//粒子组别
    int groupNo;//粒子组内编号

    public Particle() {
       super();
    }

    public void reactivate(){
        x=0;
        y=0;
        lifetime = 0;
        life = false;
        size = 3;
        v = 1;
        col[0] = 20+2*groupNo;
        col[1] = 120+2*groupNo;
        col[2] = 120+2*groupNo;
        direction[0] = 1;
        direction[1] = 1;
    }

    public void update(int time){
        if(groupNo <= time){
            life = true;
        }
        if(lifetime >= 60){
            reactivate();
        }
        if(life){
            lifetime ++;
            switch (group){
                case 0:
                    direction[0] = 1;
                    direction[1] = 1;
                    break;
                case 1:
                    direction[0] = -1;
                    direction[1] = 1;
                    break;
                case 2:
                    direction[0] = -1;
                    direction[1] = -1;
                    break;
                case 3:
                    direction[0] = 1;
                    direction[1] = -1;
                    break;
            }
            x = x + v * direction[0];
            y = y - v * direction[1];
            v = v + 0.5;
            color = new Scalar(col[0],col[1],col[2]);
        }
    }

    public void draw(Mat frame,int x0,int y0){
        if(life){
            int xp = new Double(Math.floor(x + x0)).intValue();
            int yp = new Double(Math.floor(y + y0)).intValue();
            circle(frame,new Point(xp,yp),size,color,-1);
        }
    }

    public int getGroupNo(){
        return groupNo;
    }
}
