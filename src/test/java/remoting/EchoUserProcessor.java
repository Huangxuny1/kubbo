package remoting;

import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.remoting.processor.BizContext;
import com.huangxunyi.remoting.processor.SyncUserProcessor;

import java.util.concurrent.atomic.AtomicInteger;

public class EchoUserProcessor extends SyncUserProcessor<Request> {
    private AtomicInteger ac = new AtomicInteger(0);

    @Override
    public Object handleRequest(BizContext bizCtx, Request request) throws Exception {
        ac.incrementAndGet();
        if (request.getCmd() != Constants.CMD_ONEWAY) {
            Response response = new Response();
            response.setMessageId(request.getMessageId());
            response.setCmd(request.getCmd());
            response.setCode(200);
            response.setData(request.getClassName());
            return response;
        }
        return null;
    }

    public int gethandlers() {
        return ac.get();
    }

    @Override
    public String interest() {
        return Request.class.getName();
    }
}
