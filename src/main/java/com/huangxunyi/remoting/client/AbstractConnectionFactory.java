package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.codec.Codec;
import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.codec.DefaultCodec;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.ConnectionEventHandler;
import com.huangxunyi.remoting.connection.ConnectionEventType;
import com.huangxunyi.utils.NamedThreadFactory;
import com.huangxunyi.utils.NettyEventLoopUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * abstract connection factory
 * <p>
 * init netty client {@link Bootstrap}
 */
@Slf4j
public abstract class AbstractConnectionFactory implements ConnectionFactory {
    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(Runtime
                    .getRuntime().availableProcessors() + 1,
            new NamedThreadFactory("netty-client-worker", true));

    private final Codec codec;

    protected Bootstrap bootstrap;
    private final ChannelHandler handler;

    private final ChannelHandler heartbeatHandler;

    public AbstractConnectionFactory(Codec codec, ChannelHandler handler, ChannelHandler heartbeatHandler) {
        if (handler == null) {
            throw new IllegalArgumentException("null handler");
        }
        this.heartbeatHandler = heartbeatHandler;
        this.handler = handler;
        this.codec = codec;
    }


    @Override
    public void init(final ConnectionEventHandler connectionEventHandler) {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NettyEventLoopUtil.getClientSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());
                pipeline.addLast("idleStateHandler",
                        new IdleStateHandler(15000, 90000, 0,
                                TimeUnit.MILLISECONDS));
                pipeline.addLast("heartbeatHandler", heartbeatHandler);
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", handler);
            }
        });
    }

    public void close() {
        workerGroup.shutdownGracefully();
    }

    /**
     * create connection with server
     *
     * @param targetIP
     * @param targetPort
     * @param connectTimeout the minimum is 1000 (1s)
     * @return connection {@link ChannelFuture}
     * @throws Exception
     */
    @Override
    public Connection createConnection(String targetIP, int targetPort, int connectTimeout) throws Exception {
        Channel channel = doCreateConnection(targetIP, targetPort, connectTimeout);
        Connection connection = new Connection(channel);
        channel.attr(Connection.CONNECTION).set(connection);
        /**
         trreturn igger CONNECTION event

         @see com.huangxunyi.remoting.connection.ConnectionEventHandler#userEventTriggered(ChannelHandlerContext, Object)

         */
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);

        return connection;
    }

    /**
     * connection to server by {@link Bootstrap#connect(SocketAddress)}
     *
     * @param targetIP
     * @param targetPort
     * @param connectTimeout
     * @return connection {@link ChannelFuture}
     * @throws Exception when connection is unavailable
     */
    protected Channel doCreateConnection(String targetIP, int targetPort, int connectTimeout)
            throws Exception {
        // prevent unreasonable value, at least 1000
        connectTimeout = Math.max(connectTimeout, 1000);
        String address = targetIP + ":" + targetPort;

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + address + " timeout!";
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + address + " cancelled by user!";
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + address + " error!";
            log.warn(errMsg);
            throw new Exception(errMsg, future.cause());
        }
        return future.channel();
    }
}
