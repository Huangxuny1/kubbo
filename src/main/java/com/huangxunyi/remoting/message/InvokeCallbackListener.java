package com.huangxunyi.remoting.message;

public interface InvokeCallbackListener {

    void onResponse(final InvokeFuture future) throws InterruptedException;

    String getRemoteAddress();

}