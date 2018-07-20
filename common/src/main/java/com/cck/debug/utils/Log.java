package com.cck.debug.utils;

import java.net.Socket;

public class Log {
    public static void i(String msg) {
        System.out.println("[Server]:"+msg);
    }

    public static void i(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(bytes[i])).append(" ");
        }
        i(sb.toString());
    }

    public static void e(Throwable e) {
        Log.e(e.getClass().getName()+":"+e.getMessage());
    }


    public static void e(String msg) {
        System.out.println("[Server]:"+msg);
    }


}
