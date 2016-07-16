package com.example.whx.ipctest.message;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.whx.ipctest.R;
import com.example.whx.ipctest.utils.MyConstants;

/**
 * Created by whx on 2016/7/16.
 */
public class MessengerActivity extends AppCompatActivity{

    private static final String TAG = "MessengerActivity";

    private Messenger messenger;
    private TextView textView;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);

            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();

            data.putString("msg","Hello this is client");
            msg.setData(data);
            //接收服务端回复的messenger
            msg.replyTo=getReplyMessenger;

            try {
                messenger.send(msg);
            }catch (RemoteException e){
                Log.e(TAG,e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Messenger getReplyMessenger = new Messenger(new MessengerHandler());

    private class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_SERVICE:
                    String reply = msg.getData().getString("service");
                    textView.setText(reply);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messenger);
        textView = (TextView)findViewById(R.id.text);

        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
