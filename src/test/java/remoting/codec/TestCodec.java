package remoting.codec;

import com.huangxunyi.remoting.codec.Codec;
import com.huangxunyi.remoting.codec.DefaultCodec;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCodec {

    private Codec codec = new DefaultCodec();

    @Test
    @SuppressWarnings("all")
    public void testRequest() {
        EmbeddedChannel channel = new EmbeddedChannel(codec.newDecoder(), codec.newDecoder());

        for (int i = 0; i < 10; i++) {
            Request request = new Request();
            request.setArgs(new Object[]{1, "2", 3L});
            request.setArgTypes(new Class[]{Integer.class, String.class, Long.class});
            request.setClassName(TestCodec.class.getName());
            request.setCmd((byte) 1);
            request.setMethodName(TestCodec.class.getMethods()[0].getName());
            request.setMessageId(Integer.parseInt("13456" + i));
            channel.writeOutbound(request);
            channel.writeInbound(new Object[]{channel.readOutbound()});
            Request r = channel.readInbound();
            Assertions.assertEquals(r, request);
        }
    }


    @Test
    @SuppressWarnings("all")
    public void testResponse() {
        EmbeddedChannel channel = new EmbeddedChannel(codec.newDecoder(), codec.newEncoder());

        for (int i = 0; i < 10; i++) {
            Response response = new Response();
            response.setCode(200);
            response.setData("Hello,world!");
            response.setCmd((byte) 1);
            response.setMessageId(Integer.parseInt("2456" + i));
            response.setErrorMsg("msg");

            channel.writeOutbound(response);
            channel.writeInbound(new Object[]{channel.readOutbound()});
            Response r = channel.readInbound();
            Assertions.assertEquals(r, response);
        }
    }
}
