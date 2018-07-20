package com.cck.debug.request.impl;

import com.cck.debug.common.MessageType;
import com.cck.debug.request.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PingRequest extends Request {
    public static PingRequest obtain() {
        return new PingRequest();
    }
    @Override
    public void request(DataInputStream in, DataOutputStream out) throws IOException {
        out.writeInt(MessageType.MSG_PING);
    }
}
