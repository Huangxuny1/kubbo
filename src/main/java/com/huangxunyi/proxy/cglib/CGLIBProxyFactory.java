package com.huangxunyi.proxy.cglib;

import com.huangxunyi.proxy.AbstractProxyFactory;
import com.huangxunyi.proxy.KubboInterceptor;
import com.huangxunyi.rpc.AbstractProxyInvoker;
import com.huangxunyi.rpc.Invoker;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;

public class CGLIBProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> T getProxy(Class<T> interfaceType, Invoker<T> invoker) {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new KubboInterceptor(invoker));
        enhancer.setInterfaces(new Class[]{interfaceType});
        Object result = enhancer.create();
        return interfaceType.cast(result);
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, String url) throws Exception {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Exception{
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }
}
