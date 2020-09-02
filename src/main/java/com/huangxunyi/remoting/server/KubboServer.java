package com.huangxunyi.remoting.server;

import com.huangxunyi.remoting.AbstractLifeCycle;
import com.huangxunyi.remoting.client.KubboChannelHandler;
import com.huangxunyi.remoting.codec.Codec;
import com.huangxunyi.remoting.codec.DefaultCodec;
import com.huangxunyi.remoting.connection.ConnectionEventHandler;
import com.huangxunyi.remoting.connection.ConnectionEventListener;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.processor.UserProcessor;
import com.huangxunyi.rpc.SimpleServerUserProcessor;
import com.huangxunyi.utils.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class KubboServer extends AbstractLifeCycle {
    private static boolean epollEnabled = Epoll.isAvailable();
    private ServerBootstrap bootstrap;
    private ConnectionEventHandler connectionEventHandler;
    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();
    private ChannelFuture channelFuture;
    private final ConcurrentHashMap<String, UserProcessor<?>> userProcessors;


    private final EventLoopGroup bossGroup = epollEnabled ?
            new EpollEventLoopGroup(1, new NamedThreadFactory("Rpc-netty-server-boss")) :
            new NioEventLoopGroup(1, new NamedThreadFactory("Rpc-netty-server-boss"));

    private final EventLoopGroup workGroup = epollEnabled ?
            new EpollEventLoopGroup(Runtime
                    .getRuntime()
                    .availableProcessors() * 2, new NamedThreadFactory("Rpc-netty-server-worker")) :
            new NioEventLoopGroup(Runtime
                    .getRuntime()
                    .availableProcessors() * 2, new NamedThreadFactory("Rpc-netty-server-worker"));

    private final ChannelInboundHandlerAdapter handler;

    private final Codec codec = new DefaultCodec();

    public KubboServer() {
        this.userProcessors = new ConcurrentHashMap<>();
        this.handler = new KubboChannelHandler(userProcessors);
        this.connectionEventHandler = new ConnectionEventHandler();
        this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);

    }

    @Override
    public void start() throws RuntimeException {
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                //设置TCP缓冲区
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 设置接受数据的缓存大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                // 设置保持连接
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                // 设置发送数据的缓存大小
                .childOption(ChannelOption.SO_SNDBUF, 32 * 1024);
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decode", codec.newDecoder());
                pipeline.addLast("encode", codec.newEncoder());
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", handler);
                // createConnection(ch);
            }
        });
        try {
            this.channelFuture = this.bootstrap.bind(10087).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public void shutdown() throws RuntimeException {
        super.shutdown();

        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();

    }

    public UserProcessor<?> addUserProcessors(String interest, UserProcessor<?> userProcessor) {
        UserProcessor<?> ret = userProcessors.putIfAbsent(interest, userProcessor);
        return ret;
    }
}
