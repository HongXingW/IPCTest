package com.example.whx.ipctest.aidl;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.whx.ipctest.R;

/**
 * Created by whx on 2016/7/22.
 */
public class BinderPoolActivity extends AppCompatActivity{

    private static final String TAG = "BinderPoolActivity";

    private ISecurityCenter mSecurityCenter;
    private IComputer mComputer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);

        new Thread(){
            @Override
            public void run() {
                doWork();
            }
        }.start();
    }

    private void doWork(){
        BinderPool binderPool = BinderPool.getInstance(this);

        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SCURITY_CENTER);

        mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
        Log.e(TAG,"visit ISecurityCenter");
        String str = "hello - 安卓";
        Log.e(TAG,"content: "+str);

        try{
            String sec = mSecurityCenter.encrypt(str);
            Log.e(TAG,"encrypt: "+sec);

            Log.e(TAG,"decrypt: "+mSecurityCenter.decrypt(sec));
        }catch (RemoteException e){
            Log.e(TAG,e.toString());
        }

        IBinder computerBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        Log.e(TAG,"visit IComputer");
        mComputer = ComputerImpl.asInterface(computerBinder);
        try{
            Log.e(TAG,"3 + 5 = "+mComputer.add(3,5));
        }catch (RemoteException e){
            Log.e(TAG,e.toString());
        }

    }
}
