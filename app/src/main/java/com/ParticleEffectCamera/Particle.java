package com.ParticleEffectCamera;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;
import static org.opencv.imgproc.Imgproc.circle;

public class Particle {

    int pNo;//粒子序号
    double x,y;//粒子的二维坐标
    double v ;//速度
    int[] col = new int[3];//颜色,red,green,blue
    double[] direction = new double[2];//四向方向，上右下左
    int size;//粒子大小
    int lifetime;//粒子生命周期
    boolean life;//粒子是否存活
    Scalar color = new Scalar(0,0,0);//粒子颜色
    int group;//粒子组别
    int groupNo;//粒子组内编号
    int duration;

    public Particle() {
       super();
    }

    public void reactivate(){
        x=0;
        y=0;
        lifetime = 0;
        life = false;
        size = 3;
        v = 1.5;
        col[0] = 255;
        col[1] = 0;
        col[2] = 0;
        direction[0] = 0;
        direction[1] = 1;
        //跟上前面粒子的步伐
        update(0);
    }

    public void update(int time){
        if(lifetime >= duration){
            reactivate();
        }
        if(groupNo <= time){
            life = true;
        }
        if(life){
            switch (group){
                case 0:
                    direction[0] = 0.2;
                    direction[1] = 1;
                    break;
                case 1:
                    direction[0] = 0.1;
                    direction[1] = 1;
                    break;
                case 2:
                    direction[0] = -0.2;
                    direction[1] = 1;
                    break;
                case 3:
                    direction[0] = -0.1;
                    direction[1] = 1;
                    break;
            }
            x = x + v * direction[0];
            y = y - v * direction[1];
            col[1] = 7*lifetime;
            color = new Scalar(col[0],col[1],col[2]);
            lifetime ++;
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
