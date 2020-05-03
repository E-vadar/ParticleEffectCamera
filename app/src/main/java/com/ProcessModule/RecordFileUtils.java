package com.ProcessModule;

/**
 * Author: He Jingze
 * Description: Some static methods to deal with file write or read operation about recording
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;
import android.content.Context;

public class RecordFileUtils {
    public static boolean deleteSDFile(String path) {
        return deleteSDFile(path, false);
    }

    public static boolean deleteSDFile(String path, boolean deleteParent) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        return deleteFile(file, deleteParent);
    }

    public static boolean deleteFile(File file, boolean deleteParent) {
        boolean flag = false;
        if (file == null) {
            return flag;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    flag = deleteFile(files[i], true);
                    if (!flag) {
                        return flag;
                    }
                }
            }
            if (deleteParent) {
                flag = file.delete();
            }
        } else {
            flag = file.delete();
        }
        file = null;
        return flag;
    }

    public static Uri fileScanVideo(Context context, String videoPath, int videoWidth, int videoHeight,
                                    int videoTime) {
        File file = new File(videoPath);
        if (file.exists()) {
            Uri uri = null;
            long size = file.length();
            String fileName = file.getName();
            long dateTaken = System.currentTimeMillis();
            ContentValues values = new ContentValues(11);
            values.put(MediaStore.Video.Media.DATA, videoPath);
            values.put(MediaStore.Video.Media.TITLE, fileName);
            values.put(MediaStore.Video.Media.DURATION, videoTime * 1000);
            values.put(MediaStore.Video.Media.WIDTH, videoWidth);
            values.put(MediaStore.Video.Media.HEIGHT, videoHeight);
            values.put(MediaStore.Video.Media.SIZE, size);
            values.put(MediaStore.Video.Media.DATE_TAKEN, dateTaken);
            values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Video.Media.DATE_MODIFIED, dateTaken / 1000);
            values.put(MediaStore.Video.Media.DATE_ADDED, dateTaken / 1000);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

            ContentResolver resolver = context.getContentResolver();

            if (resolver != null) {
                try {
                    uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Exception e) {
                    e.printStackTrace();
                    uri = null;
                }
            }

            if (uri == null) {
                MediaScannerConnection.scanFile(context, new String[]{videoPath}, new String[]{"video/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
            }

            return uri;
        }

        return null;
    }

    public static boolean isSDExists() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static long getSDFreeMemory() {
        try {
            if (isSDExists()) {
                File pathFile = Environment.getExternalStorageDirectory();
                StatFs statfs = new StatFs(pathFile.getPath());
                long nBlockSize = statfs.getBlockSize();
                long nAvailBlock = statfs.getAvailableBlocks();
                long nSDFreeSize = nAvailBlock * nBlockSize;
                return nSDFreeSize;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static long getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        if (manager != null) {
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            if (info != null) {
                manager.getMemoryInfo(info);
                return info.availMem / 1024 / 1024;
            }
        }
        return 0;
    }

}
