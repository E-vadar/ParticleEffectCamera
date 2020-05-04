package com.KeyPointModule;

/**
 * Author: He Jingze, vcvycy
 * Description: Call the MTCNN face detection model and 3 layers network input
 */

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.util.Vector;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class MTCNN {
    private float factor=0.709f;
    private float PNetThreshold=0.6f;
    private float RNetThreshold=0.7f;
    private float ONetThreshold=0.7f;
    private static final String MODEL_FILE  = "file:///android_asset/mtcnn_freezed_model.pb";
    private static final String   PNetInName  ="pnet/input:0";
    private static final String[] PNetOutName =new String[]{"pnet/prob1:0","pnet/conv4-2/BiasAdd:0"};
    private static final String   RNetInName  ="rnet/input:0";
    private static final String[] RNetOutName =new String[]{ "rnet/prob1:0","rnet/conv5-2/conv5-2:0",};
    private static final String   ONetInName  ="onet/input:0";
    private static final String[] ONetOutName =new String[]{ "onet/prob1:0","onet/conv6-2/conv6-2:0","onet/conv6-3/conv6-3:0"};
    public  long lastProcessTime;
    private static final String TAG="MTCNN";
    private AssetManager assetManager;
    private TensorFlowInferenceInterface inferenceInterface;

    public MTCNN(AssetManager mgr){
        assetManager=mgr;
        loadModel();
    }

    private boolean loadModel() {
        try {
            inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
            Log.d("Facenet","[*]load model success");
        }catch(Exception e){
            Log.e("Facenet","[*]load model failed"+e);
            return false;
        }
        return true;
    }

    private float[] normalizeImage(Bitmap bitmap){
        int w=bitmap.getWidth();
        int h=bitmap.getHeight();
        float[] floatValues=new float[w*h*3];
        int[]   intValues=new int[w*h];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        float imageMean=127.5f;
        float imageStd=128;

        for (int i=0;i<intValues.length;i++){
            final int val=intValues[i];
            floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }
        return floatValues;
    }

    private Bitmap bitmapResize(Bitmap bm, float scale) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    private  int PNetForward(Bitmap bitmap,float [][]PNetOutProb,float[][][]PNetOutBias){
        int w=bitmap.getWidth();
        int h=bitmap.getHeight();
        float[] PNetIn=normalizeImage(bitmap);
        FaceUtils.flip_diag(PNetIn,h,w,3);
        inferenceInterface.feed(PNetInName,PNetIn,1,w,h,3);
        inferenceInterface.run(PNetOutName,false);
        int PNetOutSizeW=(int)Math.ceil(w*0.5-5);
        int PNetOutSizeH=(int)Math.ceil(h*0.5-5);
        float[] PNetOutP=new float[PNetOutSizeW*PNetOutSizeH*2];
        float[] PNetOutB=new float[PNetOutSizeW*PNetOutSizeH*4];
        inferenceInterface.fetch(PNetOutName[0],PNetOutP);
        inferenceInterface.fetch(PNetOutName[1],PNetOutB);
        for (int y=0;y<PNetOutSizeH;y++)
            for (int x=0;x<PNetOutSizeW;x++){
               int idx=PNetOutSizeH*x+y;
               PNetOutProb[y][x]=PNetOutP[idx*2+1];
               for(int i=0;i<4;i++)
                   PNetOutBias[y][x][i]=PNetOutB[idx*4+i];
            }
        return 0;
    }

    private void nms(Vector<Box> boxes,float threshold,String method){
        for(int i=0;i<boxes.size();i++) {
            Box box = boxes.get(i);
            if (!box.deleted) {
                for (int j = i + 1; j < boxes.size(); j++) {
                    Box box2=boxes.get(j);
                    if (!box2.deleted) {
                        int x1 = max(box.box[0], box2.box[0]);
                        int y1 = max(box.box[1], box2.box[1]);
                        int x2 = min(box.box[2], box2.box[2]);
                        int y2 = min(box.box[3], box2.box[3]);
                        if (x2 < x1 || y2 < y1) continue;
                        int areaIoU = (x2 - x1 + 1) * (y2 - y1 + 1);
                        float iou=0f;
                        if (method.equals("Union"))
                            iou = 1.0f*areaIoU / (box.area() + box2.area() - areaIoU);
                        else if (method.equals("Min")) {
                            iou = 1.0f * areaIoU / (min(box.area(), box2.area()));
                            Log.i(TAG,"[*]iou="+iou);
                        }
                        if (iou >= threshold) {
                            if (box.score>box2.score)
                                box2.deleted=true;
                            else
                                box.deleted=true;
                        }
                    }
                }
            }
        }
    }

    private int generateBoxes(float[][] prob,float[][][]bias,float scale,float threshold,Vector<Box> boxes){
        int h=prob.length;
        int w=prob[0].length;
        for (int y=0;y<h;y++)
            for (int x=0;x<w;x++){
                float score=prob[y][x];
                if (score>threshold){
                    Box box=new Box();
                    box.score=score;
                    box.box[0]=Math.round(x*2/scale);
                    box.box[1]=Math.round(y*2/scale);
                    box.box[2]=Math.round((x*2+11)/scale);
                    box.box[3]=Math.round((y*2+11)/scale);
                    for(int i=0;i<4;i++)
                        box.bbr[i]=bias[y][x][i];
                    boxes.addElement(box);
                }
            }
        return 0;
    }

    private void BoundingBoxReggression(Vector<Box> boxes){
        for (int i=0;i<boxes.size();i++)
            boxes.get(i).calibrate();
    }

    private Vector<Box> PNet(Bitmap bitmap,int minSize){
        int whMin=min(bitmap.getWidth(),bitmap.getHeight());
        float currentFaceSize=minSize;
        Vector<Box> totalBoxes=new Vector<Box>();
        while (currentFaceSize<=whMin){
            float scale=12.0f/currentFaceSize;
            Bitmap bm=bitmapResize(bitmap,scale);
            int w=bm.getWidth();
            int h=bm.getHeight();
            int PNetOutSizeW=(int)(Math.ceil(w*0.5-5)+0.5);
            int PNetOutSizeH=(int)(Math.ceil(h*0.5-5)+0.5);
            float[][]   PNetOutProb=new float[PNetOutSizeH][PNetOutSizeW];;
            float[][][] PNetOutBias=new float[PNetOutSizeH][PNetOutSizeW][4];
            PNetForward(bm,PNetOutProb,PNetOutBias);
            Vector<Box> curBoxes=new Vector<Box>();
            generateBoxes(PNetOutProb,PNetOutBias,scale,PNetThreshold,curBoxes);
            nms(curBoxes,0.5f,"Union");
            for (int i=0;i<curBoxes.size();i++)
                if (!curBoxes.get(i).deleted)
                    totalBoxes.addElement(curBoxes.get(i));
            currentFaceSize/=factor;
        }
        nms(totalBoxes,0.7f,"Union");
        BoundingBoxReggression(totalBoxes);
        return FaceUtils.updateBoxes(totalBoxes);
    }

    private void crop_and_resize(Bitmap bitmap,Box box,int size,float[] data){
        Matrix matrix = new Matrix();
        float scale=1.0f*size/box.width();
        matrix.postScale(scale, scale);
        Bitmap croped=Bitmap.createBitmap(bitmap, box.left(),box.top(),box.width(), box.height(),matrix,true);
        int[] pixels_buf=new int[size*size];
        croped.getPixels(pixels_buf,0,croped.getWidth(),0,0,croped.getWidth(),croped.getHeight());
        float imageMean=127.5f;
        float imageStd=128;
        for (int i=0;i<pixels_buf.length;i++){
            final int val=pixels_buf[i];
            data[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            data[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            data[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }
    }

    private void RNetForward(float[] RNetIn,Vector<Box>boxes){
        int num=RNetIn.length/24/24/3;
        inferenceInterface.feed(RNetInName,RNetIn,num,24,24,3);
        inferenceInterface.run(RNetOutName,false);
        float[] RNetP=new float[num*2];
        float[] RNetB=new float[num*4];
        inferenceInterface.fetch(RNetOutName[0],RNetP);
        inferenceInterface.fetch(RNetOutName[1],RNetB);
        for (int i=0;i<num;i++) {
            boxes.get(i).score = RNetP[i * 2 + 1];
            for (int j=0;j<4;j++)
                boxes.get(i).bbr[j]=RNetB[i*4+j];
        }
    }

    private Vector<Box> RNet(Bitmap bitmap,Vector<Box> boxes){
        int num=boxes.size();
        float[] RNetIn=new float[num*24*24*3];
        float[] curCrop=new float[24*24*3];
        int RNetInIdx=0;
        for (int i=0;i<num;i++){
            crop_and_resize(bitmap,boxes.get(i),24,curCrop);
            FaceUtils.flip_diag(curCrop,24,24,3);
            for (int j=0;j<curCrop.length;j++) RNetIn[RNetInIdx++]= curCrop[j];
        }
        RNetForward(RNetIn,boxes);
        for (int i=0;i<num;i++)
            if (boxes.get(i).score<RNetThreshold)
                boxes.get(i).deleted=true;
        nms(boxes,0.7f,"Union");
        BoundingBoxReggression(boxes);
        return FaceUtils.updateBoxes(boxes);
    }

    private void ONetForward(float[] ONetIn,Vector<Box>boxes){
        int num=ONetIn.length/48/48/3;
        inferenceInterface.feed(ONetInName,ONetIn,num,48,48,3);
        inferenceInterface.run(ONetOutName,false);
        float[] ONetP=new float[num*2];
        float[] ONetB=new float[num*4];
        float[] ONetL=new float[num*10];
        inferenceInterface.fetch(ONetOutName[0],ONetP);
        inferenceInterface.fetch(ONetOutName[1],ONetB);
        inferenceInterface.fetch(ONetOutName[2],ONetL);
        for (int i=0;i<num;i++) {
            boxes.get(i).score = ONetP[i * 2 + 1];
            for (int j=0;j<4;j++)
                boxes.get(i).bbr[j]=ONetB[i*4+j];
            for (int j=0;j<5;j++) {
                int x= boxes.get(i).left()+(int) (ONetL[i * 10 + j]*boxes.get(i).width());
                int y= boxes.get(i).top()+(int) (ONetL[i * 10 + j +5]*boxes.get(i).height());
                boxes.get(i).landmark[j] = new Point(x,y);
            }
        }
    }

    private Vector<Box> ONet(Bitmap bitmap,Vector<Box> boxes){
        int num=boxes.size();
        float[] ONetIn=new float[num*48*48*3];
        float[] curCrop=new float[48*48*3];
        int ONetInIdx=0;
        for (int i=0;i<num;i++){
            crop_and_resize(bitmap,boxes.get(i),48,curCrop);
            FaceUtils.flip_diag(curCrop,48,48,3);
            for (int j=0;j<curCrop.length;j++) ONetIn[ONetInIdx++]= curCrop[j];
        }
        ONetForward(ONetIn,boxes);
        for (int i=0;i<num;i++)
            if (boxes.get(i).score<ONetThreshold)
                boxes.get(i).deleted=true;
        BoundingBoxReggression(boxes);
        nms(boxes,0.7f,"Min");
        return FaceUtils.updateBoxes(boxes);
    }

    private void square_limit(Vector<Box>boxes,int w,int h){
        for (int i=0;i<boxes.size();i++) {
            boxes.get(i).toSquareShape();
            boxes.get(i).limit_square(w,h);
        }
    }

    public Vector<Box> detectFaces(Bitmap bitmap,int minFaceSize) {
        long t_start = System.currentTimeMillis();
        Vector<Box> boxes=PNet(bitmap,minFaceSize);
        square_limit(boxes,bitmap.getWidth(),bitmap.getHeight());
        boxes=RNet(bitmap,boxes);
        square_limit(boxes,bitmap.getWidth(),bitmap.getHeight());
        boxes=ONet(bitmap,boxes);
        Log.i(TAG,"[*]Mtcnn Detection Time:"+(System.currentTimeMillis()-t_start));
        lastProcessTime=(System.currentTimeMillis()-t_start);
        return  boxes;
    }

}
