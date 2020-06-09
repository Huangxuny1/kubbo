package proxy;

import com.huangxunyi.proxy.cglib.CGLIBProxyFactory;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;
import com.huangxunyi.rpc.KubboInvoker;
import demo.FooImpl;
import demo.IFoo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCGLIBProxyFactory {
    @Test
    public void testCglibInvoker() throws Exception {

        CGLIBProxyFactory cglibProxyFactory = new CGLIBProxyFactory();
        Invoker<IFoo> invoker = cglibProxyFactory.getInvoker(new FooImpl(), IFoo.class, "123");
        Request request = new Request();
        request.setArgTypes(new Class[]{String.class});
        request.setArgs(new Object[]{"AthosZhou"});
        request.setMethodName("say");
        request.setClassName("demo.IFoo");
        Response invoke = invoker.invoke(request);
        System.out.println(invoke);
    }

    @Test
//    @Disabled
    public void testCglibGetProxy() throws Exception {
        IFoo proxy = new CGLIBProxyFactory().getProxy(IFoo.class, new KubboInvoker<>(IFoo.class));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            System.out.println(i + " return " + proxy.say("world"));
        }
        long end = System.currentTimeMillis() - start;
        System.out.println("cost " + end);
        System.out.println("QPS :  "+ (100000/(end/1000f)) +" /s");
        new CountDownLatch(1).await();
//        new CountDownLatch(1).await(10, TimeUnit.SECONDS);

    }

}
