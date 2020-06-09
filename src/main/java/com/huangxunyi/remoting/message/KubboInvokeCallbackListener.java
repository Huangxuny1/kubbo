package com.huangxunyi.remoting.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubboInvokeCallbackListener implements InvokeCallbackListener {

    private String address;

    public KubboInvokeCallbackListener(String address) {
        this.address = address;
    }

    @Override
    public void onResponse(InvokeFuture future) {
        log.warn(" todo " + future);
        new Throwable().printStackTrace();
    }

    @Override
    public String getRemoteAddress() {

        log.warn(" todo ");
        new Throwable().printStackTrace();
        return null;
    }
}
