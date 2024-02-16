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

            packer.packInt(3);
            packer.packArrayHeader(3).packInt(2).packString("String").packDouble(0.4567);
            packer.packBigInteger(BigInteger.valueOf(1239339812312323224L));

            packer.packFloat(0.3f);
            packer.packDouble(0.213512);
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

        ArrayList<Long> longs = new ArrayList<>();
        ArrayList<Double> doubles = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Value> objects = new ArrayList<>();

        try {
            while (unpacker.hasNext()) {
                Value v = unpacker.unpackValue();
                switch (v.getValueType()) {
                    case INTEGER:
                        IntegerValue iv = v.asIntegerValue();
                        if (iv.isInIntRange()) {
                            int i = iv.toInt();
                            longs.add((long) i);
                        }
                        else if (iv.isInLongRange()) {
                            long l = iv.toLong();
                            longs.add(l);
                        }
                        else {
                            BigInteger i = iv.toBigInteger();
                            longs.add(Long.valueOf(String.valueOf(i)));
                        }
                        break;
                    case FLOAT:
                        FloatValue fv = v.asFloatValue();
                        double d = fv.toDouble();

                        doubles.add(d);
                        break;
                    case STRING:
                        String s = v.asStringValue().asString();
                        strings.add(s);
                        break;
                    case ARRAY:
                        ArrayValue a = v.asArrayValue();
                        for (Value e : a) {
                            objects.add(e);
                        }
                        break;
                }
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
