package com.example.whx.ipctest.message;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.whx.ipctest.utils.MyConstants;

/**
 * Created by whx on 2016/7/16.
 */
public class MessengerService extends Service{

    private static final String TAG = "MessengerService";

    private static class MessengerHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_CLIENT:
                    Log.e(TAG,"messenger from client:"+msg.getData().getString("msg"));

                    Messenger client = msg.replyTo;
                    Message replyMessage = Message.obtain(null,MyConstants.MSG_FROM_SERVICE);
                    Bundle data = new Bundle();
                    data.putString("service","我现在有事不在，稍后和您练习，联系");
                    replyMessage.setData(data);
                    try {
                        client.send(replyMessage);
                    }catch (RemoteException e){
                        Log.e(TAG,e.toString());
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger messenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
