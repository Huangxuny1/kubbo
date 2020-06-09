package com.huangxunyi.proxy;

import com.huangxunyi.rpc.Invoker;

public interface ProxyFactory {
    <T> T getProxy(Class<T> clazz, Invoker<T> proxyInvoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type,  String url) throws Exception;

}
