package com.example.whx.ipctest.aidl;

import android.os.RemoteException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by whx on 2016/7/22.
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub{

    private String content;

    @Override
    public String encrypt(String content) throws RemoteException {

        this.content = content;
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(content.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        if(content == null){
            return "error, can not decrypt this string";
        }
        return content;
    }
}
