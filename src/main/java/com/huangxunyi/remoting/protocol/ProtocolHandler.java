package com.huangxunyi.remoting.protocol;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

public interface ProtocolHandler {

    void handleCommand(RemotingContext ctx, Object msg) throws Exception;

    void registerProcessor(byte cmd, RemotingProcessor<?> processor);

    void registerDefaultExecutor(ExecutorService executor);

    ExecutorService getDefaultExecutor();

}
