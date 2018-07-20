package com.cck.debug.test;


import com.cck.debug.command.CommandReceiver;
import com.cck.debug.common.MessageType;
import com.cck.debug.server.Server;
import com.cck.debug.utils.IOUtils;

import java.io.File;

public class Main {
    public static void main(String[] args){
//        Server.create(new CommandReceiver() {
//            @Override
//            public int handlePing() {
//                print("ping!");
//                return MessageType.MSG_RESULT_SUCCESS;
//            }
//
//            @Override
//            public int handleToast(String msg) {
//                print(msg);
//                return MessageType.MSG_RESULT_SUCCESS;
//            }
//
//            @Override
//            public int handleRestart() {
//                print("restart");
//                return MessageType.MSG_RESULT_SUCCESS;
//            }
//
//            @Override
//            public int handleUpdate(String pluginName, int pluginVersion, byte[] data, int len) {
//                print("plugin:"+pluginName+",v:"+pluginVersion);
//                IOUtils.copy(data,new File("update.apk"));
//                return MessageType.MSG_RESULT_SUCCESS;
//            }
//            //            @Override
////            public int handleUpdate(InputStream in) {
////                print("update");
////                return MessageType.MSG_RESULT_SUCCESS;
////            }
//        }).startServer();
    }

    public static void print(String msg) {
        System.out.println("command:"+msg);
    }
}
