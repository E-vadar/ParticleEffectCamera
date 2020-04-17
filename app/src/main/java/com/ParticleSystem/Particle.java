package com.ParticleSystem;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;

import static org.opencv.imgproc.Imgproc.LINE_AA;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;

public class Particle {

    int pNo;
    double x,y;
    double v ;//速度
    int[] col = new int[3];
    double[] direction = new double[2];
    double size;
    int lifetime;
    boolean life;
    Scalar color = new Scalar(0,0,0);//粒子颜色
    int group;//粒子组别
    int groupNo;//粒子组内编号
    int groupValue;
    int duration;//运动周期
    boolean trajectory;
    int trajectoryLength;
    int[] trajectoryColor = new int[3];
    boolean halo;
    int[] haloColor = new int[3];
    double[] xt = new double[10];
    double[] yt = new double[10];
    int[] stateOfColor = new int[3];
    double[] stateOfShape = new double[2];
    int[] config = new int[5];
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
        if(lifetime >= duration){
            life = false;
        }
        if(life){
            if(trajectory)
                TrackRecord();
            Shape(config[0]);
            Velocity(config[1]);
            Vibration(config[4]);
            Move();
            Color(config[2]);
            lifetime ++;
        }
        if(lifetime >= duration){
            reactivate();
        }
    }

    public void Vibration(int option){
        v = v + groupNo/groupValue*2*0.1*option;
        direction[0] = direction[0] + (Math.random()-0.5)*0.1*option;
        direction[0] = direction[0] + (Math.random()-0.5)*0.1*option;
    }

    public void Color(int option){
        switch(option){
            case 0:
                color_red();
                break;
            case 1:
                color_random();
                break;
            case 2:
                color_gradual();
                break;
        }
    }

    public void color_red(){
        color = new Scalar(255,0,0);
    }

    public void color_random(){
        color = new Scalar((new Double(Math.random()*10)).intValue()*25,(new Double(Math.random()*10)).intValue()*25,(new Double(Math.random()*10)).intValue()*25);
    }

    public void color_gradual(){
        color = new Scalar(col[0]-(new Double(Math.random()*5)).intValue()*5,col[1]+(new Double(Math.random()*5)).intValue()*5,col[2]+(new Double(Math.random()*5)).intValue()*5);
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
        direction[0] = direction[0] * (group-group/2)* 0.3;
        direction[1] = direction[1] * (group-group/2+1)* 0.3;
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
        if(lifetime == 0){
            y = y + 75;
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
                direction[0] = Math.random()*15-7.5;
                direction[1] = Math.random()*5-2.5-0.1*mark;
                break;
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

    //Draw particle's own on frame image;
    public void draw(Mat frame,int x0,int y0){
        if(life){
            int xp = new Double(Math.floor(x + x0)).intValue();
            int yp = new Double(Math.floor(y + y0)).intValue();
            circle(frame,new Point(xp,yp),new Double(Math.floor(size)).intValue(),color,-1);
        }
    }

    //Draw particles' trajectory on frame image;
    public void drawTrajectory(Mat frame,int x0,int y0){
        if(trajectory){
            int xp,yp;
            for(int i=0;i<trajectoryLength;i++){
                xp = new Double(Math.floor(xt[i] + x0)).intValue();
                yp = new Double(Math.floor(yt[i] + y0)).intValue();
                circle(frame,new Point(xp,yp),new Double(Math.floor(size/2)).intValue(),new Scalar(trajectoryColor[0],trajectoryColor[1],trajectoryColor[2]),-1);
                //line(frame,new Point(xp, yp),new Point(xp0,yp0),new Scalar(trajectoryColor[0],trajectoryColor[1],trajectoryColor[2]),new Double(Math.floor(size/2)).intValue());
            }
        }
    }

    public void drawHalo(Mat frame,int x0,int y0){
        if(halo){
            int xp = new Double(Math.floor(x + x0)).intValue();
            int yp = new Double(Math.floor(y + y0)).intValue();
            circle(frame,new Point(xp,yp),new Double(Math.floor(size*1.5)).intValue(),new Scalar(haloColor[0],haloColor[1],haloColor[2]),0);
        }
    }

    //Record the trajectory of particles
    public void TrackRecord(){
        int locus = lifetime % (trajectoryLength);
        xt[locus] = x;
        yt[locus] = y;
    }

}