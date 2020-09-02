package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.connection.*;
import com.huangxunyi.remoting.connection.manager.ConnectionManager;
import com.huangxunyi.remoting.connection.manager.KubboClientConnectionManager;
import com.huangxunyi.remoting.connection.strategy.ConnectionSelectStrategy;
import com.huangxunyi.remoting.connection.strategy.RandomSelectStrategy;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.KubboMessageFactory;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.message.ResponseFuture;
import com.huangxunyi.remoting.processor.UserProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class KubboRpcClient extends AbstractKubboClient {


    private final ConcurrentHashMap<String, UserProcessor<?>> userProcessors;
    private final ConnectionEventHandler connectionEventHandler;
    private final ConnectionEventListener connectionEventListener;


    private ConnectionManager connectionManager;

    protected RemotingClient rpcRemoting;

    public KubboRpcClient() {
        this.userProcessors = new ConcurrentHashMap<>();
        this.connectionEventHandler = new ConnectionEventHandler();
        this.connectionEventListener = new ConnectionEventListener();
    }


    @Override
    public Response invokeSync(String addr, Object request, long timeoutMillis) throws RuntimeException, InterruptedException {
        return this.rpcRemoting.invokeSync(addr, request, timeoutMillis);
    }

    @Override
    public ResponseFuture invokeAsync(String addr, Object request, long timeoutMillis) throws RuntimeException {
        return this.rpcRemoting.invokeAsync(addr, request, timeoutMillis);
    }

    @Override
    public void invokeOneway(String addr, Object request) throws RuntimeException {
        this.rpcRemoting.invokeOneway(addr, request);
    }

    @Override
    public void invokeWithCallback(String addr, Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RuntimeException {
        this.rpcRemoting.invokeWithCallback(addr, request, invokeCallback, timeoutMillis);
    }

    @Override
    public void start() throws RuntimeException {
        ConnectionSelectStrategy connectionSelectStrategy = new RandomSelectStrategy();

        this.connectionManager = new KubboClientConnectionManager(connectionSelectStrategy,
                new DefaultConnectionFactory(userProcessors), connectionEventHandler, connectionEventListener);
        this.connectionManager.start();

        this.rpcRemoting = new KubboComsumer(new KubboMessageFactory(), this.connectionManager);

        super.start();
    }

    @Override
    public void shutdown() throws RuntimeException {
        super.shutdown();
        this.connectionManager.shutdown();
        log.warn("Close all connections from client side!");
    }

    private static KubboClient kubboClient = new KubboRpcClient();
    public static synchronized KubboClient getInstance() {

        if (!kubboClient.isStarted()) {
            kubboClient.start();
        }
        while (!kubboClient.isStarted()) {

        }
        return kubboClient;
    }

}
