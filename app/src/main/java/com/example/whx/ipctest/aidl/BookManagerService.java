package com.example.whx.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by whx on 2016/7/18.
 */
public class BookManagerService extends Service{

    private static final String TAG = "BookManager";

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Java"));
        mBookList.add(new Book(2,"Android"));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
