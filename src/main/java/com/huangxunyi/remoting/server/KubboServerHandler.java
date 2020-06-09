package com.huangxunyi.remoting.server;

import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
@ChannelHandler.Sharable
public class KubboServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive : {}", ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Request) {

        }
        log.info(" channelRead : {}", msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        log.error(" exception caught ");
        cause.printStackTrace();

    }
}
