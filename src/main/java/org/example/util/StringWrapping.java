package org.example.util;

import io.netty.buffer.ByteBuf;
import org.example.noclosetest.util.nettyLogger;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StringWrapping {
    private static final Logger logger = nettyLogger.getInstance().getLogConnection();
    private static final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

    private StringWrapping() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] packer(String message) {
        try {
            packer.packString(message);
        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                packer.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, String.valueOf(e));
            }
        }

        return packer.toByteArray();
    }

    public static String unpacked(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++)
            bytes[i] = byteBuf.getByte(i);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        String s = null;

        try {
            while (unpacker.hasNext()) {
                Value v = unpacker.unpackValue();
                switch (v.getValueType()) {
                    case STRING:
                        s = v.asStringValue().asString();
                        break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                unpacker.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, String.valueOf(e));
            }
        }

        return s;
    }
}
