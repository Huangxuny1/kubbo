package com.huangxunyi.remoting.message;

import com.huangxunyi.utils.RemotingUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class KubboMessageFactory implements MessageFactory {
    @Override
    public KubboMessage createRequestCommand(Object requestObject) {
        log.info("createRequestCommand  ");
        Request request = new Request();
        return request;
    }

    @Override
    public KubboMessage createResponse(Object responseObject, KubboMessage requestCmd) {
        log.info(" createResponse ");
        Response response = new Response();

        return response;
    }

    @Override
    public KubboMessage createExceptionResponse(int id, String errMsg) {
        log.info("createExceptionResponse");
        Response response = new Response();
        response.setErrorMsg(errMsg);
        response.setMessageId(id);
        return response;
    }

    @Override
    public KubboMessage createExceptionResponse(int id, Throwable t, String errMsg) {
        log.info("createExceptionResponse");
        return null;
    }

    @Override
    public KubboMessage createExceptionResponse(int id, ResponseStatus status) {
        log.info(" createExceptionResponse ");
        return null;
    }

    @Override
    public KubboMessage createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        log.info("createExceptionResponse");
        return null;
    }

    @Override
    public Response createTimeoutResponse(InetSocketAddress address) {
        log.info("createTimeoutResponse");
        Response response = new Response();
        response.setCode(500);
        response.setErrorMsg(" Timeout " + RemotingUtil.parseSocketAddressToString(address));
        response.setMessageId(0);
        return response;
    }

    @Override
    public KubboMessage createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        log.info("createSendFailedResponse");
        return null;
    }

    @Override
    public KubboMessage createConnectionClosedResponse(InetSocketAddress address, String message) {
        return null;
    }
}
