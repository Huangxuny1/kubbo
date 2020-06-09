package com.huangxunyi.remoting.message;

import java.net.InetSocketAddress;

public interface MessageFactory {

   KubboMessage createRequestCommand(final Object requestObject);

   KubboMessage createResponse(final Object responseObject,
                                              KubboMessage requestCmd);

   KubboMessage createExceptionResponse(int id, String errMsg);

   KubboMessage createExceptionResponse(int id, final Throwable t, String errMsg);

   KubboMessage createExceptionResponse(int id, ResponseStatus status);

   KubboMessage createExceptionResponse(int id, ResponseStatus status,
                                                       final Throwable t);

   KubboMessage createTimeoutResponse(final InetSocketAddress address);

   KubboMessage createSendFailedResponse(final InetSocketAddress address,
                                                        Throwable throwable);
   KubboMessage createConnectionClosedResponse(final InetSocketAddress address,
                                                              String message);
}
