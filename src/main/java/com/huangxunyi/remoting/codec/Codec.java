package com.huangxunyi.remoting.codec;

import io.netty.channel.ChannelHandler;

public interface Codec {
    /**
     * new encoder instance
     *
     * @return encoder
     */
    ChannelHandler newEncoder();

    /**
     * new decoder instance
     *
     * @return decoder
     */
    ChannelHandler newDecoder();
}
