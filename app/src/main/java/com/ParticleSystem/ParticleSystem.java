package com.ParticleSystem;

import android.graphics.Point;
import java.util.ArrayDeque;
import org.opencv.core.Mat;

public class ParticleSystem {

    static int initial_size = 270;
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
            ptc.size = 2;
            ptc.v = 2;
            ptc.col[0] = 255;
            ptc.col[1] = 0;
            ptc.col[2] = 0;
            ptc.direction[0] = 0;
            ptc.direction[1] = 0;
            ptc.duration = duration;
            ptcspool.addLast(ptc);
        }
    }

    //加载粒子系统运动
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
