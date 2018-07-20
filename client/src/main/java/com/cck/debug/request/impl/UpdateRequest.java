package com.cck.debug.request.impl;

import com.cck.debug.common.MessageType;
import com.cck.debug.request.Request;
import com.cck.debug.utils.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 更新的请求
 * |msgType|plugin_name|plugin_version|   data  | eof |
 *    int     utf         int            byte[]   int
 */
public class UpdateRequest extends Request {
    private String name;
    private int version;
    private String filePath;

    public static UpdateRequest obtain(String name,int version,String path) {
        return new UpdateRequest(name,version,path);
    }

    public UpdateRequest(String mName, int version, String filePath) {
        this.name = mName;
        this.version = version;
        this.filePath = filePath;
    }

    @Override
    public void request(DataInputStream in, DataOutputStream out) throws IOException {
        out.writeInt(MessageType.MSG_UPDATE);
        out.writeUTF(name);
        out.writeInt(version);
        byte[] data = IOUtils.toByteArray(new File(filePath));
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public int getTimeout() {
        return (int)TimeUnit.SECONDS.toMillis(30);
    }
}
