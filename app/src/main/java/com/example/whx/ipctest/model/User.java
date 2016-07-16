package com.example.whx.ipctest.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 *
 * Created by whx on 2016/7/15.
 */
public class User implements Parcelable , Serializable{//若想被序列化到文件中必须实现serializable接口

    private static final long serialVersionUID = 1L;

    private int userId;
    private String userName;
    private boolean isMale;

    private Book book;

    public User(){

    }
    public User(int userId, String userName,boolean isMale){
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
        this.book = null;
    }

    private User(Parcel in){
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readInt() == 1;

        book = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeInt(isMale ? 1 : 0);

        dest.writeParcelable(book,flags);
    }

    public final static Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return String.format(
                "User:{userId:%s, userName:%s, isMale:%s}, with book:{%s}",
                userId, userName, isMale, book);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
