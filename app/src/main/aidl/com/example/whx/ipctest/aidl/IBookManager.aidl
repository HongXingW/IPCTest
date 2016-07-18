// IBookManager.aidl
package com.example.whx.ipctest.aidl;

import com.example.whx.ipctest.aidl.Book;
import com.example.whx.ipctest.aidl.IOnNewBookArrivedListener;

interface IBookManager {

    List<Book> getBookList();
    void addBook(in Book book);

    /**
    *监听新书通知
    */
    void registerListener(IOnNewBookArrivedListener listener);

    /**
    *取消监听
    */
    void unregisterListener(IOnNewBookArrivedListener listener);
}
