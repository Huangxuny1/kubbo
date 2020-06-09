package com.huangxunyi.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义名称的现场工厂
 * 1.设置线程名称
 * 2.设置线程优先级
 *
 * @author athos
 */
public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final boolean isDaemon;
    private final int priority;

    public NamedThreadFactory() {
        this("ThreadPool");
    }

    public NamedThreadFactory(String name) {
        this(name, false);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        this(prefix, daemon, Thread.NORM_PRIORITY);
    }

    public NamedThreadFactory(String prefix, boolean daemon, final int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        isDaemon = daemon;
        this.priority = priority;
    }


    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(isDaemon);
        if (t.getPriority() != priority) {
            t.setPriority(priority);
        }
        return t;
    }
}
