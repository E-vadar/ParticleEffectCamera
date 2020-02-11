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
    double size;//粒子大小
    int lifetime;//粒子生命周期
    boolean life;//粒子是否存活
    Scalar color = new Scalar(0,0,0);//粒子颜色
    int group;//粒子组别
    int groupNo;//粒子组内编号
    int duration;//运动周期
    double cre = 0.2;//方向增量

    public Particle() {
       super();
    }

    //重新激活粒子生命
    public void reactivate(){
        x=0;
        y=0;
        lifetime = 0;
        life = false;
        size = 2;
        v = 2;
        col[0] = 255;
        col[1] = 0;
        col[2] = 0;
        direction[0] = 0;
        direction[1] = 0;
    }

    //更新粒子状态
    public void update(int time){
        if(lifetime >= duration){
            reactivate();
        }
        if(groupNo <= time){
            life = true;
        }
        if(life){
            if(lifetime%3 == 2){
                cre = -cre;
            }
            switch (group){
                case 0:
                    direction[0] = 0.4+2*cre;
                    direction[1] = 0.03*lifetime-0.12;
                    break;
                case 1:
                    direction[0] = 0.3+1.5*cre;
                    direction[1] = 0.03*lifetime-0.08;
                    break;
                case 2:
                    direction[0] = 0.2+cre;
                    direction[1] = 0.03*lifetime-0.04;
                    break;
                case 3:
                    direction[0] = 0.1+0.5*cre;
                    direction[1] = 0.03*lifetime;
                    break;
                case 4:
                    direction[0] = -0.1-0.5*cre;
                    direction[1] = 0.03*lifetime;
                    break;
                case 5:
                    direction[0] = -0.2-cre;
                    direction[1] = 0.03*lifetime-0.04;
                    break;
                case 6:
                    direction[0] = -0.3-1.5*cre;
                    direction[1] = 0.03*lifetime-0.08;
                    break;
                case 7:
                    direction[0] = -0.4-2*cre;
                    direction[1] = 0.03*lifetime-0.12;
                    break;
                case 8:
                    direction[0] = 0+0.5*cre;
                    direction[1] = 0.03*lifetime;
                    break;
            }
            x = x + v * direction[0];
            y = y - v * direction[1];
            col[1] = 8*lifetime;
            size = size + 0.006*lifetime ;
            v = 2 + 0.02*lifetime;
            color = new Scalar(col[0],col[1],col[2]);
            lifetime ++;
        }
    }

    //在frame上呈现粒子
    public void draw(Mat frame,int x0,int y0){
        if(life){
            int xp = new Double(Math.floor(x + x0)).intValue();
            int yp = new Double(Math.floor(y + y0)).intValue();
            circle(frame,new Point(xp,yp),new Double(Math.floor(size)).intValue(),color,-1);
        }
    }
}
