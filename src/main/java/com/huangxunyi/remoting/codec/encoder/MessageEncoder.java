package com.huangxunyi.remoting.codec.encoder;

import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.protocol.KubboRemotingProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<KubboMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, KubboMessage msg, ByteBuf out) throws Exception {
        KubboRemotingProtocol.getInstance().getEncoder().encode(ctx, msg, out);
    }
}
