// IOnNewBookArrivedListener.aidl
package com.example.whx.ipctest.aidl;

import com.example.whx.ipctest.aidl.Book;

interface IOnNewBookArrivedListener {

    /**
    * 新书到来通知
    */
    void onNewBookArrived(in Book newBook);
}
