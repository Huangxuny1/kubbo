package com.huangxunyi.remoting;

import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.processor.UserProcessor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class RemotingContext {
    private ChannelHandlerContext channelContext;
    private ConcurrentHashMap<String, UserProcessor<?>> userProcessors;

    private boolean serverSide = false;

    private boolean timeoutDiscard = true;
    private long arriveTimestamp;
    private int timeout;
    private InvokeContext invokeContext;

    public RemotingContext(ChannelHandlerContext ctx) {
        this.channelContext = ctx;
    }

    public RemotingContext(ChannelHandlerContext ctx, InvokeContext invokeContext,
                           boolean serverSide,
                           ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        this.channelContext = ctx;
        this.invokeContext = invokeContext;
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
    }

    public ChannelFuture writeAndFlush(KubboMessage msg) {
        return this.channelContext.writeAndFlush(msg);
    }

    public boolean isServerSide() {
        return this.serverSide;
    }

    public UserProcessor<?> getUserProcessor(String className) {
        return className.isBlank() ? null : this.userProcessors.get(className);
    }

    public ChannelHandlerContext getChannelContext() {
        return channelContext;
    }

    public InvokeContext getInvokeContext() {
        return invokeContext;
    }

    public void setArriveTimestamp(long arriveTimestamp) {
        this.arriveTimestamp = arriveTimestamp;
    }

    public long getArriveTimestamp() {
        return arriveTimestamp;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isTimeoutDiscard() {
        return timeoutDiscard;
    }

    public RemotingContext setTimeoutDiscard(boolean failFastEnabled) {
        this.timeoutDiscard = failFastEnabled;
        return this;
    }
}
