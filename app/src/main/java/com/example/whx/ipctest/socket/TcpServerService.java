package com.example.whx.ipctest.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.whx.ipctest.utils.MyUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by whx on 2016/7/20.
 */
public class TcpServerService extends Service{

    private static final String TAG = "TcpServerService";

    private boolean mIsServiceDestroyed = false;

    private String[] mMessages = new String[]{
            "你好，约吗",
            "叔叔，我们不约",
            "嘿嘿嘿",
            "小妹妹，我是好人",
            "我去洗澡了"
    };

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable{

        @Override
        public void run() {

            ServerSocket serverSocket = null;
            try {
                //监听本地端口8688
                serverSocket = new ServerSocket(8688);
            }catch (IOException e){
                Log.e(TAG,e.toString());
            }

            while (!mIsServiceDestroyed){
                try {
                    //接收客户端请求
                    final Socket client = serverSocket.accept();
                    Log.e(TAG,"accept");

                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            }catch (IOException e){
                                Log.e(TAG,e.toString());
                            }
                        }
                    }.start();
                }catch (IOException e){
                    Log.e(TAG,e.toString());
                }
            }
        }
    }

    /**
     * 通过socket响应客户端
     * @param client socket
     * @throws IOException
     */
    private void responseClient(Socket client) throws IOException{

        //用于接收客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        out.println("欢迎来到小黑屋");

        while (!mIsServiceDestroyed){
            String str = in.readLine();

            if(str == null){
                //客户端断开连接
                break;
            }
            Log.e(TAG,str);

            //产生随机的一句话
            int i = new Random().nextInt(mMessages.length);
            String msg = mMessages[i];
            out.println(msg);

            Log.e(TAG,"send: "+msg);
        }

        Log.e(TAG,"client quit.");
        //关闭流
        MyUtil.close(out);
        MyUtil.close(in);

        client.close();
    }
}
