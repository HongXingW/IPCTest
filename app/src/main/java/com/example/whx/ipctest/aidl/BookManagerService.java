package com.example.whx.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by whx on 2016/7/18.
 */
public class BookManagerService extends Service{

    private static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    //private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {

//            if(!mListenerList.contains(listener)){
//                mListenerList.add(listener);
//            }else {
//                Log.e(TAG,"listener has already registered");
//            }

            mListenerList.register(listener);

            int N = mListenerList.beginBroadcast();
            Log.e(TAG,"listener list size: "+N);
            mListenerList.finishBroadcast();
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {

//            if (mListenerList.contains(listener)){
//                mListenerList.remove(listener);
//            }else{
//                Log.e(TAG,"not found the listener");
//            }

            mListenerList.unregister(listener);

            int N = mListenerList.beginBroadcast();
            Log.e(TAG,"listener list size: "+N);
            mListenerList.finishBroadcast();
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

            //验证权限
            int check = checkCallingOrSelfPermission("com.example.whx.ipctest.permission.ACCESS_BOOK_SERVICE");

            if(check == PackageManager.PERMISSION_DENIED){
                return false;
            }

            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());

            if(packages != null && packages.length > 0){
                packageName = packages[0];
            }

            if(!packageName.startsWith("com.example")){
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Java"));
        mBookList.add(new Book(2,"Android"));

        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {

        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        //验证权限
        int check = checkCallingOrSelfPermission("com.example.whx.ipctest.permission.ACCESS_BOOK_SERVICE");

        if(check == PackageManager.PERMISSION_DENIED){
            return null;
        }

        return mBinder;
    }

    private void onNewBookArrived(Book book) throws RemoteException{
        mBookList.add(book);
        //Log.e(TAG,"onNewBookArrived, notify "+mListenerList.size()+" listeners");

        int N = mListenerList.beginBroadcast();

        //通知每一个注册监听的用户
        for(int i =0;i<N;i++){
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);

            if(listener != null){

                try {
                    Log.e(TAG,"notify listener: "+listener);
                    listener.onNewBookArrived(book);
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }
            }

        }
        mListenerList.finishBroadcast();
    }

    //每隔一段时间添加一本书
    class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get() && mBookList.size()<10){
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e){
                    Log.e(TAG,e.toString());
                }

                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId,"new book#"+bookId);

                try {
                    onNewBookArrived(newBook);
                }catch (RemoteException e){
                    Log.e(TAG,e.toString());
                }
            }
        }
    }
}
