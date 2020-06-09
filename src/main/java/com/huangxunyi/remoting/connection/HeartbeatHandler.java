package com.huangxunyi.remoting.connection;

import com.huangxunyi.remoting.protocol.KubboRemotingProtocol;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class HeartbeatHandler extends ChannelDuplexHandler {

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            KubboRemotingProtocol.getInstance().getHeartbeatTrigger().heartbeatTriggered(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}