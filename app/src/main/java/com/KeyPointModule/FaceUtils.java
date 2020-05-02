package com.KeyPointModule;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import java.util.Vector;

public class FaceUtils {

    public static void drawRect(Bitmap bitmap,Rect rect){
        try {
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            int r=255;
            int g=0;
            int b=0;
            paint.setColor(Color.rgb(r, g, b));
            paint.setStrokeWidth(1+bitmap.getWidth()/500 );
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect, paint);
        }catch (Exception e){
            Log.i("FaceUtils","[*] error"+e);
        }
    }

    public static void drawPoints(Bitmap bitmap, Point[] landmark){
        for (int i=0;i<landmark.length;i++){
            int x=landmark[i].x;
            int y=landmark[i].y;
            drawRect(bitmap,new Rect(x-1,y-1,x+1,y+1));
        }
    }

    public static void flip_diag(float[]data,int h,int w,int stride){
        float[] tmp=new float[w*h*stride];
        for (int i=0;i<w*h*stride;i++) tmp[i]=data[i];
        for (int y=0;y<h;y++)
            for (int x=0;x<w;x++){
               for (int z=0;z<stride;z++)
                 data[(x*h+y)*stride+z]=tmp[(y*w+x)*stride+z];
            }
    }

    public static Vector<Box> updateBoxes(Vector<Box> boxes){
        Vector<Box> b=new Vector<Box>();
        for (int i=0;i<boxes.size();i++)
            if (!boxes.get(i).deleted)
                b.addElement(boxes.get(i));
        return b;
    }

}
