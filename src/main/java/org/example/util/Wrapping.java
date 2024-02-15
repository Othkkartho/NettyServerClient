package org.example.util;

import io.netty.buffer.ByteBuf;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ImmutableValue;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Wrapping {
    private static Logger logger = Logger.getLogger(Wrapping.class.getName());
    private static MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

    public static byte[] packer(HardwareAbstractionLayer info) {
        try {
            packer.packString("MemoryTotal");
            packer.packLong(info.getMemory().getTotal());
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

    public static byte[] unpacked(byte[] msg) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);

        String memoryName = "";
        long size = 0L;

        try {
            memoryName = unpacker.unpackString();
            size = unpacker.unpackLong();
        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                unpacker.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, String.valueOf(e));
            }
        }

        long finalSize = size;
        String finalMemoryName = memoryName;
        logger.log(Level.INFO, () -> finalMemoryName + ": " + finalSize);

        return msg;
    }
}
