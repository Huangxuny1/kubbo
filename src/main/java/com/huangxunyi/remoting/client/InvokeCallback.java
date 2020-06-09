package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.message.Response;

public interface InvokeCallback {
    void onComplete(Response response);
    void onError(Throwable e);
}