package com.huangxunyi.rpc;

public interface Exporter<T> {

    Invoker<T> getInvoker();

    void unexport();

}