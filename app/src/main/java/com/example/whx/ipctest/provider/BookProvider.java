package com.example.whx.ipctest.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by whx on 2016/7/19.
 */
public class BookProvider extends ContentProvider{

    private static final String TAG = "BookProvider";

    public static final String AUTHORITY = "com.example.whx.ipctest.provider";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/user");

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {

        Log.e(TAG,"current thread :"+Thread.currentThread().getName());

        mContext = getContext();

        new Thread(){
            @Override
            public void run() {
                initProviderData();
            }
        }.start();
        return true;
    }

    private void initProviderData(){
        mDb = new DbOpenHelper(mContext).getWritableDatabase();
        mDb.execSQL("delete from "+DbOpenHelper.BOOK_TABLE_NAME);
        mDb.execSQL("delete from "+DbOpenHelper.USER_TABLE_NAME);
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG,"current thread :"+Thread.currentThread().getName());

        String table = getTableName(uri);
        if(table == null){
            throw new IllegalArgumentException("unsupported uri: "+uri);
        }

        return mDb.query(table,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table = getTableName(uri);
        if(table == null){
            throw new IllegalArgumentException("unsupported uri: "+uri);
        }
        mDb.insert(table,null,values);
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String table = getTableName(uri);
        if(table == null){
            throw new IllegalArgumentException("unsupported uri: "+uri);
        }

        int count = mDb.delete(table,selection,selectionArgs);
        if(count > 0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = getTableName(uri);
        if(table == null){
            throw new IllegalArgumentException("unsupported uri: "+uri);
        }

        int row = mDb.update(table,values,selection,selectionArgs);
        if(row > 0){
            mContext.getContentResolver().notifyChange(uri,null);
        }

        return row;
    }

    private String getTableName(Uri uri){

        String tableName = null;
        switch (sUriMatcher.match(uri)){
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;

        }
        return tableName;
    }
}
