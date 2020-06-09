package com.huangxunyi.remoting.client;

import com.huangxunyi.remoting.connection.manager.ConnectionManager;
import com.huangxunyi.remoting.message.MessageFactory;

public class KubboComsumer extends AbstractBaseRemoting {
    public KubboComsumer(MessageFactory messageFactory , ConnectionManager connectionManager) {
        super(messageFactory,connectionManager);
    }


}
