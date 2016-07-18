package com.example.whx.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<>();

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

            if(!mListenerList.contains(listener)){
                mListenerList.add(listener);
            }else {
                Log.e(TAG,"listener has already registered");
            }

            Log.e(TAG,"listener list size: "+mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {

            if (mListenerList.contains(listener)){
                mListenerList.remove(listener);
            }else{
                Log.e(TAG,"not found the listener");
            }

            Log.e(TAG,"listener list size: "+mListenerList.size());
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
        return mBinder;
    }

    private void onNewBookArrived(Book book) throws RemoteException{
        mBookList.add(book);
        Log.e(TAG,"onNewBookArrived, notify "+mListenerList.size()+" listeners");

        //通知每一个注册监听的用户
        for(IOnNewBookArrivedListener listener : mListenerList){
            Log.e(TAG,"notify listener: "+listener);
            listener.onNewBookArrived(book);
        }
    }

    //每隔一段时间添加一本书
    class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()){
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
