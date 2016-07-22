// IBinderPool.aidl
package com.example.whx.ipctest.aidl;

// Declare any non-default types here with import statements

interface IBinderPool {

    /**
    * @param binderCode 是binder的唯一标识
    * @return 根据code返回binder
    */
    IBinder queryBinder(int binderCode);
}
