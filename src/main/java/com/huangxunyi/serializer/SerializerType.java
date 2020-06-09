package com.huangxunyi.serializer;

public enum SerializerType {
    KRYO((byte) 0x01),
    //HESSIAN((byte) 0x02)
    // ...
    ;

    SerializerType(byte value) {
        if (0x00 < value && value < 0x10) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("out of range(0x01 ~ 0x0f): " + value);
        }
    }

    private final byte value;

    public byte value() {
        return value;
    }

    public static SerializerType parse(String name) {
        for (SerializerType s : values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public static SerializerType parse(byte value) {
        for (SerializerType s : values()) {
            if (s.value() == value) {
                return s;
            }
        }
        return null;
    }

    public static SerializerType getDefault() {
        return KRYO;
    }
}