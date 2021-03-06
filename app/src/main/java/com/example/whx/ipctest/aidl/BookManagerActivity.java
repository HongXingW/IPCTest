package com.example.whx.ipctest.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.whx.ipctest.R;

import java.util.List;

/**
 * Created by whx on 2016/7/18.
 */
public class BookManagerActivity extends AppCompatActivity{

    private static final String TAG = "BMActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private TextView textView;
    private Button button;

    private IBookManager manager;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG,"receive new book: "+msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            }catch (RemoteException e){
                Log.e(TAG,e.toString());
            }

            manager = bookManager;
            try{
                List<Book> list = bookManager.getBookList();
                Log.e(TAG,"list type: "+ list.getClass().getCanonicalName());

                Book newBook = new Book(3,"Big Data");
                bookManager.addBook(newBook);

                List<Book> newList = bookManager.getBookList();
                Log.e(TAG,"list is: "+newList.toString());

                bookManager.registerListener(mOnNewBookArrivedListener);
            }catch (RemoteException e){
                Log.e(TAG,e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            manager = null;
            Log.e(TAG,"binder died");
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {

            if(manager == null){
                return;
            }
            manager.asBinder().unlinkToDeath(mDeathRecipient,0);
            manager = null;

            //重新绑定远程Service
            Intent intent = new Intent(BookManagerActivity.this,BookManagerService.class);
            bindService(intent,connection,BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmanager);

        Intent intent = new Intent(this,BookManagerService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

        textView = (TextView)findViewById(R.id.book_text);
        button = (Button)findViewById(R.id.get_list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String s = manager.getBookList().toString();

                    textView.setText(s);
                }catch (RemoteException e){
                    Log.e(TAG,e.toString());
                }

            }
        });
    }

    @Override
    protected void onDestroy() {

        if(manager != null && manager.asBinder().isBinderAlive()){

            try{
                Log.e(TAG,"unregister listener: "+mOnNewBookArrivedListener);

                manager.unregisterListener(mOnNewBookArrivedListener);
            }catch (RemoteException e){
                Log.e(TAG,e.toString());
            }
        }
        unbindService(connection);
        super.onDestroy();
    }

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub(){
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {

            handler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,newBook).sendToTarget();
        }
    };
}
