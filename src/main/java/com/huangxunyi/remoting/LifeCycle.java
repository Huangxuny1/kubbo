package com.huangxunyi.remoting;

public interface LifeCycle {

    void start() throws RuntimeException;

    void shutdown() throws RuntimeException;

    public boolean isStarted();
}
