package com.huangxunyi.remoting.connection.manager;

import com.huangxunyi.remoting.AbstractLifeCycle;
import com.huangxunyi.remoting.client.ConnectionFactory;
import com.huangxunyi.remoting.connection.*;
import com.huangxunyi.remoting.connection.strategy.ConnectionSelectStrategy;
import com.huangxunyi.remoting.connection.strategy.RandomSelectStrategy;
import com.huangxunyi.utils.NamedThreadFactory;
import com.huangxunyi.utils.RunStateRecordedFutureTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 连接管理 抽象类
 */
@Slf4j
public abstract class DefaultConnectionManager extends AbstractLifeCycle implements ConnectionManager, ConnectionHeartbeatManager {

    // 异步创建连接线程池
    private ThreadPoolExecutor asyncCreateConnectionExecutor;

    // 连接工厂 真正发起连接是通过此类
    private ConnectionFactory connectionFactory;

    // 连接事件监听器
    protected ConnectionEventListener connectionEventListener;

    // 连接选择策略
    protected ConnectionSelectStrategy connectionSelectStrategy;

    // 连接事件处理器
    protected ConnectionEventHandler connectionEventHandler;

    // 连接表
    protected ConcurrentHashMap<String, RunStateRecordedFutureTask<ConnectionPool>> connTasks;

    private DefaultConnectionManager() {
        this.connTasks = new ConcurrentHashMap<>();
        this.connectionSelectStrategy = new RandomSelectStrategy();
    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                    ConnectionFactory connectionFactory,
                                    ConnectionEventHandler connectionEventHandler,
                                    ConnectionEventListener connectionEventListener) {
        this();
        this.connectionSelectStrategy = connectionSelectStrategy;
        this.connectionFactory = connectionFactory;
        this.connectionEventHandler = connectionEventHandler;
        this.connectionEventListener = connectionEventListener;

    }

    @Override
    public void add(String poolKey, Connection connection) {
        ConnectionPool pool = null;
        try {
            pool = this.getConnectionPoolAndCreateIfAbsent(poolKey, new ConnectionPoolCall());
        } catch (Exception e) {
            log.error("[NOTIFYME] Exception occurred when getOrCreateIfAbsent an empty ConnectionPool!", e);
        }
        if (pool != null) {
            pool.add(connection);
        } else {
            log.error("[NOTIFYME] Connection pool NULL!");
        }
    }

    private ConnectionPool getConnectionPoolAndCreateIfAbsent(String poolKey, Callable<ConnectionPool> callable) throws RuntimeException {

        RunStateRecordedFutureTask<ConnectionPool> initialTask;
        ConnectionPool pool = null;

        int retry = 3;

        int timesOfResultNull = 0;
        int timesOfInterrupt = 0;

        for (int i = 0; (i < retry) && (pool == null); ++i) {
            initialTask = this.connTasks.get(poolKey);
            if (null == initialTask) {
                RunStateRecordedFutureTask<ConnectionPool> newTask = new RunStateRecordedFutureTask<>(
                        callable);
                initialTask = this.connTasks.putIfAbsent(poolKey, newTask);
                if (null == initialTask) {
                    initialTask = newTask;
                    initialTask.run();
                }
            }

            try {
                pool = initialTask.get();
                if (null == pool) {
                    if (i + 1 < retry) {
                        timesOfResultNull++;
                        continue;
                    }
                    this.connTasks.remove(poolKey);
                    String errMsg = "Get future task result null for poolKey [" + poolKey + "] after [" + (timesOfResultNull + 1) + "] times try.";
                    throw new RuntimeException(errMsg);
                }
            } catch (InterruptedException e) {
                if (i + 1 < retry) {
                    timesOfInterrupt++;
                    continue;// retry if interrupted
                }
                this.connTasks.remove(poolKey);
                log.warn("Future task of poolKey {} interrupted {} times. InterruptedException thrown and stop retry.",
                        poolKey, (timesOfInterrupt + 1), e);
//                throw e;
                e.printStackTrace();
            } catch (ExecutionException e) {
                // DO NOT retry if ExecutionException occurred
                this.connTasks.remove(poolKey);

                Throwable cause = e.getCause();
//                throw (Exception) cause;
                e.printStackTrace();
//                if (cause instanceof RemotingException) {
//                    throw (RemotingException) cause;
//                } else {
//                    FutureTaskUtil.launderThrowable(cause);
//                }
            }
        }
        return pool;
    }

    private void doCreate(final Url url, final ConnectionPool pool, final String taskName,
                          final int syncCreateNumWhenNotWarmup) throws RuntimeException {
        final int actualNum = pool.size();
        final int expectNum = url.getConnNum();
        if (actualNum >= expectNum) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("actual num {}, expect num {}, task name {}", actualNum, expectNum,
                    taskName);
        }
        if (url.isConnWarmup()) {
            for (int i = actualNum; i < expectNum; ++i) {
                Connection connection = create(url);
                pool.add(connection);
            }
        } else {
            if (syncCreateNumWhenNotWarmup < 0 || syncCreateNumWhenNotWarmup > url.getConnNum()) {
                throw new IllegalArgumentException(
                        "sync create number when not warmup should be [0," + url.getConnNum() + "]");
            }
            // create connection in sync way
            if (syncCreateNumWhenNotWarmup > 0) {
                for (int i = 0; i < syncCreateNumWhenNotWarmup; ++i) {
                    Connection connection = create(url);
                    pool.add(connection);
                }
                if (syncCreateNumWhenNotWarmup >= url.getConnNum()) {
                    return;
                }
            }

            pool.markAsyncCreationStart();// mark the start of async
            try {
                this.asyncCreateConnectionExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = pool.size(); i < url.getConnNum(); ++i) {
                                Connection conn = null;
                                try {
                                    conn = create(url);
                                } catch (RuntimeException e) {
                                    log.error("Exception occurred in async create connection thread for {}, taskName {}",
                                            url.getUniqueKey(), taskName, e);
                                }
                                pool.add(conn);
                            }
                        } finally {
                            pool.markAsyncCreationDone();// mark the end of async
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
                pool.markAsyncCreationDone();// mark the end of async when reject
                throw e;
            }
        } // end of NOT warm up
    }

    @Override
    public Connection get(String poolKey) {
        ConnectionPool pool = this.getConnectionPool(this.connTasks.get(poolKey));
        return null == pool ? null : pool.get();
    }

    private ConnectionPool getConnectionPool(RunStateRecordedFutureTask<ConnectionPool> task) {
        ConnectionPool t = null;
        if (null != task) {
            try {
                t = task.getAfterRun();
            } catch (InterruptedException e) {
                log.error("Future task interrupted!", e);
            } catch (ExecutionException e) {
                log.error("Future task execute failed!", e);
            }
        }
        return t;
    }

    @Override
    public List<Connection> getAll(String poolKey) {
        ConnectionPool pool = this.getConnectionPool(this.connTasks.get(poolKey));
        return null == pool ? new ArrayList<>() : pool.getAll();
    }

    @Override
    public void remove(Connection connection) {
        throw new RuntimeException(" todo ");
    }

    @Override
    public void remove(String poolKey) {
        if (poolKey.isBlank()) {
            return;
        }

        RunStateRecordedFutureTask<ConnectionPool> task = this.connTasks.remove(poolKey);
        if (null != task) {
            ConnectionPool pool = this.getConnectionPool(task);
            if (null != pool) {
//                pool.removeAllAndTryClose();
                log.warn("Remove and close all connections in ConnectionPool of poolKey={}", poolKey);
            }
        }
    }

    @Override
    public Connection create(Url url) throws RuntimeException {
        Connection conn;
        try {
            conn = this.connectionFactory.createConnection(url.getIp(), url.getPort(), 3000);
        } catch (Exception e) {
            throw new RuntimeException("Create connection failed. The address is " + url.getOriginUrl(), e);
        }
        return conn;
    }

    @Override
    public Connection getAndCreateIfAbsent(Url url) throws RuntimeException {
        ConnectionPool pool = this.getConnectionPoolAndCreateIfAbsent(url.getUniqueKey(),
                new ConnectionPoolCall(url));
        if (null != pool) {
            return pool.get();
        } else {
            log.error("[NOTIFYME] bug detected! pool here must not be null!");
            return null;
        }
    }

    @Override
    public void disableHeartbeat(Connection connection) {

    }

    @Override
    public void enableHeartbeat(Connection connection) {

    }

    @Override
    public void start() throws RuntimeException {
        this.asyncCreateConnectionExecutor = new ThreadPoolExecutor(5, 10,
                20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
                new NamedThreadFactory("conn-warmup-executor", true));
        this.connectionEventHandler.setConnectionManager(this);
        this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);
        this.connectionFactory.init(this.connectionEventHandler);

        super.start();
    }

    @Override
    public void shutdown() throws RuntimeException {
        super.shutdown();
        connectionFactory.close();
        if (asyncCreateConnectionExecutor != null) {
            asyncCreateConnectionExecutor.shutdown();
        }

    }

    /**
     *
     */
    private class ConnectionPoolCall implements Callable<ConnectionPool> {
        private boolean whetherInitConnection;
        private Url url;

        public ConnectionPoolCall() {
            this.whetherInitConnection = false;
        }

        public ConnectionPoolCall(Url url) {
            this.whetherInitConnection = true;
            this.url = url;
        }

        @Override
        public ConnectionPool call() throws Exception {
            final ConnectionPool pool = new ConnectionPool(connectionSelectStrategy);
            if (whetherInitConnection) {
                try {
                    doCreate(this.url, pool, this.getClass().getSimpleName(), 1);
                } catch (Exception e) {
                    //todo pool.removeAllAndTryClose();
                    throw e;
                }
            }
            return pool;
        }

    }
}
