package com.ParticleEffectCamera;

import android.graphics.Point;
import java.util.ArrayDeque;
import org.opencv.core.Mat;

public class ParticleSystem {

    static int initial_size = 120;
    static int duration = 30;
    static ArrayDeque<Particle> ptcspool = new ArrayDeque<>();

    //生成粒子池
    public static void initialize(){
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
            ptc.size = 3;
            ptc.v = 1;
            ptc.col[0] = 20+2*ptc.getGroupNo();
            ptc.col[1] = 20+2*ptc.getGroupNo();
            ptc.col[2] = 20+2*ptc.getGroupNo();
            ptc.direction[0] = 1;
            ptc.direction[1] = 1;
            ptc.duration = duration;
            ptcspool.addLast(ptc);
        }
    }

    //粒子运动
    public static void runSystem(Mat frame, Point[] landmark, int t){
        int x = landmark[2].x;
        int y = landmark[2].y;
        int time = t % duration;//周期时间内

        for (int i = 0; i < ptcspool.size(); i++) {
            Particle ptc = ptcspool.removeFirst();
            ptc.update(time);
            ptc.draw(frame,x,y);
            ptcspool.addLast(ptc);
        }
    }
}
