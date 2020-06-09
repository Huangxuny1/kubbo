package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.message.Heartbeat;
import com.huangxunyi.remoting.message.InvokeFuture;
import com.huangxunyi.remoting.message.MessageFactory;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.utils.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatProcessor extends AbstractRemotingProcessor<Heartbeat> {

    public HeartbeatProcessor(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public void doProcess(RemotingContext ctx, Heartbeat msg) throws Exception {
        log.warn(msg.toString());
    }
}
