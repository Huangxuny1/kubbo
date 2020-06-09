package com.huangxunyi.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.huangxunyi.serializer.Serializer;
import com.huangxunyi.serializer.SerializerType;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;

@Slf4j
public class KryoSerializer extends Serializer {
    private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            return kryo;
        }
    };

    private static final ThreadLocal<Output> outputThreadLocal = ThreadLocal.withInitial(() -> new Output(DEFAULT_BUF_SIZE, -1));

    @Override
    public byte code() {
        return SerializerType.KRYO.value();
    }

    @Override
    public <T> byte[] writeObject(T obj) {
        Output output = outputThreadLocal.get();
        try {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            return output.toBytes();
        } finally {
            output.clear();
            // 防止hold过大的内存块一直不释放
            if (output.getBuffer().length > MAX_CACHED_BUF_SIZE) {
                output.setBuffer(new byte[DEFAULT_BUF_SIZE], -1);
            }
        }
    }

    @Override
    public <T> T readObject(byte[] bytes, int offset, int length, Class<T> clazz) {
        Input input = new Input(bytes, offset, length);
        Kryo kryo = kryoThreadLocal.get();
        return kryo.readObject(input, clazz);
    }

    @Override
    public String toString() {
        return "kryo: " + code();
    }
}
