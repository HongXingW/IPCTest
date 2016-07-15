package com.example.whx.ipctest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.whx.ipctest.model.User;
import com.example.whx.ipctest.utils.MyConstants;
import com.example.whx.ipctest.utils.MyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by whx on 2016/7/15.
 */
public class SecondActivity extends AppCompatActivity{

    private static final String TAG = "SecondActivity";
    Button toThird;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        toThird = (Button)findViewById(R.id.to_third);
        toThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this,ThirdActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recoverFromFile();
    }

    public void recoverFromFile(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = null;
                File cachedFile = new File(MyConstants.CACHE_FILE_PATH);
                if(cachedFile.exists()){
                    ObjectInputStream objectInputStream = null;
                    try{
                        objectInputStream = new ObjectInputStream(new FileInputStream(cachedFile));
                        user = (User)objectInputStream.readObject();
                        Log.e(TAG,"recover user = "+user);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }finally {
                        MyUtil.close(objectInputStream);
                    }
                }
            }
        }).start();
    }
}
