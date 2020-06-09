package com.huangxunyi.filter;

import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;

public class FilterWrapper<T> implements Invoker<T> {

    private Filter next;

    private Invoker<T> invoker;

    public FilterWrapper(Filter next, Invoker<T> invoker) {
        this.next = next;
        this.invoker = invoker;
    }

    @Override
    public Response invoke(Request args) throws Exception {
        if (next != null) {
            try {
                return next.filter(invoker, args);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            return invoker.invoke(args);
        }
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

}