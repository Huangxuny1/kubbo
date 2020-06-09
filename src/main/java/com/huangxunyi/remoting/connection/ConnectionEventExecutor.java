package com.huangxunyi.remoting.connection;

import com.huangxunyi.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConnectionEventExecutor {
    ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            new NamedThreadFactory("kubbo-conn-event-executor", true));

    public void onEvent(Runnable runnable) {
        try {
            executor.execute(runnable);
        } catch (Throwable t) {
            log.error("Exception caught when execute connection event!", t);
        }
    }
}