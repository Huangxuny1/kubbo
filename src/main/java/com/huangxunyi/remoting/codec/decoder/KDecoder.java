package com.huangxunyi.remoting.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface KDecoder {
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
}
