package com.huangxunyi.remoting;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void start() throws RuntimeException {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new RuntimeException("this component has started");
    }

    @Override
    public void shutdown() throws RuntimeException {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new RuntimeException("this component has closed");
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

}