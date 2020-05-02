package com.ProcessModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class RecordUtils {
    private static RecordService s_ScreenRecordService;
    private static List<RecordListener> s_RecordListener = new ArrayList<>();
    private static List<OnPageRecordListener> s_PageRecordListener = new ArrayList<>();
    public static boolean s_IsRecordingTipShowing = false;

    public static boolean isScreenRecordEnable(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ;
    }

    public static void setScreenService(RecordService screenService){
        s_ScreenRecordService = screenService;
    }

    public static void clear(){
        if ( isScreenRecordEnable() && s_ScreenRecordService != null){
            s_ScreenRecordService.clearAll();
            s_ScreenRecordService = null;
        }
        if (s_RecordListener != null && s_RecordListener.size() > 0){
            s_RecordListener.clear();
        }
        if (s_PageRecordListener != null && s_PageRecordListener.size() > 0 ){
            s_PageRecordListener.clear();
        }
    }

    public static void startScreenRecord(Activity activity, int requestCode) {
        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){
                if (!s_ScreenRecordService.isReady()){
                    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.
                            getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    if (mediaProjectionManager != null){
                        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
                        PackageManager packageManager = activity.getPackageManager();
                        if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null){
                            activity.startActivityForResult(intent,requestCode);
                        }else {
                            Toast.makeText(activity, "No music", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    s_ScreenRecordService.startRecord();
                }
            }
        }
    }

    public static void setUpData(int resultCode,Intent resultData) throws Exception{
        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){
                s_ScreenRecordService.setResultData(resultCode,resultData);
                s_ScreenRecordService.startRecord();
            }
        }
    }

    public static void stopScreenRecord(Context context){
        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null && s_ScreenRecordService.ismIsRunning()){
                String str = "Stop record";
                s_ScreenRecordService.stopRecord(str);
            }
        }
    }

    public static String getScreenRecordFilePath(){
        if (isScreenRecordEnable() && s_ScreenRecordService!= null) {
            return s_ScreenRecordService.getRecordFilePath();
        }
        return null;
    }

    public static boolean isCurrentRecording(){
        if (isScreenRecordEnable() && s_ScreenRecordService!= null) {
            return s_ScreenRecordService.ismIsRunning();
        }
        return false;
    }
    public static boolean isRecodingTipShow(){
        return s_IsRecordingTipShowing;
    }
    public static void setRecordingStatus(boolean isShow){
        s_IsRecordingTipShowing = isShow;
    }
    public static void clearRecordElement(){
        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null ){
                s_ScreenRecordService.clearRecordElement();
            }
        }
    }
    public static void addRecordListener(RecordListener listener){
        if (listener != null && !s_RecordListener.contains(listener)){
            s_RecordListener.add(listener);
        }
    }
    public static void removeRecordListener(RecordListener listener){
        if (listener != null && s_RecordListener.contains(listener)){
            s_RecordListener.remove(listener);
        }
    }
    public static void addPageRecordListener( OnPageRecordListener listener){
        if (listener != null && !s_PageRecordListener.contains(listener)){
            s_PageRecordListener.add(listener);
        }
    }
    public static void removePageRecordListener( OnPageRecordListener listener){
        if (listener != null && s_PageRecordListener.contains(listener)){
            s_PageRecordListener.remove(listener);
        }
    }
    public static void onPageRecordStart(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onStartRecord();
            }
        }
    }
    public static void onPageRecordStop(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onStopRecord();
            }
        }
    }
    public static void onPageBeforeShowAnim(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onBeforeShowAnim();
            }
        }
    }
    public static void onPageAfterHideAnim(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onAfterHideAnim();
            }
        }
    }

    public static void startRecord(){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onStartRecord();
                Log.i("xxx", "startRecord: ");
            }
        }
    }

    public static void pauseRecord(){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onPauseRecord();
            }
        }
    }
    public static void resumeRecord(){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onResumeRecord();
            }
        }
    }

    public static void onRecording(String timeTip){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onRecording(timeTip);
            }
        }
    }

    public static void stopRecord(String stopTip){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onStopRecord( stopTip);
            }
        }
    }

    public interface RecordListener{
        void onStartRecord();
        void onPauseRecord();
        void onResumeRecord();
        void onStopRecord(String stopTip);
        void onRecording(String timeTip);
    }

    public interface OnPageRecordListener {
        void onStartRecord();
        void onStopRecord();
        void onBeforeShowAnim();
        void onAfterHideAnim();
    }
}
