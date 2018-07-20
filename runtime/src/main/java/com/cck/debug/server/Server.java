package com.cck.debug.server;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import com.cck.debug.command.CommandReceiver;
import com.cck.debug.common.MessageType;
import com.cck.debug.utils.IOUtils;
import com.cck.debug.utils.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.cck.debug.common.MessageType.MAGIC;
import static com.cck.debug.common.MessageType.VERSION;

public class Server {

    private LocalServerSocket mSocket;
    private CommandReceiver mCommandReceiver;
    public static Server create(CommandReceiver receiver,String name) {
        return new Server(receiver,name);
    }

    public Server(CommandReceiver receiver,String name) {
        try{
            Log.i("serverName:"+name);
            mSocket = new LocalServerSocket(name);
            mCommandReceiver = receiver;
        }catch (IOException e) {
            Log.e(e);
            shutdown();
        }
    }

    public void startServer() {
        if(mSocket == null) {
            Log.i("can't start server,exit");
            return ;
        }
        if(mCommandReceiver == null) {
            Log.i("command receiver is null,exit");
            return ;
        }
        Thread serverThread = new Thread(new ServerRunThread());
        serverThread.start();
    }

    public void shutdown() {
        if(mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(e);
            }
            mSocket = null;
        }
    }

    public class ServerRunThread implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setName("ServerThread");
            while (true) {
                if(mSocket == null) {
                    Log.i("server socket is NULL,shutdown server");
                    break;
                }
                try{
                    LocalSocket client = mSocket.accept();
                    Thread connectionThread = new Thread(new ConnectionThread(client));
                    connectionThread.start();
                } catch (IOException e) {
                    Log.e(e);
                }
            }

        }

    }

    public class ConnectionThread implements Runnable {
        private LocalSocket mClient;
        public ConnectionThread(LocalSocket client) {
            mClient = client;
        }
        @Override
        public void run() {
            DataInputStream in = null;
            DataOutputStream out = null;
            try{
                in = new DataInputStream(mClient.getInputStream());
                out = new DataOutputStream(mClient.getOutputStream());
                handle(in,out);
            }catch (IOException e) {
                Log.e(e);
            }finally {
                IOUtils.closeQuiet(in);
                IOUtils.closeQuiet(out);
                IOUtils.closeQuiet(mClient);
            }
        }

        private void handle(final DataInputStream in,final DataOutputStream out) throws IOException{
            int magic = in.readInt();
            if(magic != MAGIC) {
                Log.i("magic number is wrong,magic:0x"+Integer.toHexString(magic));
                return ;
            }
            int version = in.readInt();
            if(version != VERSION) {
                Log.i("version not support,version:"+version);
                return ;
            }

            out.writeInt(version);

            while (true) {
                Log.e("prepare to read next msg");
                int msg = in.readInt();
                Log.i("read msg type,msg:0x"+Integer.toHexString(msg));
                if(msg == MessageType.MSG_EOF) {
                    Log.i("end of msg");
                    break;
                }
                if(mCommandReceiver == null) {
                    returnAndFlush(out,MessageType.MSG_RESULT_FAIL);
                    continue;
                }
                if(msg == MessageType.MSG_TOAST) {
                    String toast = in.readUTF();
                    Log.i("show toast:"+toast);
                    returnAndFlush(out,mCommandReceiver.handleToast(toast));
                }else if(msg == MessageType.MSG_PING){
                    returnAndFlush(out,mCommandReceiver.handlePing());
                }else if(msg == MessageType.MSG_RESTART) {
                    returnAndFlush(out,mCommandReceiver.handleRestart());
                }else if(msg == MessageType.MSG_UPDATE) {
                    String pluginName = in.readUTF();
                    int pluginVersion = in.readInt();
                    int fileLen = in.readInt();
                    byte[] fileBytes = new byte[fileLen];
                    in.readFully(fileBytes);
                    returnAndFlush(out,mCommandReceiver.handleUpdate(pluginName,pluginVersion,fileBytes,fileLen));
                }else{
                    Log.i("invalid msg type,msg:0x"+Integer.toHexString(msg)+"");
                    break;
                }
            }

        }

        private void returnAndFlush(DataOutputStream out,int result)throws IOException {
            Log.i("write response:"+result);
            out.writeInt(result);
            out.flush();
        }



    }
}
