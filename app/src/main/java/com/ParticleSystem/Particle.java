package com.ParticleSystem;

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
    int groupValue;
    int duration;//运动周期
    double cre = 0.2;//方向增量
    boolean trajectory;
    int trajectoryLength;
    boolean Halo;
    double[] xt = new double[10];
    double[] yt = new double[10];
    int[] stateOfColor = new int[3];
    double[] stateOfShape = new double[2];
    int[] config = new int[3];
    int[] configGroup = new int[10];

    public Particle() {
       super();
    }

    //重新激活粒子生命
    public void reactivate(){
        size = stateOfShape[0];
        v = stateOfShape[1];
        col[0] = stateOfColor[0];
        col[1] = stateOfColor[1];
        col[2] = stateOfColor[2];
        x=0;
        y=0;
        lifetime = 0;
        life = false;
        direction[0] = 0;
        direction[1] = 0;
    }

    public void update(int time){
        if(groupNo<=time-5){
            life = true;
        }
        if(life){
            TrackRecord(x,y,xt,yt,lifetime,trajectoryLength);
            Shape(config[0]);
            Velocity(config[1]);
            Vibration(config[1]);
            Move();
            Color(config[2]);
            lifetime ++;
        }
        if(lifetime == duration){
            reactivate();
        }
    }

    public void Vibration(int option){
        v = v + groupNo/groupValue*2;
        direction[0] = direction[0]+Math.random()-0.5;
        direction[0] = direction[0]+Math.random()-0.5;
    }

    public void Color(int option){
        switch(option){
            case 0:
                color_red();
                break;
            case 1:
                color_random();
                break;
        }
    }

    public void color_red(){
        color = new Scalar(255,0,0);
    }

    public void color_random(){
        color = new Scalar((new Double(Math.random()*10)).intValue()*25,(new Double(Math.random()*10)).intValue()*25,(new Double(Math.random()*10)).intValue()*25);

    }

    public void Velocity(int option){
        switch(option){
            case 0:
                velocity_linerity();
                break;
            case 1:
                velocity_acceleration();
                break;
        }
    }
    public void velocity_linerity(){
        v = v + 0;
    }

    public void velocity_acceleration(){
        double a = 0.2;
        v = v + a;
    }

    public void Move(){
        x = x - v * direction[0];
        y = y - v * direction[1];
    }

    public void Shape(int option){
        switch(option){
            case 0:
                shape_snake();
                break;
            case 1:
                shape_thunder();
                break;
            case 2:
                shape_circle();
                break;
            case 3:
                shape_firework();
                break;
        }

    }

    public void shape_snake(){
        int smallDuration = duration/6;
        int mark = lifetime%smallDuration;
        int period = new Double(Math.floor(lifetime/smallDuration)).intValue()%4;
        switch(period){
            case 0:
                direction[0] = -1 + 0.1*mark;
                direction[1] = 0.1*mark;
                break;
            case 1:
                direction[0] = 0.1*mark;
                direction[1] = 1 - 0.1*mark;
                break;
            case 2:
                direction[0] = 1 - 0.1*mark;
                direction[1] = 0.1*mark;
                break;
            case 3:
                direction[0] =  -0.1*mark;
                direction[1] = 1 - 0.1*mark;
                break;
        }
    }

    public void shape_circle(){
        int smallDuration = duration/4;
        int mark = lifetime%smallDuration;
        int period = new Double(Math.floor(lifetime/smallDuration)).intValue()%4;
        switch(period){
            case 0:
                direction[0] = -1 + 0.066*mark;
                direction[1] = 0.066*mark;
                break;
            case 1:
                direction[0] = 0.066*mark;
                direction[1] = 1 - 0.066*mark;
                break;
            case 2:
                direction[0] = 1 - 0.066*mark;
                direction[1] = -0.066*mark;
                break;
            case 3:
                direction[0] =  -0.066*mark;
                direction[1] =  -1 + 0.066*mark;
                break;
        }
    }

    public void shape_thunder(){
        int smallDuration = duration/3;
        int mark = lifetime%smallDuration;
        int period = new Double(Math.floor(lifetime/smallDuration)).intValue()%3;
        switch(period){
            case 0:
                direction[0] = -1 - 0.1*mark;
                direction[1] = -1 - 0.1*mark;
                break;
            case 1:
                direction[0] = 1 + 0.1*mark;
                direction[1] = 0;
                break;
            case 2:
                direction[0] = -1 - 0.1*mark;
                direction[1] = -1 - 0.1*mark;
                break;
        }
    }

    public void shape_firework(){
        int smallDuration = duration/2;
        int mark = lifetime%smallDuration;
        int period = new Double(Math.floor(lifetime/smallDuration)).intValue()%2;
        switch(period){
            case 0:
                direction[0] = 0;
                direction[1] = 6-0.2*mark;
                break;
            case 1:
                direction[0] = Math.random()*10-5;
                direction[1] = Math.random()*5-2.5-0.2*mark;
                break;
        }
    }

    //更新粒子状态
    public void updateFire(int time){
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

    public void updateWaterfall(int time){
        if(lifetime >= duration){
            reactivate();
        }
        if(groupNo <= time){
            life = true;
        }
        if(life){
            direction[0] = move(lifetime,group);
            direction[1] = gravity(lifetime);
            x = x + v * direction[0];
            y = y + v * direction[1];
            col[0] = group*5 + 2*lifetime;
            col[1] = group*5 + 2*lifetime;
            col[2] = group*5 + 4*lifetime;
            color = new Scalar(col[0],col[1],col[2]);
            lifetime ++;
        }
    }

    public void updateFirework(int time){
        if(lifetime >= duration){
            reactivate();
        }
        if(groupNo <= time){
            life = true;
        }
        if(life){
            direction[0] = moveRandomly(group);
            direction[1] = gravityRandomly(lifetime);
            TrackRecord(x,y,xt,yt,lifetime,trajectoryLength);
            x = x + v * direction[0];
            y = y + v * direction[1];
            col[0] = ColorRandomly()/2+150;
            col[1] = ColorRandomly()+group * 20;
            col[2] = ColorRandomly()+group * 20;
            color = new Scalar(col[0],col[1],col[2]);
            lifetime ++;
        }
    }

    //Draw particles on frame image;
    public void Render(Mat frame,int[][] key_position,int option){
        if(option == 0){
            int x0 = key_position[0][0];
            int y0 = key_position[0][1];
            drawTrajectory(frame,x0,y0);
            drawHalo(frame,x0,y0);
            draw(frame,x0,y0);
            int x1 = key_position[1][0];
            int y1 = key_position[1][1];
            drawTrajectory(frame,x1,y1);
            drawHalo(frame,x1,y1);
            draw(frame,x1,y1);
        } else {
            int x0 = key_position[0][0];
            int y0 = key_position[0][1];
            drawTrajectory(frame,x0,y0);
            drawHalo(frame,x0,y0);
            draw(frame,x0,y0);
        }
    }

    public void draw(Mat frame,int x0,int y0){
        if(life){
            int xp = new Double(Math.floor(x + x0)).intValue();
            int yp = new Double(Math.floor(y + y0)).intValue();
            circle(frame,new Point(xp,yp),new Double(Math.floor(size)).intValue(),color,-1);
        }
    }

    //Draw particles' trajectory on frame image;
    public void drawTrajectory(Mat frame,int x0,int y0){
        if(life || trajectory){
            for(int i=0;i<trajectoryLength;i++){
                int xp = new Double(Math.floor(xt[i] + x0)).intValue();
                int yp = new Double(Math.floor(yt[i] + y0)).intValue();
                circle(frame,new Point(xp,yp),new Double(Math.floor(size/2)).intValue(),new Scalar(255,255,255),-1);
            }
        }
    }

    public void drawHalo(Mat frame,int x0,int y0){
        if(life || Halo){
            for(int i=0;i<trajectoryLength;i++){
                int xp = new Double(Math.floor(x + x0)).intValue();
                int yp = new Double(Math.floor(y + y0)).intValue();
                circle(frame,new Point(xp,yp),new Double(Math.floor(size*1.5)).intValue(),new Scalar(255,255,255),0);
            }
        }
    }

    public double gravity(int lifetime){
        double y_direction = 0;
        y_direction = y_direction + 0.03*lifetime;
        return y_direction;
    }
    public double move(int lifetime,int group){
        double x_direction;
        if(group>=0 && group <8){
            x_direction = -0.8 + group*0.1
                    + lifetime*(0.025-group*0.003);
        } else {
            x_direction = 0.1 + (group-8)*0.1
                    - lifetime*(0.025-(16-group)*0.003);
        }
        return x_direction;
    }

    public double moveRandomly(int group){
        double x_direction;
            x_direction = 3 * (Math.random()-0.5)+0.2*(group-4);
        return x_direction;
    }

    public double gravityRandomly(int lifetime){
        double y_direction = 0;
        y_direction = 0.07*lifetime*((new Double(Math.random())).intValue()-0.5);
        return y_direction;
    }

    public int ColorRandomly(){
        int color = (new Double(Math.random())).intValue()*50;
        return color;
    }

    //Record the trajectory of particles
    public void TrackRecord(double tempx,double tempy,double[] tempxt,double[] tempyt,int lifetime,int trajectoryLength){
        int locus = lifetime % (trajectoryLength);
        tempxt[locus] = tempx;
        tempyt[locus] = tempy;
    }

}