package com.huangxunyi.remoting.connection;

import com.huangxunyi.remoting.connection.strategy.ConnectionSelectStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ConnectionPool {
    private CopyOnWriteArrayList<Connection> connections;
    private ConnectionSelectStrategy strategy;
    private volatile long lastAccessTimestamp;
    private volatile boolean asyncCreationDone;

    public ConnectionPool(ConnectionSelectStrategy strategy) {
        this.strategy = strategy;
        this.connections = new CopyOnWriteArrayList<Connection>();
        this.asyncCreationDone = true;
    }

    public void add(Connection connection) {
        markAccess();
        if (null == connection) {
            return;
        }
        boolean res = connections.addIfAbsent(connection);
        if (res) {
            //connection.increaseRef();
        }
    }

    public void removeAndTryClose(Connection connection) {
        if (null == connection) {
            return;
        }
        boolean res = connections.remove(connection);
        if (res) {
            // connection.decreaseRef();
        }
//        if (connection.noRef()) {
//            connection.close();
//        }
    }

    public Connection get() {
        markAccess();
        if (null != connections) {
            List<Connection> snapshot = new ArrayList<Connection>(connections);
            if (snapshot.size() > 0) {
                return strategy.select(snapshot);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Connection> getAll() {
        markAccess();
        return new ArrayList<Connection>(connections);
    }

    public boolean contains(Connection connection) {
        return connections.contains(connection);
    }

    public int size() {
        return connections.size();
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }

    public long getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    private void markAccess() {
        lastAccessTimestamp = System.currentTimeMillis();
    }

    public boolean isAsyncCreationDone() {
        return asyncCreationDone;
    }

    public void markAsyncCreationDone() {
        asyncCreationDone = true;
    }

    public void markAsyncCreationStart() {
        asyncCreationDone = false;
    }

}
