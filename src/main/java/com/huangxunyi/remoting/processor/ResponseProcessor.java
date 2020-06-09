package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.message.InvokeFuture;
import com.huangxunyi.remoting.message.MessageFactory;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.utils.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseProcessor extends AbstractRemotingProcessor<Response> {

    public ResponseProcessor(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public void doProcess(RemotingContext ctx, Response msg) throws Exception {
        Connection conn = ctx.getChannelContext().channel().attr(Connection.CONNECTION).get();
        InvokeFuture future = conn.removeInvokeFuture(msg.getMessageId());
        ClassLoader oldClassLoader = null;
        try {
            if (future != null) {
                if (future.getAppClassLoader() != null) {
                    oldClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(future.getAppClassLoader());
                }
                future.putResponse(msg);
                future.cancelTimeout();
                try {
                    future.executeInvokeCallback();
                } catch (Exception e) {
                    log.error("Exception caught when executing invoke callback, id={}", msg.getMessageId(), e);
                }
            } else {
                log.warn("Cannot find InvokeFuture, maybe already timeout, id={}, from={} ",
                                msg.getMessageId(), RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        } finally {
            if (null != oldClassLoader) {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
//        new Throwable(Thread.currentThread() + " do process ").printStackTrace();
    }
}
