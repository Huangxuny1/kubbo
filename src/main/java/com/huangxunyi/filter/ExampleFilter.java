package com.huangxunyi.filter;

import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;

public class ExampleFilter implements Filter {

    @Override
    public Response filter(Invoker<?> invoker, Request req) throws Exception{
        if (req == null) {
            throw new IllegalArgumentException("req  not be null");
        }
        System.out.println("ExampleFilter ");
        return invoker.invoke(req);
    }
}