package com.ParticleEffectCamera;

import android.graphics.Point;
import java.util.ArrayDeque;
import org.opencv.core.Mat;

public class ParticleSystem {

    static int initial_size = 10;
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
            ptc.col[0] = 20+10*i;
            ptc.col[1] = 120+10*i;
            ptc.col[2] = 120+10*i;
            ptc.direction = 1;
            ptcspool.addLast(ptc);
        }
    }
    public static void draw(Mat frame, Point[] landmark,int t){
        int x = landmark[2].x;
        int y = landmark[2].y;
        int time = t % 10;
        for (int i = 0; i < ptcspool.size(); i++) {
            Particle ptc = ptcspool.removeFirst();
            ptc.update();
            if(i <= time){
                ptc.activate();
            }
            ptc.draw(frame,x,y);
            ptcspool.addLast(ptc);
        }

    }
}
