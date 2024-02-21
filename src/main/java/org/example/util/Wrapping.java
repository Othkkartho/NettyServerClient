package org.example.util;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
            packer.packString("Memory Total");
            packer.packLong(info.getMemory().getTotal());

            packer.packString("Memory Available");
            packer.packLong(info.getMemory().getAvailable());

            packer.packString("ComputerSystemModel");
            packer.packString(info.getComputerSystem().getModel());

            packer.packString("GraphicCardVendor");
            packer.packString(info.getGraphicsCards().get(0).getVendor());
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

    public static HashMap<String, Object> unpacked(byte[] msg) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);

        HashMap<String, Object> map = new HashMap<>(5);

        String s = null;
        Object o = null;

        try {
            while (unpacker.hasNext()) {
                if (s != null && o != null) {
                    s = null; o = null;
                }

                Value v = unpacker.unpackValue();
                switch (v.getValueType()) {
                    case INTEGER:
                        IntegerValue iv = v.asIntegerValue();
                        if (iv.isInIntRange()) {
                            o = iv.toInt();
                        }
                        else if (iv.isInLongRange()) {
                            o = iv.toLong();
                        }
                        else {
                            o = iv.toBigInteger();
                        }
                        break;
                    case FLOAT:
                        FloatValue fv = v.asFloatValue();
                        o = fv.toDouble();
                        break;
                    case STRING:
                        if (s == null)
                            s = v.asStringValue().asString();
                        else
                            o = v.asStringValue().asString();
                        break;
                }

                if (s != null && o != null)
                    map.put(s, o);
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

        return map;
    }
}
