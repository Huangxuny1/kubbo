package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.codec.DefaultCodec;
import com.huangxunyi.remoting.connection.HeartbeatHandler;
import com.huangxunyi.remoting.processor.UserProcessor;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultConnectionFactory extends AbstractConnectionFactory {
    public DefaultConnectionFactory() {
        super(new DefaultCodec(), new KubboChannelHandler(new ConcurrentHashMap<>()), new HeartbeatHandler());
    }

    public DefaultConnectionFactory(ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        super(new DefaultCodec(), new KubboChannelHandler(userProcessors), new HeartbeatHandler());
    }
}
