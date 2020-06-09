package com.huangxunyi.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunStateRecordedFutureTask<V> extends FutureTask<V> {

    private AtomicBoolean hasRun = new AtomicBoolean();

    public RunStateRecordedFutureTask(Callable<V> callable) {
        super(callable);
    }

    @Override
    public void run() {
        this.hasRun.set(true);
        super.run();
    }

    public V getAfterRun() throws InterruptedException, ExecutionException {

        if (!hasRun.get()) {
            throw new RuntimeException();
        }

        if (!isDone()) {
            throw new RuntimeException();
        }

        return super.get();
    }
}