package serializer;

import com.huangxunyi.serializer.Serializer;
import com.huangxunyi.serializer.SerializerManager;
import com.huangxunyi.serializer.SerializerType;
import org.junit.jupiter.api.Test;

public class TestSerializer {
    @Test
    public void testKryo(){
        Serializer serializer = SerializerManager.getSerializer(SerializerType.KRYO);
        byte[] hello = serializer.writeObject("Hello");
        String s = serializer.readObject(hello, String.class);
    }
}
