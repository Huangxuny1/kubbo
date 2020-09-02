package com.huangxunyi.remoting.message;

import com.huangxunyi.remoting.client.InvokeCallback;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DefaultInvokeFuture implements InvokeFuture {

    private int invokeId;

    private InvokeCallbackListener callbackListener;

    private InvokeCallback callback;

    private volatile KubboMessage response;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private Timeout timeout;

    private Throwable cause;

    private ClassLoader classLoader;


//    private InvokeContext invokeContext;

    private MessageFactory messageFactory;

    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, /*byte protocol, */MessageFactory messageFactory) {
        this.invokeId = invokeId;
        this.callbackListener = callbackListener;
        this.callback = callback;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.messageFactory = messageFactory;
    }

    /**
     * 等待 Response
     *
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    @Override
    public Response waitResponse(long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return (Response) response;
    }

    /**
     * 将Response放入
     *
     * @param response
     */
    @Override
    public void putResponse(KubboMessage response) {
        this.response = response;
        countDownLatch.countDown();
    }


    @Override
    public boolean isDone() {
        return this.countDownLatch.getCount() <= 0;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return this.classLoader;
    }

    @Override
    public void addTimeout(Timeout timeout) {

    }

    @Override
    public void cancelTimeout() {

    }

    @Override
    public int invokeId() {
        return this.invokeId;
    }

    @Override
    public void executeInvokeCallback() {
        if (callbackListener != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                try {
                    callbackListener.onResponse(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void tryAsyncExecuteInvokeCallbackAbnormally() {

    }

    @Override
    public InvokeCallback getInvokeCallback() {
        return this.callback;
    }


    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

//    @Override
//    public byte getProtocolCode() {
//        return this.protocol;
//    }

//    @Override
//    public void setInvokeContext(InvokeContext invokeContext) {
//        this.invokeContext = invokeContext;
//    }

//    @Override
//    public InvokeContext getInvokeContext() {
//        return invokeContext;
//    }


}
