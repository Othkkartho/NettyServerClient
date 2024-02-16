package org.example.util;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Wrapping {
    private static final GlobalLogger logger = new GlobalLogger(Wrapping.class.getName());
    private static final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

    private Wrapping() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] packer(HardwareAbstractionLayer info) {
        try {
            packer.packString("MemoryTotal");
            packer.packLong(info.getMemory().getTotal());
        } catch (IOException e) {
            logger.logging(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                packer.close();
            } catch (IOException e) {
                logger.logging(Level.WARNING, String.valueOf(e));
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
            logger.logging(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                unpacker.close();
            } catch (IOException e) {
                logger.logging(Level.WARNING, String.valueOf(e));
            }
        }

        long finalSize = size;
        String finalMemoryName = memoryName;
        logger.logging(Level.INFO, String.format("%s: %d",finalMemoryName, finalSize));

        return msg;
    }
}
