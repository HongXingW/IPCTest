// IsecurityCenter.aidl
package com.example.whx.ipctest.aidl;

// Declare any non-default types here with import statements

interface ISecurityCenter {

    /**
    *加密字符串
    */
    String encrypt(String content);
    /**
    *解密字符串
    */
    String decrypt(String password);
}
