package com.huangxunyi.remoting.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubboInvokeCallbackListener implements InvokeCallbackListener {

    private String address;

    public KubboInvokeCallbackListener(String address) {
        this.address = address;
    }

    @Override
    public void onResponse(InvokeFuture future) throws InterruptedException {
        log.warn(" todo " + future);
        future.getInvokeCallback().onComplete(future.waitResponse(3000));
    }

    @Override
    public String getRemoteAddress() {

        log.warn(" todo ");
        new Throwable().printStackTrace();
        return null;
    }
}
