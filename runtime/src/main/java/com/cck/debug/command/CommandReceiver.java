package com.cck.debug.command;

import java.io.InputStream;
import java.io.OutputStream;

public interface CommandReceiver {
    int handlePing();
    int handleToast(String msg);
    int handleRestart();
    int handleUpdate(String pluginName, int pluginVersion, byte[] data, int len);
}
