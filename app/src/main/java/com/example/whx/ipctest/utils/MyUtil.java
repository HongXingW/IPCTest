package com.example.whx.ipctest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.app.ActivityManager.RunningAppProcessInfo;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Created by whx on 2016/7/15.
 */
public class MyUtil {

    public static String getProcessName(Context context,int pid){

        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();

        if(runningApps == null){
            return null;
        }else{
            for (RunningAppProcessInfo info : runningApps){
                if(info.pid == pid){
                    return info.processName;
                }
            }
        }

        return null;
    }

    public static void close(Closeable closeable){
        try{
            if(closeable != null){
                closeable.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void executeInThread(Runnable runnable){
        new Thread(runnable).start();
    }

    public static boolean isEmpty(String string){
        return string == null||string.equals("");
    }
}
