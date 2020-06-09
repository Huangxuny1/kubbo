package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.InvokeContext;
import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.processor.UserProcessor;
import com.huangxunyi.remoting.protocol.KubboRemotingProtocol;
import com.huangxunyi.remoting.protocol.ProtocolHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class KubboChannelHandler extends ChannelInboundHandlerAdapter {

    private boolean serverSide;
    private ConcurrentHashMap<String, UserProcessor<?>> userProcessors;

    public KubboChannelHandler(ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        serverSide = false;
        this.userProcessors = userProcessors;
    }

    public KubboChannelHandler(boolean serverSide, ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolHandler protocolHandler = KubboRemotingProtocol.getInstance().getProtocolHandler();
        protocolHandler.handleCommand(
                new RemotingContext(ctx, new InvokeContext(), serverSide, userProcessors), msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
