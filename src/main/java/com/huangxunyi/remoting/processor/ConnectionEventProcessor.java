package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.connection.Connection;

/**
 * 连接事件回调
 */
public interface ConnectionEventProcessor {

    void onEvent(String remoteAddress, Connection connection);
}