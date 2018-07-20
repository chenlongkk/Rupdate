package com.cck.debug.request;

import com.cck.debug.request.impl.ToastRequest;
import com.cck.debug.utils.Log;

public class Test {
    public static void main(String[] args) {
//        DeviceClient.INSTANCE.init();
//        DeviceClient.INSTANCE.connect(ToastRequest.obtain("hello"), "com.lightsky.video_plugin", new ResponseCallback() {
//            @Override
//            public void onResponse(int result) {
//                Log.i(result+"");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(e);
//            }
//        });
//        DeviceClient.INSTANCE.shutdown();
        String s = "com.lightsky.video.home101010010ak";
        int i = s.hashCode()-88;
        System.out.println(i);
    }
}
