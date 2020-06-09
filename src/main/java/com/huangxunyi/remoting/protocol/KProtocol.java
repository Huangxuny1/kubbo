package com.huangxunyi.remoting.protocol;

import com.huangxunyi.remoting.codec.heartbeat.HeartbeatTrigger;
import com.huangxunyi.remoting.codec.decoder.KDecoder;
import com.huangxunyi.remoting.codec.encoder.KEncoder;
import com.huangxunyi.remoting.message.MessageFactory;

public interface KProtocol {

    KEncoder getEncoder();

    KDecoder getDecoder();

    HeartbeatTrigger getHeartbeatTrigger();

    ProtocolHandler getProtocolHandler();

    MessageFactory getMessageFactory();
}
