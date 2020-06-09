package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.ConnectionEventHandler;
import io.netty.channel.Channel;

public interface ConnectionFactory {

    void init(final ConnectionEventHandler connectionEventHandler);

    void close();

    Connection createConnection(String targetIP, int targetPort, int connectTimeout)
            throws Exception;
}