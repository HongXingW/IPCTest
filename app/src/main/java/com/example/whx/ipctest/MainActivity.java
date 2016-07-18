package com.example.whx.ipctest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.whx.ipctest.aidl.BookManagerActivity;
import com.example.whx.ipctest.message.MessengerActivity;
import com.example.whx.ipctest.aidl.Book;
import com.example.whx.ipctest.model.User;
import com.example.whx.ipctest.utils.MyConstants;
import com.example.whx.ipctest.utils.MyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button toSecond;
    Button toMessenger,toBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toSecond = (Button)findViewById(R.id.to_second);
        toSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });

        toMessenger = (Button)findViewById(R.id.to_messenger);
        toMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessengerActivity.class);
                startActivity(intent);
            }
        });

        toBookManager = (Button)findViewById(R.id.to_book_manager);
        toBookManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //persistToFile();
    }

    private void persistToFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User(1,"hello",false);
                user.setBook(new Book(1,"Java"));

                File dir = new File(MyConstants.PRIMARY_PATH);

                if(!dir.exists()){
                    dir.mkdirs();
                }

                File cachedFile = new File(MyConstants.CACHE_FILE_PATH);
                ObjectOutputStream objectOutputStream = null;

                try{

                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedFile));
                    objectOutputStream.writeObject(user);

                    Log.e(TAG,"persist user = "+user);
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                }finally {

                    MyUtil.close(objectOutputStream);
                }
            }
        }).start();
    }
}
