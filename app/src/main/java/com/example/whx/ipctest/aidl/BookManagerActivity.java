package com.example.whx.ipctest.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.whx.ipctest.R;

import java.util.List;

/**
 * Created by whx on 2016/7/18.
 */
public class BookManagerActivity extends AppCompatActivity{

    private static final String TAG = "BMActivity";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IBookManager bookManager = IBookManager.Stub.asInterface(service);

            try{
                List<Book> list = bookManager.getBookList();
                Log.e(TAG,"list type: "+ list.getClass().getCanonicalName());

                Book newBook = new Book(3,"Big Data");
                bookManager.addBook(newBook);

                List<Book> newList = bookManager.getBookList();
                Log.e(TAG,"list is: "+newList.toString());

            }catch (RemoteException e){
                Log.e(TAG,e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmanager);

        Intent intent = new Intent(this,BookManagerService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
