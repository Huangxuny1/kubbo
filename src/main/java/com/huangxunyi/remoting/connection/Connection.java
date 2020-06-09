package com.huangxunyi.remoting.connection;

import com.huangxunyi.remoting.message.InvokeFuture;
import com.huangxunyi.utils.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Connection {
    public static final AttributeKey<Connection> CONNECTION = AttributeKey
            .valueOf("connection");

    private Channel channel;

    private AtomicBoolean closed = new AtomicBoolean(false);

    private final ConcurrentHashMap<Integer, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<Integer, InvokeFuture>(
            4);

    public Connection(Channel channel) {
        this.channel = channel;
    }

    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                if (this.getChannel() != null) {
                    this.getChannel().close().addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (log.isInfoEnabled()) {
                                log.info("Close the connection to remote address={}, result={}, cause={}",
                                        RemotingUtil.parseRemoteAddress(Connection.this.getChannel()), future.isSuccess(), future.cause());
                            }
                        }

                    });
                }
            } catch (Exception e) {
                log.warn("Exception caught when closing connection {}", RemotingUtil.parseRemoteAddress(Connection.this.getChannel()), e);
            }
        }
    }


    public Url getUrl() {
        return new Url("localhost", 10087);
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isFine() {
        return this.channel != null && this.channel.isActive();
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    public InvokeFuture addInvokeFuture(InvokeFuture future) {
        return this.invokeFutureMap.putIfAbsent(future.invokeId(), future);
    }

    public InvokeFuture removeInvokeFuture(int id) {
        return this.invokeFutureMap.remove(id);
    }
}
