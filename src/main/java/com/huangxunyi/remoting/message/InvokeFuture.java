package com.huangxunyi.remoting.message;

import com.huangxunyi.remoting.client.InvokeCallback;
import io.netty.util.Timeout;

public interface InvokeFuture {
    Response waitResponse(final long timeoutMillis) throws InterruptedException;

//    Response createConnectionClosedResponse(InetSocketAddress responseHost);

    void putResponse(final KubboMessage response);

    int invokeId();

    void executeInvokeCallback();

    void tryAsyncExecuteInvokeCallbackAbnormally();

    void setCause(Throwable cause);

    Throwable getCause();

    InvokeCallback getInvokeCallback();

    boolean isDone();

    ClassLoader getAppClassLoader();

//    byte getProtocolCode();

//    void setInvokeContext(InvokeContext invokeContext);

//    InvokeContext getInvokeContext();


    void addTimeout(Timeout timeout);


    void cancelTimeout();
}
