package com.huangxunyi.remoting.codec;

import com.huangxunyi.remoting.codec.decoder.MessageDecoder;
import com.huangxunyi.remoting.codec.encoder.MessageEncoder;
import io.netty.channel.ChannelHandler;

public class DefaultCodec implements Codec {
    @Override
    public ChannelHandler newEncoder() {
        return new MessageEncoder();
    }

    @Override
    public ChannelHandler newDecoder() {
        return new MessageDecoder();
    }
}
