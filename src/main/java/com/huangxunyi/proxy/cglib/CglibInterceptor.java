package com.huangxunyi.proxy.cglib;

import com.huangxunyi.rpc.AbstractProxyInvoker;
import com.huangxunyi.rpc.Invoker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public abstract class CglibInterceptor implements MethodInterceptor {

    protected final Invoker<?> invoker;

    public CglibInterceptor(Invoker<?> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxy.invoke(invoker, args);
    }

    public Invoker<?> getInvoker() {
        return invoker;
    }
}
