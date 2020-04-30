package com.EffectSystem;

import com.ParticleEffectCamera.WelcomeActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RepositoryUtil {

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

}
