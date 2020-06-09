package com.huangxunyi.rpc;

import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {

    private final T proxy;

    private final Class<T> type;

    private final String url;

    public AbstractProxyInvoker(T proxy, Class<T> type, String url) {
        if (null == proxy) {
            throw new IllegalArgumentException(" proxy == null ");
        }
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Response invoke(Request request) throws Exception {
        Object result = doInvoke(proxy, request.getMethodName(), request.getArgTypes(), request.getArgs());
        Response response = new Response();
        response.setMessageId(request.getMessageId());
        response.setData(result);
        return response;
    }


    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws RuntimeException, NoSuchMethodException, Exception;
}
