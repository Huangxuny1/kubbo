package com.huangxunyi.remoting.connection;

import com.huangxunyi.remoting.connection.manager.ConnectionManager;
import com.huangxunyi.utils.RemotingUtil;
import io.netty.channel.*;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@ChannelHandler.Sharable
@Slf4j
public class ConnectionEventHandler extends ChannelDuplexHandler {

    private ConnectionManager connectionManager;

    private ConnectionEventListener eventListener;

    private ConnectionEventExecutor eventExecutor;

    public ConnectionEventHandler() {

    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (log.isInfoEnabled()) {
            final String local = localAddress == null ? null : RemotingUtil
                    .parseSocketAddressToString(localAddress);
            final String remote = remoteAddress == null ? "UNKNOWN" : RemotingUtil
                    .parseSocketAddressToString(remoteAddress);
            if (local == null) {
                if (log.isInfoEnabled()) {
                    log.info("Try connect to {}", remote);
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Try connect from {} to {}", local, remote);
                }
            }
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Connection disconnect to {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        }
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Connection closed: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        }
        final Connection conn = ctx.channel().attr(Connection.CONNECTION).get();
        if (conn != null) {
//            conn.onClose();
        }
        super.close(ctx, promise);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Connection channel registered: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Connection channel unregistered: {}",
                    RemotingUtil.parseRemoteAddress(ctx.channel()));
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Connection channel active: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        }
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        if (log.isInfoEnabled()) {

            log.info("Connection channel inactive: {}", remoteAddress);
        }
        super.channelInactive(ctx);
        Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
//        todo re connect
//        if (null != attr) {
//            // add reconnect task
//            if (this.globalSwitch != null
//                    && this.globalSwitch.isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
//                Connection conn = (Connection) attr.get();
//                if (reconnectManager != null) {
//                    reconnectManager.reconnect(conn.getUrl());
//                }
//            }
        // trigger close connection event
        onEvent(attr.get(), remoteAddress, ConnectionEventType.CLOSE);
//        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof ConnectionEventType) {
            switch ((ConnectionEventType) event) {
                case CONNECT:
                    Channel channel = ctx.channel();
                    if (null != channel) {
                        Connection connection = channel.attr(Connection.CONNECTION).get();
                        this.onEvent(connection, connection.getUrl().getOriginUrl(),
                                ConnectionEventType.CONNECT);
                    } else {
                        log.warn("channel null when handle user triggered event in ConnectionEventHandler!");
                    }
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, event);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        final String localAddress = RemotingUtil.parseLocalAddress(ctx.channel());
        log.warn("ExceptionCaught in connection: local[{}], remote[{}], close the connection! Cause[{}:{}]",
                localAddress, remoteAddress, cause.getClass().getSimpleName(), cause.getMessage());
        ctx.channel().close();
    }

    private void onEvent(final Connection conn, final String remoteAddress,
                         final ConnectionEventType type) {
        if (this.eventListener != null) {
            this.eventExecutor.onEvent(() -> ConnectionEventHandler.this.eventListener.onEvent(type, remoteAddress, conn));
        }
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setConnectionEventListener(ConnectionEventListener listener) {
        if (listener != null) {
            this.eventListener = listener;
            if (this.eventExecutor == null) {
                this.eventExecutor = new ConnectionEventExecutor();
            }
        }
    }

}
