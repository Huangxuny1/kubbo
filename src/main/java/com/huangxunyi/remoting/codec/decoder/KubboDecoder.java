package com.huangxunyi.remoting.codec.decoder;

import com.huangxunyi.remoting.codec.Constants;
import com.huangxunyi.remoting.message.MessageType;
import com.huangxunyi.remoting.message.KubboMessage;
import com.huangxunyi.serializer.SerializerManager;
import com.huangxunyi.utils.CrcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class KubboDecoder implements KDecoder {

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        // header length = 14
        if (in.readableBytes() >= 14) {
            int startIndex = in.readerIndex();
            short magic = in.readShort();
            if (Constants.MAGIC_NUMBER != magic) {
                log.error(" magic incorrect ... " + magic);
                return;
            }
            byte vers = in.readByte();
            byte type = in.readByte();
            byte cmd = in.readByte();
            byte ser = in.readByte();
            int seq = in.readInt();
            int len = in.readInt();
            if (in.readableBytes() >= len + 4) { // content + crc
                byte[] content = null;
                if (len > 0) {
                    content = new byte[len];
                    in.readBytes(content);
                }
                checkCRC(in, startIndex);
                Optional<MessageType> typeOptional = Arrays.stream(MessageType.values()).filter(i -> i.getType() == type).findFirst();
                if (typeOptional.isEmpty()) {
                    return;
                }
                Class<? extends KubboMessage> typeClazz = typeOptional.get().getClazz();
                KubboMessage message = SerializerManager.getSerializer(ser).readObject(content, typeClazz);
                message.setCmd(cmd);
                message.setMessageId(seq);
                message.setType(type);
                if (log.isDebugEnabled()) {
                    log.debug(" receive message : {}", message);
                    log.debug(" vers:{} type:{} cmd:{} seq:{} ", vers, type, cmd, seq);
                }
                out.add(message);

            } else {
                in.resetReaderIndex();
                return;
            }
        }
    }

    private void checkCRC(ByteBuf in, int startIndex) {
        int endIndex = in.readerIndex();
        int expectedCrc = in.readInt();
        byte[] frame = new byte[endIndex - startIndex];
        in.getBytes(startIndex, frame, 0, endIndex - startIndex);
        int actualCrc = CrcUtil.crc32(frame);
        if (expectedCrc != actualCrc) {
            throw new RuntimeException("CRC check failed!");
        }
    }
}
