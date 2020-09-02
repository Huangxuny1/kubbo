package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.LifeCycle;
import com.huangxunyi.remoting.client.InvokeCallback;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.message.ResponseFuture;

public interface KubboClient extends LifeCycle {

    Response invokeSync(final String addr, final Object request, final long timeoutMillis) throws RuntimeException, InterruptedException;

    ResponseFuture invokeAsync(final String addr, final Object request, final long timeoutMillis) throws RuntimeException;

    void invokeOneway(final String addr, final Object request) throws RuntimeException;

    void invokeWithCallback(final String addr, final Object request,
                            final InvokeCallback invokeCallback, final int timeoutMillis) throws RuntimeException;

}
