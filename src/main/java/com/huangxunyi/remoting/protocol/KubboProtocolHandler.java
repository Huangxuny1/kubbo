package com.huangxunyi.remoting.protocol;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.MessageFactory;
import com.huangxunyi.remoting.processor.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class KubboProtocolHandler implements ProtocolHandler {

    private ProcessorManager processorManager;
    private MessageFactory messageFactory;

    public KubboProtocolHandler(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        this.processorManager = new ProcessorManager();
        this.processorManager.registerProcessor(Constants.TYPE_REQUEST,
                new RequestProcessor(this.messageFactory));
        this.processorManager.registerProcessor(Constants.TYPE_RESPONSE,
                new ResponseProcessor(this.messageFactory));
        this.processorManager.registerProcessor(Constants.TYPE_HEARTBEAT,
                new HeartbeatProcessor(this.messageFactory));

        log.error(" register default process");

    }

    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        try {
            process(ctx, msg);
        } catch (final Throwable t) {
            processException(ctx, msg, t);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void process(RemotingContext ctx, Object msg) {
        try {
            final KubboMessage cmd = (KubboMessage) msg;
            final RemotingProcessor processor = processorManager.getProcessor(cmd.getType());
            processor.process(ctx, cmd, processorManager.getDefaultExecutor());
        } catch (final Throwable t) {
            processException(ctx, msg, t);
        }
    }

    private void processException(RemotingContext ctx, Object msg, Throwable t) {
        final int id = ((KubboMessage) msg).getMessageId();
        final String emsg = "Exception caught when processing ";
        new Throwable(emsg).printStackTrace();
    }

    @Override
    public void registerProcessor(byte cmd, RemotingProcessor<?> processor) {
        this.processorManager.registerProcessor(cmd, processor);
    }

    @Override
    public void registerDefaultExecutor(ExecutorService executor) {
        this.processorManager.registerDefaultExecutor(executor);
    }

    @Override
    public ExecutorService getDefaultExecutor() {
        return this.processorManager.getDefaultExecutor();
    }
}
