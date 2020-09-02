package com.huangxunyi.remoting.message;

public class ResponseFuture {
    private String addr;

    private InvokeFuture future;

    /**
     * Constructor
     */
    public ResponseFuture(String addr, InvokeFuture future) {
        this.addr = addr;
        this.future = future;
    }

    public boolean isDone() {
        return this.future.isDone();
    }

    public Response get(int timeoutMillis) throws RuntimeException, InterruptedException {
        this.future.waitResponse(timeoutMillis);
        if (!isDone()) {
            throw new RuntimeException("Future get result timeout!");
        }
        Response response = this.future.waitResponse(timeoutMillis);
        return response;
    }

}
