package com.huangxunyi.remoting.codec.decoder;

import com.huangxunyi.remoting.protocol.KubboRemotingProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        KubboRemotingProtocol.getInstance().getDecoder().decode(ctx, in, out);
    }
}
