package invoker;

import com.huangxunyi.filter.LoadFilters;
import com.huangxunyi.proxy.cglib.CGLIBProxyFactory;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.KubboInvoker;
import demo.IFoo;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestInvoker {

    @Test
    public void testInvoker() {
        KubboInvoker<IFoo> objectKubboInvoker = spy(new KubboInvoker<>(IFoo.class));

        Response response = mock(Response.class);
        when(response.getData()).thenReturn("hello");
        doReturn(response).when(objectKubboInvoker).invoke(any(Request.class));
//        Response invoke = objectKubboInvoker.invoke(request);
        IFoo proxy = new CGLIBProxyFactory().getProxy(IFoo.class, objectKubboInvoker);
        System.out.println(proxy.say("heihei"));
    }
}
