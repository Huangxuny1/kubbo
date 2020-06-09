package com.huangxunyi.remoting.codec.heartbeat;

import io.netty.channel.ChannelHandlerContext;

public interface HeartbeatTrigger {
    void heartbeatTriggered(final ChannelHandlerContext channelHandlerContext);
}