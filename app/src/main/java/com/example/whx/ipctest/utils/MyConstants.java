package com.example.whx.ipctest.utils;

import android.os.Environment;

/**
 * Created by whx on 2016/7/15.
 */
public class MyConstants {

    public static final String PRIMARY_PATH = Environment.getExternalStorageDirectory().getPath()+"/ipctest/";

    public static final String CACHE_FILE_PATH = PRIMARY_PATH+"usercache";

    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVICE = 1;
}
