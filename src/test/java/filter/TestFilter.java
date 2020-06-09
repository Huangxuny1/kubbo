package filter;

import com.huangxunyi.filter.FilterChain;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.rpc.Invoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.mockito.Mockito.*;

public class TestFilter {
    @Test
    public void testFiler() throws Exception {
        Request mockRequest = mock(Request.class);
        Invoker mockInvoker = mock(Invoker.class);
        Response response = mock(Response.class);

        when(mockInvoker.invoke(mockRequest)).thenReturn(response);
        when(response.toString()).thenReturn("hello");

        FilterChain filterChain = new FilterChain(mockInvoker);
        Response invoke = filterChain.buildFilterChain().invoke(mockRequest);

        Assertions.assertEquals(invoke.toString(), "hello");
    }
}
