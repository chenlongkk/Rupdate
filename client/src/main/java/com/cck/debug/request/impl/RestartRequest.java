package com.cck.debug.request.impl;

import com.cck.debug.common.MessageType;
import com.cck.debug.request.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RestartRequest extends Request {
    public static RestartRequest obtain() {
        return new RestartRequest();
    }
    @Override
    public void request(DataInputStream in, DataOutputStream out) throws IOException {
        out.writeInt(MessageType.MSG_RESTART);
    }
}
