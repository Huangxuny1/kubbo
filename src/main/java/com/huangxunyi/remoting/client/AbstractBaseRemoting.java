package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.BaseRemoting;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.manager.ConnectionManager;
import com.huangxunyi.remoting.connection.Url;
import com.huangxunyi.remoting.message.*;
import com.huangxunyi.utils.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public abstract class AbstractBaseRemoting extends BaseRemoting implements RemotingClient {

    public AbstractBaseRemoting(MessageFactory messageFactory, ConnectionManager connectionManager) {
        super(messageFactory, connectionManager);
    }

    @Override
    protected InvokeFuture createInvokeFuture(KubboMessage request) {
        return new DefaultInvokeFuture(request.getMessageId(), null, null, getMessageFactory());
    }

    @Override
    protected InvokeFuture createInvokeFuture(Connection conn, KubboMessage request, InvokeCallback invokeCallback) {
        return new DefaultInvokeFuture(request.getMessageId(), new KubboInvokeCallbackListener(
                RemotingUtil.parseRemoteAddress(conn.getChannel())), invokeCallback, this.getMessageFactory());
    }

    @Override
    public KubboMessage invokeSync(String addr, Object request, long timeoutMillis) throws RuntimeException, InterruptedException {
        String[] split = addr.split(":");
        final Connection connection = connectionManager.getAndCreateIfAbsent(new Url(split[0], Integer.parseInt(split[1])));
        if (request instanceof Request) {
            return invokeSync(connection, (KubboMessage) request, timeoutMillis);
        } else {
            KubboMessage requestMessage = getMessageFactory().createRequestCommand(request);
            return invokeSync(connection, requestMessage, timeoutMillis);
        }
    }

    @Override
    public ResponseFuture invokeAsync(String addr, Object request, long timeoutMillis) throws RuntimeException {
        String[] split = addr.split(":");
        final Connection connection = connectionManager.getAndCreateIfAbsent(new Url(split[0], Integer.parseInt(split[1])));

        KubboMessage requestMessage = getMessageFactory().createRequestCommand(request);
        InvokeFuture invokeFuture = invokeAsync(connection, (KubboMessage) request, timeoutMillis);

        return new ResponseFuture(addr, invokeFuture);
    }

    @Override
    public void invokeOneway(String addr, Object request) throws RuntimeException {
        String[] split = addr.split(":");
        final Connection connection = connectionManager.getAndCreateIfAbsent(new Url(split[0], Integer.parseInt(split[1])));


        KubboMessage requestMessage = getMessageFactory().createRequestCommand(request);
        invokeOneway(connection, requestMessage);
    }

    @Override
    public void invokeWithCallback(String addr, Object request, InvokeCallback invokeCallback, int timeoutMillis) throws RuntimeException {
        String[] split = addr.split(":");
        final Connection connection = connectionManager.getAndCreateIfAbsent(new Url(split[0], Integer.parseInt(split[1])));

        KubboMessage requestMessage = getMessageFactory().createRequestCommand(request);
        super.invokeWithCallback(connection, requestMessage, invokeCallback, timeoutMillis);
    }
}
