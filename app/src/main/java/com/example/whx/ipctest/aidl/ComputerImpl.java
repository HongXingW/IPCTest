package com.example.whx.ipctest.aidl;

import android.os.RemoteException;

/**
 * Created by whx on 2016/7/22.
 */
public class ComputerImpl extends IComputer.Stub{
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
