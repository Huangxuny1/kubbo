package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.MessageFactory;
import com.huangxunyi.utils.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class AbstractRemotingProcessor<T extends KubboMessage> implements RemotingProcessor<T> {

    private ExecutorService executor;
    private MessageFactory messageFactory;

    public AbstractRemotingProcessor(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public abstract void doProcess(RemotingContext ctx, T msg) throws Exception;

    @Override
    public void process(RemotingContext ctx, T msg, ExecutorService defaultExecutor)
            throws Exception {
        ProcessTask task = new ProcessTask(ctx, msg);
        if (this.getExecutor() != null) {
            this.getExecutor().execute(task);
        } else {
            defaultExecutor.execute(task);
        }
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    class ProcessTask implements Runnable {

        RemotingContext ctx;
        T msg;

        public ProcessTask(RemotingContext ctx, T msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                AbstractRemotingProcessor.this.doProcess(ctx, msg);
            } catch (Throwable e) {
                //protect the thread running this task
                String remotingAddress = RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                        .channel());
                log.error("Exception caught when process rpc request command in AbstractRemotingProcessor, Id="
                        + msg.getMessageId() + "! Invoke source address is [" + remotingAddress + "].", e);
            }
        }
    }
}
