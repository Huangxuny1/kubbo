package remoting;

import com.huangxunyi.remoting.client.InvokeCallback;
import com.huangxunyi.remoting.client.KubboClient;
import com.huangxunyi.remoting.client.KubboRpcClient;
import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.message.ResponseFuture;
import com.huangxunyi.remoting.server.KubboServer;
import com.huangxunyi.rpc.SimpleServerUserProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RemotingTest {

    private EchoUserProcessor userProcessor = new EchoUserProcessor();

    @BeforeEach
    public void setUp() {
        KubboServer kubboServer = new KubboServer();
        kubboServer.addUserProcessors(Request.class.getName(), userProcessor);
        kubboServer.start();

        KubboClient instance = KubboRpcClient.getInstance();
    }

    @Test
    public void testOneway() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            final Request request = new Request();
            request.setCmd(Constants.CMD_ONEWAY);
            request.setMessageId(new Random().nextInt() + 1);
            request.setClassName("" + i);

            KubboRpcClient.getInstance().invokeOneway("localhost:10087", request);
            System.out.println("r" + request);
        }
        Thread.sleep(1000);
        Assertions.assertEquals(userProcessor.gethandlers(), 10);
    }

    @Test
    public void testSync() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            final Request request = new Request();
            request.setCmd(Constants.CMD_SYNC);
            request.setMessageId(new Random().nextInt() + 1);
            request.setClassName("" + i);

            Response response = KubboRpcClient.getInstance().invokeSync("localhost:10087", request, 3000);

            Assertions.assertEquals(response.getData(), "" + i);
        }

    }

    @Test
    public void testAsync() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            final Request request = new Request();
            request.setCmd(Constants.CMD_ASYNC);
            request.setMessageId(new Random().nextInt() + 1);
            request.setClassName("" + i);

            ResponseFuture future = KubboRpcClient.getInstance().invokeAsync("localhost:10087", request, 3000);

            Assertions.assertEquals(future.get(3000).getData(), "" + i);
        }
    }

    @Test
    public void testCallback() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            final Request request = new Request();
            request.setCmd(Constants.CMD_ASYNC);
            request.setMessageId(new Random().nextInt() + 1);
            request.setClassName("" + i);

            KubboRpcClient.getInstance().invokeWithCallback("localhost:10087", request, new InvokeCallback() {
                @Override
                public void onComplete(Response response) {
                    countDownLatch.countDown();
                    System.out.println(response);
                }

                @Override
                public void onError(Throwable e) {
                    countDownLatch.countDown();
                    e.printStackTrace();
                }
            }, 3000);
        }

        countDownLatch.await(50, TimeUnit.SECONDS);
    }


}
