package com.huangxunyi.rpc;

import java.util.Collections;
import java.util.List;

public interface Protocol {

    int getDefaultPort();

    <T> Exporter<T> export(Invoker<T> invoker) throws RuntimeException;

    <T> Invoker<T> refer(Class<T> type, String url) throws RuntimeException;

    void destroy();

    default List<String> getServers() {
        return Collections.emptyList();
    }

}