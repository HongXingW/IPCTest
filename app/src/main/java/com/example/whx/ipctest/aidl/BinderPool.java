package com.example.whx.ipctest.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Created by whx on 2016/7/22.
 */
public class BinderPool {

    private static final String TAG = "BinderTool";
    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SCURITY_CENTER = 1;

    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BinderPool sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context context){
        mContext = context;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context){

        if(sInstance == null){
            synchronized (BinderPool.class){
                if(sInstance == null){
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 连接binder池service
     */
    private synchronized void connectBinderPoolService(){
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);

        Intent service = new Intent(mContext,BinderPoolService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await();
        }catch (InterruptedException e){
            Log.e(TAG,e.toString());
        }
    }

    /**
     * 根据binder的标识码来得到binder对象
     * @param binderCode
     * @return
     */
    public IBinder queryBinder(int binderCode){

        IBinder binder = null;
        try{
            if(mBinderPool != null){
                binder = mBinderPool.queryBinder(binderCode);
            }
        }catch (RemoteException e){
            Log.e(TAG,e.toString());
        }

        return binder;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (RemoteException e){

                Log.e(TAG,e.toString());
            }

            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG,"bind died");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;

            connectBinderPoolService();
        }
    };

    /**
     * 根据不同的code返回不同的binder
     */
    public static class BinderPoolImpl extends IBinderPool.Stub{

        public BinderPoolImpl(){
            super();
        }
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {

            IBinder binder = null;
            switch (binderCode){
                case BINDER_SCURITY_CENTER:
                    binder = new SecurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputerImpl();
                    break;
                default:
                    break;
            }

            return binder;
        }
    }
}
