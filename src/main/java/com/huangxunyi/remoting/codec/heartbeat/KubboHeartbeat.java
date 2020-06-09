package com.huangxunyi.remoting.codec.heartbeat;

import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.Heartbeat;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class KubboHeartbeat implements HeartbeatTrigger {
    @Override
    public void heartbeatTriggered(ChannelHandlerContext channelHandlerContext) {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setTimestamp(System.currentTimeMillis());
        heartbeat.setType(Constants.TYPE_HEARTBEAT);
        heartbeat.setCmd(Constants.CMD_ONEWAY);
        heartbeat.setMessageId(new Random().nextInt());

        channelHandlerContext.writeAndFlush(heartbeat).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.warn("  heartbeat fail ... ");
            } else {
                log.info("  heartbeat sent ... ");
            }
        });
    }
}
