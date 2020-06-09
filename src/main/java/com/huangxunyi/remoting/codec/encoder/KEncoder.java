package com.huangxunyi.remoting.codec.encoder;

import com.huangxunyi.remoting.message.KubboMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface KEncoder {
    void encode(ChannelHandlerContext ctx, KubboMessage msg, ByteBuf out) throws Exception;

}
