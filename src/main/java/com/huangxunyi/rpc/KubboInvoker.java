package com.huangxunyi.rpc;

import com.huangxunyi.remoting.client.KubboClient;
import com.huangxunyi.remoting.client.KubboRpcClient;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.message.ResponseFuture;

public class KubboInvoker<T> extends AbstractInvoker<T> {


    private final KubboClient kubboClient = KubboRpcClient.getInstance();

    public KubboInvoker(Class<T> clazz) {
        //todo
        super(clazz, "123");
    }

    @Override
    protected Response doInvoke(Request request) throws RuntimeException {
        try {
            return (Response) kubboClient.invokeSync("127.0.0.1:10087", request, 3000);
//            ResponseFuture responseFuture = kubboClient.invokeAsync("127.0.0.1:10087", request, 3000);
//            return responseFuture.get(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
