package com.ParticleSystem;

import android.graphics.Point;
import java.util.ArrayDeque;
import org.opencv.core.Mat;

public class ParticleSystem {

    private static int total_duration = 0;
    private static int group_size = 0;
    private static ArrayDeque<Particle> ptcspool = new ArrayDeque<>();

    public static void Configuration(int[][] config,int Total_Duration_Second){
        //reconfiguration
        group_size = 0;
        total_duration = Total_Duration_Second * 30;
        ptcspool.clear();
        //traverse each group and set size
        for(int groupNo = 0; groupNo < 10; groupNo++){
            if(config[groupNo][22] == 1){
                group_size++;
                for (int j = 0; j < config[groupNo][5]; j++) {
                    ptcspool.add(new Particle());
                }
            }
        }
        //traverse particles pool in each group and configure particles
        int GroupNo = 0;
        for(int groupNo = 0; groupNo < 10; groupNo++){
            if(config[groupNo][22] == 1){
                int No = 0;
                for (int z = 0; z < config[groupNo][5]; z++) {
                    Particle ptc = ptcspool.removeFirst();
                    //Particles' number configuration
                    ptc.pNo = No;
                    ptc.groupNo = z;
                    ptc.group = GroupNo;
                    ptc.groupValue = group_size;
                    //Particles' shape configuration
                    ptc.x = 0;
                    ptc.y = 0;
                    ptc.direction[0] = 0;
                    ptc.direction[1] = 0;
                    ptc.size = config[groupNo][7];
                    ptc.col[0] = config[groupNo][13];
                    ptc.col[1] = config[groupNo][14];
                    ptc.col[2] = config[groupNo][15];
                    ptc.v = config[groupNo][8];
                    //Particles' life configuration
                    ptc.life = false;
                    ptc.lifetime = 0;
                    ptc.duration = config[groupNo][6];
                    //Particle effect configuration
                    ptc.config[0] = config[groupNo][0];
                    ptc.config[1] = config[groupNo][1];
                    ptc.config[2] = config[groupNo][2];
                    ptc.config[3] = config[groupNo][3];
                    ptc.config[4] = config[groupNo][4];
                    //Trajectory configuration
                    if(config[groupNo][9] == 1)
                        ptc.trajectory = true;
                    ptc.trajectoryColor[0] = config[groupNo][16];
                    ptc.trajectoryColor[1] = config[groupNo][17];
                    ptc.trajectoryColor[2] = config[groupNo][18];
                    ptc.trajectoryLength = config[groupNo][10];//Length should be less than 10
                    //Halo configuration
                    if(config[groupNo][11] == 1)
                        ptc.halo = true;
                    ptc.HaloSize = config[groupNo][12];
                    ptc.haloColor[0] = config[groupNo][19];
                    ptc.haloColor[1] = config[groupNo][20];
                    ptc.haloColor[2] = config[groupNo][21];
                    //Save the initial state of the particle
                    ptc.stateOfSize = ptc.size;
                    ptc.stateOfVelocity = ptc.v;
                    ptc.stateOfColor[0] = ptc.col[0];
                    ptc.stateOfColor[1] = ptc.col[1];
                    ptc.stateOfColor[2] = ptc.col[2];
                    ptcspool.addLast(ptc);
                    No++;
                }
                GroupNo++;
            }
        }
    }

    //Run particles in pool every frame.
    public static void runSystem(Mat frame, Point[] landmark, int t){
        //input key position of human face(android.graphics.Point) and saved in array.
        int[][] key_position = new int[4][2];
        key_position[0][0] = landmark[0].x;//Eye left
        key_position[0][1] = landmark[0].y;
        key_position[1][0] = landmark[1].x;//Eye right
        key_position[1][1] = landmark[1].y;
        key_position[2][0] = landmark[2].x;//Nose
        key_position[2][1] = landmark[2].y;
        key_position[3][0] = (landmark[3].x + landmark[4].x)/2;//Mouth
        key_position[3][1] = (landmark[3].y + landmark[4].y)/2;

        int time = t % total_duration;//Transfer frame period to particle effect period

        //Update every particles in pool and draw them on frame image
        for (int i = 0; i < ptcspool.size(); i++) {
            Particle ptc = ptcspool.removeFirst();
            ptc.update(time);
            ptc.Render(frame,key_position);
            ptcspool.addLast(ptc);
        }
    }

}
