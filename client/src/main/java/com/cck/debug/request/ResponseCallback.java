package com.cck.debug.request;

public interface ResponseCallback {
    void onResponse(int result);
    void onError(Throwable e);
}
