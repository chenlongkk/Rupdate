package com.cck.debug.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class Request {
    abstract public void request(DataInputStream in, DataOutputStream out) throws IOException;
    public int getTimeout(){
        return (int)TimeUnit.SECONDS.toMillis(5);
    }
}
