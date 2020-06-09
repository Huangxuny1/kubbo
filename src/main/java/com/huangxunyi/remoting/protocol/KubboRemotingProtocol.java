package com.huangxunyi.remoting.protocol;

import com.huangxunyi.remoting.codec.decoder.KDecoder;
import com.huangxunyi.remoting.codec.decoder.KubboDecoder;
import com.huangxunyi.remoting.codec.encoder.KEncoder;
import com.huangxunyi.remoting.codec.encoder.KubboEncoder;
import com.huangxunyi.remoting.codec.heartbeat.HeartbeatTrigger;
import com.huangxunyi.remoting.codec.heartbeat.KubboHeartbeat;
import com.huangxunyi.remoting.message.KubboMessageFactory;
import com.huangxunyi.remoting.message.MessageFactory;

public class KubboRemotingProtocol implements KProtocol {


    private static final KEncoder ENCODER = new KubboEncoder();
    private static final KDecoder DECODER = new KubboDecoder();
    private static final HeartbeatTrigger HEARTBEAT_TRIGGER = new KubboHeartbeat();
    private static final MessageFactory MESSAGE_FACTORY = new KubboMessageFactory();
    private static final ProtocolHandler PROTOCOL_HANDLER = new KubboProtocolHandler(MESSAGE_FACTORY);

    private KubboRemotingProtocol() {
    }

    private static final KubboRemotingProtocol KUBBO_REMOTING_PROTOCOL = new KubboRemotingProtocol();

    public static KubboRemotingProtocol getInstance() {
        return KUBBO_REMOTING_PROTOCOL;
    }

    @Override
    public KEncoder getEncoder() {
        return ENCODER;
    }

    @Override
    public KDecoder getDecoder() {
        return DECODER;
    }

    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return HEARTBEAT_TRIGGER;
    }

    @Override
    public ProtocolHandler getProtocolHandler() {
        return PROTOCOL_HANDLER;
    }

    @Override
    public MessageFactory getMessageFactory() {
        return MESSAGE_FACTORY;
    }


}
