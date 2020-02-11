package com.ParticleEffectCamera;

import android.graphics.Point;
import java.util.ArrayDeque;
import org.opencv.core.Mat;

public class ParticleSystem {

    static int initial_size = 40;
    static ArrayDeque<Particle> ptcspool = new ArrayDeque<>();

    public static void initialize(){
        for (int i = 0; i < initial_size; i++) {
            ptcspool.add(new Particle());
        }
    }

    public static void ptcConfig(){
        for (int i = 0; i < ptcspool.size(); i++) {
            Particle ptc = ptcspool.removeFirst();
            ptc.x = 0;
            ptc.y = 0;
            ptc.life = false;
            ptc.size = 1;
            ptc.v = 1;
            int duration = i % 10;
            ptc.col[0] = 20+10*duration;
            ptc.col[1] = 120+10*duration;
            ptc.col[2] = 120+10*duration;
            ptc.direction[0] = 1;
            ptc.direction[1] = 1;
            ptcspool.addLast(ptc);
        }
    }
    public static void runSystem(Mat frame, Point[] landmark, int t){
        int x = landmark[2].x;
        int y = landmark[2].y;
        int time = t % 10;
        for (int i = 0; i < ptcspool.size(); i++) {
            int duration = i % 10;
            int group = (new Double(Math.floor(i/10))).intValue();
            Particle ptc = ptcspool.removeFirst();
            ptc.update(group);
            if(duration <= time){
                ptc.activate();
            }
            ptc.draw(frame,x,y);
            ptcspool.addLast(ptc);
        }

    }
}
