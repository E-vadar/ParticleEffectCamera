package com.ProcessModule;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Looper;
import java.io.File;
import java.io.IOException;
import android.util.Log;
import android.os.Build;
import android.content.Context;
import androidx.annotation.RequiresApi;
import android.annotation.TargetApi;
import com.MainSystem.CameraViewActivity;

public class RecordService extends Service implements Handler.Callback {
    private static final String TAG = "RecordService";
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;
    private boolean mIsRunning;
    private int mRecordWidth;
    private int mRecordHeight;
    private int mScreenDpi;
    private int mResultCode;
    private Intent mResultData;
    private String mRecordFilePath;
    private Handler mHandler;
    private int mRecordSeconds = 0;
    private static final int MSG_TYPE_COUNT_DOWN = 110;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIsRunning = false;
        mMediaRecorder = new MediaRecorder();
        mHandler = new Handler(Looper.getMainLooper(), this);
        mRecordWidth = CameraViewActivity.width;
        mRecordHeight = CameraViewActivity.height;
        mScreenDpi = CameraViewActivity.DPI;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isReady() {
        return mMediaProjection != null && mResultData != null;
    }

    public boolean ismIsRunning() {
        return mIsRunning;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setResultData(int resultCode, Intent resultData) {
        mResultCode = resultCode;
        mResultData = resultData;
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mProjectionManager != null) {
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode, mResultData);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean startRecord() {
        if (mIsRunning) {
            return false;
        }
        if (mMediaProjection == null) {
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode, mResultData);
        }
        setUpMediaRecorder();
        createVirtualDisplay();
        mMediaRecorder.start();
        RecordUtils.startRecord();
        mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
        mIsRunning = true;
        Log.d(TAG, "startRecord ");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean stopRecord(String tip) {
        Log.d(TAG, "stopRecord: first ");
        if (!mIsRunning) {
            return false;
        }
        mIsRunning = false;
        Log.w(TAG, "stopRecord middle");
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder = null;
            mVirtualDisplay.release();
            mMediaProjection.stop();
            Log.w(TAG, "stopRecord ");
        } catch (Exception e) {
            e.printStackTrace();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.w(TAG, "stopRecord exception");
        }
        mMediaProjection = null;
        mHandler.removeMessages(MSG_TYPE_COUNT_DOWN);
        RecordUtils.stopRecord(tip);
        Log.i(TAG, "stopRecord: " + mRecordFilePath);
        if (mRecordSeconds <= 2) {
            RecordFileUtils.deleteSDFile(mRecordFilePath);
        } else {
            RecordFileUtils.fileScanVideo(this, mRecordFilePath, 1280, 720, mRecordSeconds);
        }
        mRecordSeconds = 0;
        return true;
    }

    //这个就是刚才讲过的 绘制窗口大小,dpi问题 VirtualDisplay
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("MainScreen", 1280, 720, mScreenDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpMediaRecorder() {
        mRecordFilePath = getSaveDirectory() + File.separator + "1 Test Video_" + System.currentTimeMillis() + ".mp4";
        Log.i(TAG, "setUpMediaRecorder: " + mRecordFilePath);
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mRecordFilePath != null) {
            mMediaRecorder.setOutputFile(mRecordFilePath);
        } else if (mRecordFilePath == null) {
            mMediaRecorder.setOutputFile(mRecordFilePath);
        }
        //设置录屏时屏幕大小,这个可跟mVirtualDisplay 一起控制屏幕大小
        //mVirtualDisplay 是将屏幕设置成多大多小，setVideoSize是输出文件时屏幕多大多小
        mMediaRecorder.setVideoSize(1280, 720);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置码率 高清的话的要数越大
        mMediaRecorder.setVideoEncodingBitRate((int) (1280 * 720 * 2.6));
        mMediaRecorder.setVideoFrameRate(20);
        try {
            //准备
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clearAll() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
    public String getRecordFilePath() {
        return mRecordFilePath;
    }

    public String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_TYPE_COUNT_DOWN: {
                String str = null;
                boolean enough = RecordFileUtils.getSDFreeMemory() / (1024 * 1024) < 4;
                if (enough) {
                    str = "No enough space";
                    stopRecord(str);
                    mRecordSeconds = 0;
                    break;
                }
                mRecordSeconds++;
                int minute = 0, second = 0;
                if (mRecordSeconds >= 60) {
                    minute = mRecordSeconds / 60;
                    second = mRecordSeconds % 60;
                } else {
                    second = mRecordSeconds;
                }
                RecordUtils.onRecording("0" + minute + ":" + (second < 10 ? "0" + second : second + ""));
                if (mRecordSeconds < 5 * 60) {
                    mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
                } else if (mRecordSeconds == 5 * 60) {
                    str = "Time enough";
                    stopRecord(str);
                    mRecordSeconds = 0;
                }
                break;
            }
        }
        return true;
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }

}