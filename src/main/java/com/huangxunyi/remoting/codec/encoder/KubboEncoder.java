package com.huangxunyi.remoting.codec.encoder;

import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;
import com.huangxunyi.serializer.SerializerManager;
import com.huangxunyi.serializer.SerializerType;
import com.huangxunyi.utils.CrcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubboEncoder implements KEncoder {

    @Override
    public void encode(ChannelHandlerContext ctx, KubboMessage msg, ByteBuf out) throws Exception {
        log.debug(msg.toString());
        int index = out.writerIndex();
        // magic
        out.writeShort(Constants.MAGIC_NUMBER);
        // vers
        out.writeByte(Constants.VERSION_1);
        // type
        byte type = msg.getType();
        out.writeByte(type);
        // cmd
        out.writeByte(msg.getCmd());
        // ser
        out.writeByte(SerializerType.KRYO.value());
        // seq
        out.writeInt(msg.getMessageId());

        byte[] content = SerializerManager.getSerializer(SerializerType.KRYO).writeObject(msg);
        // length
        out.writeInt(content.length);
        // ------ header end ------
        // content
        out.writeBytes(content);

        byte[] frame = new byte[out.readableBytes()];
        out.getBytes(index, frame);
        // crc
        out.writeInt(CrcUtil.crc32(frame));
    }
}
