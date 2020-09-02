package brenchmark;

import com.huangxunyi.proxy.cglib.CGLIBProxyFactory;
import com.huangxunyi.remoting.client.KubboRpcClient;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.server.KubboServer;
import com.huangxunyi.rpc.KubboInvoker;
import com.huangxunyi.rpc.SimpleServerUserProcessor;
import demo.IFoo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestQPSJUnit {

    private static KubboServer server;

    @BeforeAll
    public static void setUp() {
        server = new KubboServer();
        server.addUserProcessors(Request.class.getName(), new SimpleServerUserProcessor(3000));
        server.start();

        KubboRpcClient.getInstance();
    }

    @Test
    public void test() {
        IFoo proxy = new CGLIBProxyFactory().getProxy(IFoo.class, new KubboInvoker<>(IFoo.class));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            proxy.say("world");
//            System.out.println(i + " return " + proxy.say("world"));
        }
        long end = System.currentTimeMillis() - start;
        System.out.println("cost " + end);
        System.out.println("QPS :  " + (100000 / (end / 1000f)) + " /s");
    }


    @AfterAll
    public static void tearDown() {
        server.shutdown();
    }
}
