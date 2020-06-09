package com.huangxunyi.remoting.processor;

import com.huangxunyi.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ProcessorManager {
    private final ConcurrentHashMap<Byte, RemotingProcessor<?>> cmd2processors = new ConcurrentHashMap<>(
            4);
    private RemotingProcessor<?> defaultProcessor;
    private ExecutorService defaultExecutor;

    public ProcessorManager() {
        defaultExecutor = new ThreadPoolExecutor(4, 10, 10000,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new NamedThreadFactory(
                "kubbo-default-executor", true));
    }


    public void registerProcessor(Byte cmdCode, RemotingProcessor<?> processor) {
        if (this.cmd2processors.containsKey(cmdCode)) {
            log.warn("Processor for cmd={} is already registered, the processor is {}, and changed to {}",
                    cmdCode, cmd2processors.get(cmdCode).getClass().getName(), processor.getClass().getName());
        }
        this.cmd2processors.put(cmdCode, processor);
    }

    public void registerDefaultProcessor(RemotingProcessor<?> processor) {
        if (this.defaultProcessor == null) {
            this.defaultProcessor = processor;
        } else {
            throw new IllegalStateException("The defaultProcessor has already been registered: "
                    + this.defaultProcessor.getClass());
        }
    }

    public RemotingProcessor<?> getProcessor(byte cmdCode) {
        RemotingProcessor<?> processor = this.cmd2processors.get(cmdCode);
        if (processor != null) {
            return processor;
        }
        return this.defaultProcessor;
    }

    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }

    public void registerDefaultExecutor(ExecutorService executor) {
        this.defaultExecutor = executor;
    }

}
