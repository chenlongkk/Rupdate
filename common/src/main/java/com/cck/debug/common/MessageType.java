package com.cck.debug.common;

public class MessageType {
    public static final int MSG_EOF         = 0x01;
    public static final int MSG_TOAST       = 0x02;
    public static final int MSG_PING        = 0x03;
    public static final int MSG_RESTART     = 0x04;
    public static final int MSG_UPDATE      = 0x05;

    public static final int MSG_RESULT_SUCCESS = 0x99;
    public static final int MSG_RESULT_FAIL    = 0x98;

    public static final int PORT = 29773;
    public static final int MAGIC = 0xCCFF43FD;
    public static final int VERSION = 1;
}
