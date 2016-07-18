// IBookManager.aidl
package com.example.whx.ipctest.aidl;

import com.example.whx.ipctest.aidl.Book;

interface IBookManager {

    List<Book> getBookList();
    void addBook(in Book book);
}
