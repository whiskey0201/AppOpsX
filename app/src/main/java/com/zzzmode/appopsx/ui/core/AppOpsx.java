package com.zzzmode.appopsx.ui.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zzzmode.appopsx.OpsxManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zl on 2016/11/19.
 */

public class AppOpsx {

    private static final String LOG_FILE="appopsx.log";

    private static OpsxManager sManager;

    public static OpsxManager getInstance(Context context) {
        if(sManager == null){
            synchronized (AppOpsx.class){
                if(sManager == null){
                    sManager=new OpsxManager(context.getApplicationContext(),buildConfig(context));
                }
            }
        }
        return sManager;
    }

    public static void updateConfig(Context context){
        if(sManager!=null) {
            OpsxManager.Config config = sManager.getConfig();
            if(config != null){
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                config.allowBgRunning=sp.getBoolean("allow_bg_remote",true);
                config.useAdb=sp.getBoolean("use_adb", false);
            }
        }
    }


    private static OpsxManager.Config buildConfig(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        OpsxManager.Config config=new OpsxManager.Config();
        config.allowBgRunning=sp.getBoolean("allow_bg_remote",true);
        config.logFile=context.getFileStreamPath(LOG_FILE).getAbsolutePath();
        config.useAdb=sp.getBoolean("use_adb", false);
        Log.e("test", "buildConfig --> "+context.getFileStreamPath(LOG_FILE).getAbsolutePath());

        return config;
    }

    public static String readLogs(Context context){
        StringBuilder sb=new StringBuilder();
        File file = context.getFileStreamPath(LOG_FILE);
        if(file.exists()){
            BufferedReader br=null;
            try{
                br=new BufferedReader(new FileReader(file));
                String line=br.readLine();

                while (line!=null){
                    sb.append(line);
                    sb.append("\n");
                    line=br.readLine();
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(br != null){
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else {

            sb.append("没有日志");
        }

        return sb.toString();
    }


    private static String readProcess(){
        Process exec=null;
        BufferedReader br=null;
        try {
            exec = Runtime.getRuntime().exec("su -C 'ps'");
            br=new BufferedReader(new InputStreamReader(exec.getInputStream()));
            StringBuilder sb=new StringBuilder();
            String line=br.readLine();
            while (line != null){
                Log.e("test", "readProcess --> "+line);
                //sb.append(line);
                //sb.append("\n");
                line=br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(exec != null){
                exec.destroy();
            }
        }
        return null;
    }
}
