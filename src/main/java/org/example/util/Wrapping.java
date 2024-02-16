package org.example.util;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class Wrapping {
    private static final GlobalLogger logger = new GlobalLogger(Wrapping.class.getName());
    private static final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

    private Wrapping() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] packer(HardwareAbstractionLayer info) {
        try {
            packer.packMapHeader(3);

            packer.packString("Memory Total");
            packer.packLong(info.getMemory().getTotal());

            packer.packString("Memory Available");
            packer.packLong(info.getMemory().getAvailable());

            packer.packString("Memory PageSize");
            packer.packLong(info.getMemory().getPageSize());
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

    public static HashMap<String, Long> unpacked(byte[] msg) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);

        HashMap<String, Long> map = new HashMap<>();
        int num = 0;

        String[] memoryName = new String[0];
        Long[] size = new Long[0];

        try {
            num = unpacker.unpackMapHeader();

            memoryName = new String[num];
            size = new Long[num];

            for (int i = 0; i < num; ++i) {
                memoryName[i] = unpacker.unpackString();
                size[i] = unpacker.unpackLong();
            }
        } catch (IOException e) {
            logger.logging(Level.WARNING, String.valueOf(e));
        } finally {
            try {
                unpacker.close();
            } catch (IOException e) {
                logger.logging(Level.WARNING, String.valueOf(e));
            }
        }

        for (int i = 0; i < num; i++)
            logger.logging(Level.INFO, String.format("%s: %d", memoryName[i], size[i]));

        return map;
    }
}
