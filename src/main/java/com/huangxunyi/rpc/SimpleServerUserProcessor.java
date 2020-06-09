package com.huangxunyi.rpc;

import com.huangxunyi.proxy.cglib.CGLIBProxyFactory;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.processor.BizContext;
import com.huangxunyi.remoting.processor.SyncUserProcessor;
import com.huangxunyi.test.FooImpl;
import com.huangxunyi.test.IFoo;
import com.huangxunyi.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SimpleServerUserProcessor extends SyncUserProcessor<Request> {

    /**
     * delay milliseconds
     */
    private long delayMs;

    /**
     * whether delay or not
     */
    private boolean delaySwitch;

    /**
     * executor
     */
    private ThreadPoolExecutor executor;

    /**
     * default is true
     */
    private boolean timeoutDiscard = true;

    private AtomicInteger invokeTimes = new AtomicInteger();

    private AtomicInteger onewayTimes = new AtomicInteger();
    private AtomicInteger syncTimes = new AtomicInteger();
    private AtomicInteger futureTimes = new AtomicInteger();
    private AtomicInteger callbackTimes = new AtomicInteger();

    private String remoteAddr;
    private CountDownLatch latch = new CountDownLatch(1);

    public SimpleServerUserProcessor() {
        this.delaySwitch = false;
        this.delayMs = 0;
        this.executor = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4), new NamedThreadFactory("Request-process-pool"));
    }

    public SimpleServerUserProcessor(long delay) {
        this();
        if (delay < 0) {
            throw new IllegalArgumentException("delay time illegal!");
        }
        this.delaySwitch = true;
        this.delayMs = delay;
    }

    public SimpleServerUserProcessor(long delay, int core, int max, int keepaliveSeconds,
                                     int workQueue) {
        this(delay);
        this.executor = new ThreadPoolExecutor(core, max, keepaliveSeconds, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(workQueue), new NamedThreadFactory(
                "Request-process-pool"));
    }

    // ~~~ override methods

    @Override
    public Object handleRequest(BizContext bizCtx, Request request) throws Exception {
        log.warn("Request received:" + request + ", timeout:" + bizCtx.getClientTimeout()
                + ", arriveTimestamp:" + bizCtx.getArriveTimestamp());

        if (bizCtx.isRequestTimeout()) {
            String errMsg = "Stop process in server biz thread, already timeout!";
            processTimes(request);
            log.warn(errMsg);
            throw new Exception(errMsg);
        }

        this.remoteAddr = bizCtx.getRemoteAddress();

        CGLIBProxyFactory cglibProxyFactory = new CGLIBProxyFactory();
        Invoker<IFoo> invoker = cglibProxyFactory.getInvoker(new FooImpl(), IFoo.class, "123");
        Response invoke = invoker.invoke(request);
        invoke.setCmd(request.getCmd());
        invoke.setMessageId(request.getMessageId());
        return invoke;

    }

    @Override
    public String interest() {
        return Request.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    // ~~~ public methods
    public int getInvokeTimes() {
        return this.invokeTimes.get();
    }

    public String getRemoteAddr() throws InterruptedException {
        latch.await(100, TimeUnit.MILLISECONDS);
        return this.remoteAddr;
    }

    // ~~~ private methods
    private void processTimes(Request req) {
    }

}