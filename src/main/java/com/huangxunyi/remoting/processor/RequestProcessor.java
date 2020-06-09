package com.huangxunyi.remoting.processor;

import com.huangxunyi.remoting.RemotingContext;
import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.MessageFactory;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestProcessor extends AbstractRemotingProcessor<Request> {
    public RequestProcessor(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public void doProcess(RemotingContext ctx, Request msg) throws Exception {
        long currentTimestamp = System.currentTimeMillis();
        preHandler(ctx, currentTimestamp);
        dispatchToUserProcessor(ctx, msg);
    }

    @SuppressWarnings("all")
    private void dispatchToUserProcessor(RemotingContext ctx, Request msg) {
        UserProcessor userProcessor = ctx.getUserProcessor(msg.getClass().getName());

        if (userProcessor != null) {
            try {
                Object o = userProcessor.handleRequest(userProcessor.preHandleRequest(ctx, msg), msg);
                if (o instanceof Response) {
                    ctx.getChannelContext().writeAndFlush((Response) o);
                }
//                new Throwable("send response ").printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void preHandler(RemotingContext ctx, long currentTimestamp) {
        // todo send arrived time ...
//        new Throwable(" preHandler").printStackTrace();

    }
}
