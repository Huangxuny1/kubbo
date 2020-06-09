package com.huangxunyi.remoting.protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KProtocolManager {
    private static final ConcurrentMap<Byte, KProtocol> protocols = new ConcurrentHashMap<Byte, KProtocol>();

    public static KProtocol getProtocol(Byte protocolCode) {
        return protocols.get(protocolCode);
    }

//    public static void registerProtocol(KProtocol protocol, byte... protocolCodeBytes) {
//        registerProtocol(protocol, ProtocolCode.fromBytes(protocolCodeBytes));
//    }

    public static void registerProtocol(KProtocol protocol, Byte protocolCode) {
        if (null == protocolCode || null == protocol) {
            throw new RuntimeException("Protocol: " + protocol + " and protocol code:"
                    + protocolCode + " should not be null!");
        }
        KProtocol exists = KProtocolManager.protocols.putIfAbsent(protocolCode, protocol);
        if (exists != null) {
            throw new RuntimeException("Protocol for code: " + protocolCode + " already exists!");
        }
    }

    public static KProtocol unRegisterProtocol(byte protocolCode) {
        return KProtocolManager.protocols.remove(protocolCode);
    }
}
