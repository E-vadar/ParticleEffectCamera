package com.ProcessModule;

/**
 * Author: He Jingze
 * Description: Some static method to deal with repository of effect templates on web server
 */

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import com.MainSystem.WelcomeActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RepositoryUtils {
    public static boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if(!f.exists()) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = "";
        File file = new File(path);
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while (( line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
                instream.close();
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void ReadListFile(String strFilePath) {
        String path = strFilePath;
        File file = new File(path);
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while (( line = buffreader.readLine()) != null) {
                   WelcomeActivity.effectList.add(line);
                }
                instream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String filePath,String fileName) {
        File file = new File(filePath + "/" + fileName);
        file.delete();
    }

    public static boolean download(int type, String fileName, Context context){
        String doc = null ,filetype = null;
        switch(type){
            case 0:
                doc = "/";
                filetype = ".txt";
                break;
            case 1:
                doc = "/image/";
                filetype = ".png";
                break;
            case 2:
                doc = "/template/";
                filetype = ".txt";
                break;
        }
        if(fileIsExists(Environment.getExternalStorageDirectory()+"/download/" + fileName + filetype)){
            return true;
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://q91np8f4n.bkt.clouddn.com" + doc + fileName + filetype));
            request.setDestinationInExternalPublicDir("/download/",fileName + filetype);
            DownloadManager downloadManager= (DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            return false;
        }
    }

}
