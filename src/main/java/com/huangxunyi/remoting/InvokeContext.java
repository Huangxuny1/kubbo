package com.huangxunyi.remoting;

import java.util.concurrent.ConcurrentHashMap;

public class InvokeContext {
    public final static int INITIAL_SIZE = 8;
    private ConcurrentHashMap<String, Object> context;

    public InvokeContext() {
        this.context = new ConcurrentHashMap<>(INITIAL_SIZE);
    }

    public void putIfAbsent(String key, Object value) {
        this.context.putIfAbsent(key, value);
    }

    public void put(String key, Object value) {
        this.context.put(key, value);
    }

    public <T> T get(String key) {
        return (T) this.context.get(key);
    }

    public <T> T get(String key, T defaultIfNotFound) {
        return this.context.get(key) != null ? (T) this.context.get(key) : defaultIfNotFound;
    }

    public void clear() {
        this.context.clear();
    }


}
