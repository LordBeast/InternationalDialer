package com.ic.stephen.internationaldialer.CustomUtils;

import android.app.Activity;
import android.content.Context;

import com.ic.stephen.internationaldialer.Constants.DBConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Jarvis on 6/24/2016.
 */
public class FileManager {
    private static FileManager fileManager;

    private FileManager(){

    }

    public static synchronized FileManager getInstance(){
        if(fileManager == null){
            fileManager = new FileManager();
        }
        return fileManager;
    }

    public boolean createFile(String path){
        boolean res = false;
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.length()).trim();
        path = path.substring(0, path.lastIndexOf("/"));
        try{
            File file = new File(createDirectories(path), fileName);
            res = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public boolean appendToLogFile(String content){
        boolean res = false;
        res = appendToFile(DBConstants.LOG_FILE_PATH, content, true);
        return res;
    }

    public boolean appendToFile(Activity activity, String path, String value, boolean createIfNot){
        boolean res = false;
        File file = new File(path);
        if(!file.exists() && createIfNot == true){
            createFile(path);
        }
        try{
            OutputStreamWriter writer = new OutputStreamWriter(activity.openFileOutput(path, Context.MODE_APPEND));
            writer.write(value + "\n\n");
            writer.close();
            res = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public boolean appendToFile(String path, String value, boolean createIfNot){
        boolean res = false;
        File file = new File(path);
        if(!file.exists() && createIfNot == true){
            createFile(path);
        }
        try{
            FileWriter writer = new FileWriter(path, true);
            writer.write(value.trim() + "\n\n");
            writer.flush();
            writer.close();
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean createDirs(String path){
        boolean res = false;
        try{
            File directoryPath = new File(path);
            directoryPath.mkdirs();
            res = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public File createDirectories(String path){
        File directory = new File(path);
        directory.mkdirs();
        return directory;
    }

    public boolean checkIfFileExists(String path){
        File file = new File(path);
        return file.exists();
    }

    public boolean removeDir(String path){
        return false;
    }

}
