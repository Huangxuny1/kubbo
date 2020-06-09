package com.huangxunyi.remoting.codec;

public class Constants {
    public static final byte VERSION_1 = 0x01;

    // ceeeeeeeeeeeeeeeeeeeeeeeeeeeeb
    public static final short MAGIC_NUMBER = (short) 0xcebb;

    public static final byte TYPE_REQUEST = 0x01;
    public static final byte TYPE_RESPONSE = 0x02;
    public static final byte TYPE_HEARTBEAT = 0x09;

    public static final byte TYPE_REQUEST_ONEWAY = 0x03;

    public static final byte CMD_ONEWAY = 0x01;
    public static final byte CMD_SYNC = 0x02;
    public static final byte CMD_ASYNC = 0x03;
}
