package com.huangxunyi.proxy;

import com.huangxunyi.proxy.cglib.CglibInterceptor;
import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class KubboInterceptor extends CglibInterceptor {

    public KubboInterceptor(Invoker<?> invoker) {
        super(invoker);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Request request = new Request();
        request.setMessageId(new Random().nextInt());
        request.setMethodName(method.getName());
        request.setClassName(getInvoker().getInterface().getName());
        request.setArgs(args);
        request.setArgTypes(Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
        request.setCmd(Constants.CMD_SYNC);
        Response invoke = getInvoker().invoke(request);

        return invoke.getData();
    }
}
