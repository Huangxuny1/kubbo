package utils;

import com.huangxunyi.remoting.codec.Codec;
import com.huangxunyi.remoting.codec.DefaultCodec;
import com.huangxunyi.utils.CrcUtil;
import com.huangxunyi.utils.RemotingUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestUtils {


    @Test
    public void testCRC() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future<Integer> submit = executorService.submit(() -> CrcUtil.crc32("hello,kubbo".getBytes()));
            futureList.add(submit);
        }

        int except = CrcUtil.crc32("hello,kubbo".getBytes());
        for (Future<Integer> future : futureList) {
            Assertions.assertEquals(except, future.get());
        }
    }


    // RemotingUtil
    @Test
    public void testParseSocketAddressToString() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        new Thread(() -> {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        String s = RemotingUtil.parseSocketAddressToString(serverSocket.getLocalSocketAddress());
        Assertions.assertNotNull(s);

        String[] host = s.split(":");
        Socket socket = new Socket(host[0], Integer.parseInt(host[1]));
        String s1 = RemotingUtil.parseSocketAddressToString(socket.getLocalSocketAddress());
        Assertions.assertNotNull(s1);
        String s2 = RemotingUtil.parseSocketAddressToString(socket.getRemoteSocketAddress());
        Assertions.assertNotNull(s2);
    }

    @Test
    public void TestParseAddressWithChannel() {
        EmbeddedChannel channel = new EmbeddedChannel();
        String s = RemotingUtil.parseLocalAddress(channel);
        Assertions.assertNotNull(s);
        String s1 = RemotingUtil.parseRemoteAddress(channel);
        Assertions.assertNotNull(s1);
    }
}
