package com.example.whx.ipctest.socket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.whx.ipctest.R;
import com.example.whx.ipctest.utils.MyUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by whx on 2016/7/21.
 */
public class TcpClientActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TcpClientActivity";

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private TextView text;
    private EditText message;
    private Button sendBtn;

    private PrintWriter mPrintWriter;
    private Socket mClient;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MESSAGE_RECEIVE_NEW_MSG:
                    text.setText(text.getText()+(String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    sendBtn.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        text = (TextView)findViewById(R.id.chat_text);
        message = (EditText)findViewById(R.id.message);
        sendBtn = (Button)findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);

        Intent intent = new Intent(this,TcpServerService.class);
        startService(intent);

        new Thread(){
            @Override
            public void run() {
                connectTcpServer();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if(mClient != null){
            try{
                mClient.shutdownInput();
                mClient.close();
            }catch (IOException e){
                Log.e(TAG,e.toString());
            }
        }
        super.onDestroy();
    }

    private void connectTcpServer(){

        Socket socket = null;
        //断线重连机制，每隔1秒连一次
        while (socket == null){
            try{
                socket = new Socket("localhost",8688);
                mClient = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

                handler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.e(TAG,"connect success");

            } catch (IOException e){

                SystemClock.sleep(1000);

                Log.e(TAG,e.toString());
            }
        }

        try{
            //接收服务端消息
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            while (!TcpClientActivity.this.isFinishing()){

                String msg = br.readLine();

                if(msg != null){

                    Log.e(TAG,"receive: "+msg);

                    String time = formatDateTime(System.currentTimeMillis());
                    final String showMsg = "server "+time+":"+msg+"\n";

                    handler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showMsg).sendToTarget();
                }
            }

            Log.e(TAG,"quit...");

            MyUtil.close(mPrintWriter);
            MyUtil.close(br);

            socket.close();
        } catch (IOException e){
            Log.e(TAG,e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        if(v == sendBtn){
            final String msg = message.getText().toString();
            if(!MyUtil.isEmpty(msg) && mPrintWriter != null){
                mPrintWriter.println(msg);
                message.setText("");

                String time = formatDateTime(System.currentTimeMillis());

                final String showMsg = "self " + time +":"+msg+"\n";
                text.setText(text.getText()+showMsg);
            }
        }
    }
    //获取发送时间
    private String formatDateTime(long time){
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }
}
