package com.huangxunyi.remoting.connection;

public interface ConnectionHeartbeatManager {

    void disableHeartbeat(Connection connection);

    void enableHeartbeat(Connection connection);
}
