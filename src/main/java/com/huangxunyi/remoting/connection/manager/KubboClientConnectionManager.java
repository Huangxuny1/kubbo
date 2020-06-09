package com.huangxunyi.remoting.connection.manager;

import com.huangxunyi.remoting.client.ConnectionFactory;
import com.huangxunyi.remoting.connection.ConnectionEventHandler;
import com.huangxunyi.remoting.connection.ConnectionEventListener;
import com.huangxunyi.remoting.connection.strategy.ConnectionSelectStrategy;

public class KubboClientConnectionManager extends DefaultConnectionManager {

    public KubboClientConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                        ConnectionFactory connectionFactory,
                                        ConnectionEventHandler connectionEventHandler,
                                        ConnectionEventListener connectionEventListener) {
        super(connectionSelectStrategy, connectionFactory, connectionEventHandler,
                connectionEventListener);
    }
}
