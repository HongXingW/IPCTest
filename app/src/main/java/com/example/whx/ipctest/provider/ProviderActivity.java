package com.example.whx.ipctest.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.whx.ipctest.R;
import com.example.whx.ipctest.aidl.Book;
import com.example.whx.ipctest.model.User;

/**
 * Created by whx on 2016/7/19.
 */
public class ProviderActivity extends AppCompatActivity{

    private TextView books,users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_provider);

        books = (TextView)findViewById(R.id.books);
        users = (TextView)findViewById(R.id.users);

        Uri bookUri = Uri.parse("content://com.example.whx.ipctest.provider/book");
        insertIntoBook(bookUri);
        queryBook(bookUri);

        Uri userUri = Uri.parse("content://com.example.whx.ipctest.provider/user");
        insertIntoUser(userUri);
        queryUser(userUri);

    }

    private void insertIntoUser(Uri uri){
        ContentValues values = new ContentValues();
        values.put("_id",1);
        values.put("name","Tom");
        values.put("sex",1);

        getContentResolver().insert(uri,values);
    }

    private void queryUser(Uri uri){
        Cursor cursor = getContentResolver().query(uri,new String[]{"_id","name","sex"},null,null,null);

        String s="";

        while (cursor.moveToNext()){
            User user = new User(cursor.getInt(0),cursor.getString(1),cursor.getInt(2)==1);
            s += user.toString();
        }
        cursor.close();
        users.setText(s);
    }
    private void insertIntoBook(Uri uri){
        ContentValues values = new ContentValues();
        values.put("_id",1);
        values.put("name","Java");

        getContentResolver().insert(uri,values);
    }

    private void queryBook(Uri uri){

        Cursor cursor = getContentResolver().query(uri,new String[]{"_id","name"},null,null,null);

        String s="";

        while (cursor.moveToNext()){
            Book book = new Book(cursor.getInt(0),cursor.getString(1));
            s += book.toString();
        }
        cursor.close();
        books.setText(s);
    }
}
