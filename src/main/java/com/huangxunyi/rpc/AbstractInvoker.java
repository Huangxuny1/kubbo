package com.huangxunyi.rpc;

import com.huangxunyi.filter.Filter;
import com.huangxunyi.filter.FilterChain;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractInvoker<T> implements Invoker<T> {

    private final Class<T> type;

    private final String url;

    private final Map<String, Object> attachment;


    public AbstractInvoker(Class<T> type, String url) {
        this(type, url, null);
    }

    public AbstractInvoker(Class<T> type, String url, Map<String, Object> attachment) {
        if (type == null) {
            throw new IllegalArgumentException("service type == null");
        }
        if (url == null) {
            throw new IllegalArgumentException("service url == null");
        }
        this.type = type;
        this.url = url;
        this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
    }

//    public <T> Invoker<T> buildFilterChain(List<Filter> filters) {
//        return new FilterChain<T>(filters, (Invoker<T>) this).buildFilterChain();
//    }


    @Override
    public Response invoke(Request request) throws RuntimeException {
        Response response = doInvoke(request);
        return response;
    }


    protected abstract Response doInvoke(Request request) throws RuntimeException;

    @Override
    public Class<T> getInterface() {
        return type;
    }
}
