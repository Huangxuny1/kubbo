package com.huangxunyi.serializer;

import com.huangxunyi.serializer.kryo.KryoSerializer;

public class SerializerManager {
    private static Serializer[] serializers = new Serializer[5];

    static {
        addSerializer(SerializerType.KRYO, new KryoSerializer());
    }

    public static Serializer getSerializer(byte index) {
        return serializers[index];
    }

    public static Serializer getSerializer(SerializerType type) {
        return serializers[type.value()];
    }

    public static void addSerializer(SerializerType type, final Serializer serializer) {
        int index = type.value();
        if (serializers.length <= index) {
            Serializer[] newSerializers = new Serializer[index + 5];
            System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
            serializers = newSerializers;
        }
        serializers[index] = serializer;
    }

}
