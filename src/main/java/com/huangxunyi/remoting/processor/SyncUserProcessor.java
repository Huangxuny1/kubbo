package com.huangxunyi.remoting.processor;

public abstract class SyncUserProcessor<T> extends AbstractUserProcessor<T> {

    @Override
    public abstract Object handleRequest(BizContext bizCtx, T request) throws Exception;


    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, T request) {
        throw new UnsupportedOperationException(
            "ASYNC handle request is unsupported in SyncUserProcessor!");
    }


    @Override
    public abstract String interest();
}