package com.cck.debug.request;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.cck.debug.common.MessageType;
import com.cck.debug.utils.IOUtils;
import com.cck.debug.utils.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.cck.debug.common.MessageType.MAGIC;
import static com.cck.debug.common.MessageType.VERSION;

public enum DeviceClient {
    INSTANCE;
    private ExecutorService mConnectPool = Executors.newCachedThreadPool();
    private IDevice mCurrentDevice;
    private final Object mWaitConnect = new Object();
    private DeviceStatusCallback mDeviceCallback;
    public interface DeviceStatusCallback {
        int STATUS_CONNECT    = 1;
        int STATUS_DISCONNECT = 2;
        void onStatusChange(IDevice device,int status);
    }

    public void init(DeviceStatusCallback callback) {
        mDeviceCallback = callback;
        AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge adb = AndroidDebugBridge.createBridge();
        if(adb != null) {
            if(!adb.hasInitialDeviceList()) {
                waitForDevice();
            }
            IDevice[] devices = adb.getDevices();
            if (devices != null && devices.length > 0) {
                mCurrentDevice = devices[0];
            }
        }
        if(mCurrentDevice != null) {
            Log.i("connect:"+mCurrentDevice.getName());
        }else{
            Log.i("can't connect device");
        }
    }


    private void waitForDevice() {
        AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            @Override
            public void deviceConnected(IDevice device) {
                if(mDeviceCallback != null) {
                    mDeviceCallback.onStatusChange(device,DeviceStatusCallback.STATUS_CONNECT);
                }
                notifyWait();
            }

            @Override
            public void deviceDisconnected(IDevice device) {
                if(device.getName().equals(mCurrentDevice.getName())) {
                    mCurrentDevice = null;
                }

                if(mDeviceCallback != null) {
                    mDeviceCallback.onStatusChange(device,DeviceStatusCallback.STATUS_DISCONNECT);
                }
                notifyWait();
            }

            @Override
            public void deviceChanged(IDevice device, int changeMask) {
                notifyWait();
            }

            private void notifyWait() {
                synchronized (mWaitConnect) {
                    mWaitConnect.notifyAll();
                }
            }
        });

        synchronized (mWaitConnect) {
            try{
                mWaitConnect.wait(800);
            }catch (InterruptedException ignore) {

            }
        }
    }

    public void connect(final Request request,final String serverName,final ResponseCallback callback){
        mConnectPool.submit(new Runnable() {
            @Override
            public void run() {
                if(callback == null) return ;
                if(request == null) {
                    callback.onError(new IllegalArgumentException("request is NULL."));
                    return ;
                }

                if(mCurrentDevice == null) {
                    callback.onError(new IOException("device is not connected."));
                    return ;
                }

                try{
                    mCurrentDevice.createForward(MessageType.PORT,serverName,IDevice.DeviceUnixSocketNamespace.ABSTRACT);
                }catch (Exception e) {
                    Log.e(e);
                }

                Socket socket = null;
                try{
                    socket = new Socket("127.0.0.1", MessageType.PORT);
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeInt(MAGIC);
                    out.writeInt(VERSION);
                    out.flush();
                    socket.setSoTimeout(2 * 1000);
                    int version = input.readInt();
                    if(version != VERSION) {
                        throw new IOException("wrong protocol version");
                    }
                    socket.setSoTimeout(request.getTimeout());
                    request.request(input,out);
                    out.flush();
                    int result = input.readInt();
                    callback.onResponse(result);
                    out.writeInt(MessageType.MSG_EOF);
                    out.flush();
                }catch (IOException e) {
                    callback.onError(e);
                }finally {
                    try{
                        Log.i("remove adb forward");
                        mCurrentDevice.removeForward(MessageType.PORT,serverName,IDevice.DeviceUnixSocketNamespace.ABSTRACT);
                    }catch (Exception e) {
                        Log.e(e);
                    }
                    IOUtils.closeQuiet(socket);
                }
            }
        });
    }

    public void shutdown() {
        AndroidDebugBridge.terminate();
        mConnectPool.shutdown();
        try{
            mConnectPool.awaitTermination(10,TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
