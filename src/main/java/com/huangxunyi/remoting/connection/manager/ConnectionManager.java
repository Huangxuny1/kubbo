package com.huangxunyi.remoting.connection.manager;

import com.huangxunyi.remoting.LifeCycle;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.Url;

import java.util.List;

public interface ConnectionManager extends LifeCycle {
    void add(String poolKey, Connection connection);

    Connection get(String poolKey);

    List<Connection> getAll(String poolKey);

    void remove(Connection connection);

    void remove(String poolKey);

    Connection create(Url url) throws RuntimeException;

    Connection getAndCreateIfAbsent(Url url) throws RuntimeException;

}
