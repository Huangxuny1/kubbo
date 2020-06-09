package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.message.KubboMessage;

import java.util.concurrent.ExecutorService;

/**
 * 
 * @param <T>
 */
public interface RemotingProcessor<T extends KubboMessage> {

    void process(RemotingContext ctx, T msg, ExecutorService defaultExecutor) throws Exception;

    ExecutorService getExecutor();

    void setExecutor(ExecutorService executor);

}
