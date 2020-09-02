package com.huangxunyi.remoting;

import com.huangxunyi.remoting.client.InvokeCallback;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.manager.ConnectionManager;
import com.huangxunyi.remoting.message.*;
import com.huangxunyi.utils.RemotingUtil;
import com.huangxunyi.utils.TimerHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public abstract class BaseRemoting {

    protected MessageFactory messageFactory;
    protected ConnectionManager connectionManager;

    public BaseRemoting(MessageFactory messageFactory, ConnectionManager connectionManager) {
        this.messageFactory = messageFactory;
        this.connectionManager = connectionManager;
    }

    protected Response invokeSync(final Connection connection, final KubboMessage request,
                                  final long timeoutMillis) throws InterruptedException {
        final InvokeFuture invokeFuture = createInvokeFuture(request);
        connection.addInvokeFuture(invokeFuture);
        final int requestId = request.getMessageId();
        try {
            connection.getChannel().writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        connection.removeInvokeFuture(requestId);
                        invokeFuture.putResponse(messageFactory.createSendFailedResponse(
                                connection.getRemoteAddress(), future.cause()));
                        log.error("Invoke send failed, id={}", requestId, future.cause());
                    }
                }
            });
        } catch (Exception e) {
            connection.removeInvokeFuture(requestId);
            invokeFuture.putResponse(messageFactory.createSendFailedResponse(connection.getRemoteAddress(), e));
            log.error("Exception caught when sending invocation, id={}", requestId, e);
        }
        Response response = invokeFuture.waitResponse(timeoutMillis);
        if (response == null) {
            connection.removeInvokeFuture(requestId);
            response = this.messageFactory.createTimeoutResponse(connection.getRemoteAddress());
            log.warn("Wait response, request id={} timeout!", requestId);
        }
        return response;
    }

    protected InvokeFuture invokeAsync(final Connection connection, final KubboMessage request,
                                       final long timeoutMillis) {
        final InvokeFuture future = createInvokeFuture(request);
        connection.addInvokeFuture(future);
        final int requestId = request.getMessageId();
        log.warn(" async timeoutMillis" + timeoutMillis);
        try {
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    InvokeFuture future = connection.removeInvokeFuture(requestId);
                    if (future != null) {
                        future.putResponse(messageFactory.createTimeoutResponse(connection.getRemoteAddress()));
                    }
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            connection.getChannel().writeAndFlush(request).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (!cf.isSuccess()) {
                        InvokeFuture f = connection.removeInvokeFuture(requestId);
                        if (f != null) {
                            f.cancelTimeout();
                            f.putResponse(messageFactory.createSendFailedResponse(
                                    connection.getRemoteAddress(), cf.cause()));
                        }
                        log.error("Invoke send failed. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), cf.cause());
                    }
                }

            });
        } catch (Exception e) {
            InvokeFuture f = connection.removeInvokeFuture(requestId);
            if (f != null) {
                f.cancelTimeout();
                f.putResponse(messageFactory.createSendFailedResponse(connection.getRemoteAddress(), e));
            }
            log.error("Exception caught when sending invocation. The address is {}",
                    RemotingUtil.parseRemoteAddress(connection.getChannel()), e);
        }
        return future;

    }

    protected void invokeOneway(final Connection connection, final KubboMessage request) {
        try {
            connection.getChannel().writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        log.error("Invoke send failed. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), future.cause());
                    }
                }
            });
        } catch (Exception e) {
            if (null == connection) {
                log.error("Conn is null");
            } else {
                log.error("Exception caught when sending invocation. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), e);
            }
        }
    }

    public void invokeWithCallback(final Connection connection, KubboMessage request, InvokeCallback invokeCallback, int timeoutMillis) throws RuntimeException {
        final InvokeFuture future = createInvokeFuture(connection, request, invokeCallback);
        connection.addInvokeFuture(future);
        final int requestId = request.getMessageId();
        try {
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    InvokeFuture future = connection.removeInvokeFuture(requestId);
                    if (future != null) {
                        future.putResponse(messageFactory.createTimeoutResponse(connection.getRemoteAddress()));
                        future.tryAsyncExecuteInvokeCallbackAbnormally();
                    }
                }

            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);
            connection.getChannel().writeAndFlush(request).addListener((ChannelFutureListener) cf -> {
                if (!cf.isSuccess()) {
                    InvokeFuture f = connection.removeInvokeFuture(requestId);
                    if (f != null) {
                        f.cancelTimeout();
                        f.putResponse(messageFactory.createSendFailedResponse(
                                connection.getRemoteAddress(), cf.cause()));
                        f.tryAsyncExecuteInvokeCallbackAbnormally();
                    }
                    log.error("Invoke send failed. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), cf.cause());
                }
            });

        } catch (Exception e) {
            InvokeFuture f = connection.removeInvokeFuture(requestId);
            if (f != null) {
                f.cancelTimeout();
                f.putResponse(messageFactory.createSendFailedResponse(connection.getRemoteAddress(), e));
                f.tryAsyncExecuteInvokeCallbackAbnormally();
            }
            log.error("Exception caught when sending invocation. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), e);
        }
    }

    protected abstract InvokeFuture createInvokeFuture(final KubboMessage request/*, final InvokeContext invokeContext*/);

    protected abstract InvokeFuture createInvokeFuture(final Connection conn,
                                                       final KubboMessage request,
                                                       final InvokeCallback invokeCallback);

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }
}
