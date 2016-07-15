package com.example.whx.ipctest;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.example.whx.ipctest.utils.MyUtil;

/**
 * Created by whx on 2016/7/15.
 */
public class MyApplication extends Application{

    private final static String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        String processName = MyUtil.getProcessName(getApplicationContext(), Process.myPid());

        Log.e(TAG,"Application start, process name= "+processName);
    }
}
