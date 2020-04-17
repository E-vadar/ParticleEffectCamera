package com.ParticleSystem;

import android.graphics.Camera;
import android.graphics.Point;

import com.ParticleEffectCamera.CameraViewActivity;

import java.util.ArrayDeque;
import org.opencv.core.Mat;

import static com.ParticleEffectCamera.CameraViewActivity.option;

public class ParticleSystem {

    static int initial_size = 0;
    static int duration = 0;
    static ArrayDeque<Particle> ptcspool = new ArrayDeque<>();

    //生成粒子池
    public static void initialize(int Initial_size, int Duration){
        initial_size = Initial_size;
        duration = Duration;
        ptcspool.clear();
        for (int i = 0; i < initial_size; i++) {
            ptcspool.add(new Particle());
        }
    }


    //初始化粒子配置
    public static void ptcConfig(){
        for (int z = 0; z < ptcspool.size(); z++) {
            Particle ptc = ptcspool.removeFirst();
            //粒子编号
            ptc.pNo = z;
            ptc.groupNo = z % duration;
            ptc.group = (new Double(Math.floor(z/duration))).intValue();
            //粒子属性
            ptc.x = 0;
            ptc.y = 0;
            ptc.life = false;
            ptc.lifetime = 0;
            ptc.size = 2;
            ptc.v = 2;
            ptc.col[0] = 255;
            ptc.col[1] = 0;
            ptc.col[2] = 0;
            ptc.direction[0] = 0;
            ptc.direction[1] = 0;
            ptc.duration = duration;
            ptc.trajectory = true;
            ptc.trajectoryLength = 9;
            ptcspool.addLast(ptc);
        }
    }

    //加载粒子系统运动
    public static void runSystem(Mat frame, Point[] landmark, int t){
        int x = 0;
        int y = 0;
        switch(CameraViewActivity.option){
            case 1:
                x = landmark[2].x;
                y = landmark[2].y;
                break;
            case 2:
                x = landmark[2].x;
                y = landmark[2].y;
                break;
            case 3:
                x = (landmark[3].x + landmark[4].x)/2;
                y = (landmark[3].y + landmark[4].y)/2;
                break;
            case 4:
                x = landmark[2].x;
                y = landmark[2].y;
                break;
            default:
                break;
        }
        int time = t % duration;//周期时间内
        for (int i = 0; i < ptcspool.size(); i++) {
            Particle ptc = ptcspool.removeFirst();
            switch(CameraViewActivity.option){
                case 1:
                    ptc.update(time);
                    break;
                case 2:
                    ptc.updateFire(time);
                    break;
                case 3:
                    ptc.updateWaterfall(time);
                    break;
                case 4:
                    ptc.updateFirework(time);
                    break;
                default:
                    break;
            }
            ptc.draw(frame,x,y);
            ptc.drawTrajectory(frame,x,y);
            ptcspool.addLast(ptc);
        }
    }
}
