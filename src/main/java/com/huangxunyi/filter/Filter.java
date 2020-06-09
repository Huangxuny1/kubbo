package com.huangxunyi.filter;

import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;

public interface Filter {

    /**
     * 过滤器设计
     *
     * @param invoker
     * @param request
     * @return
     */
    Response filter(Invoker<?> invoker, Request request) throws RuntimeException, Exception;

    interface Listener {

        void onResponse(Response appResponse, Invoker<?> invoker, Request request);

        void onError(Throwable t, Invoker<?> invoker, Request request);
    }
}