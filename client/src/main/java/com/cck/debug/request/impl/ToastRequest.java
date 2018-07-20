package com.cck.debug.request.impl;

import com.cck.debug.common.MessageType;
import com.cck.debug.request.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ToastRequest extends Request {
    public String mMessage;

    public static ToastRequest obtain(String msg) {
        return new ToastRequest(msg);
    }

    private ToastRequest(String msg) {
        this.mMessage = msg;
    }

    @Override
    public void request(DataInputStream in, DataOutputStream out) throws IOException {
        if(mMessage != null && !mMessage.isEmpty()) {
            out.writeInt(MessageType.MSG_TOAST);
            out.writeUTF(mMessage);
        }
    }
}
