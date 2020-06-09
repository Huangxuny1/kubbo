package com.huangxunyi.remoting.connection.strategy;

import com.huangxunyi.remoting.connection.Connection;

import java.util.List;

public interface ConnectionSelectStrategy {

    Connection select(List<Connection> connections);
}