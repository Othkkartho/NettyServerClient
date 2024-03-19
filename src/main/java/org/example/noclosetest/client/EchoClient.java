package org.example.noclosetest.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.example.util.StringWrapping;
import org.example.util.nettyChannel;
import org.example.util.nettyLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient {
    private final Logger logger =  nettyLogger.getInstance().getLogConnection();
    private static final Channel serverChannel = nettyChannel.getInstance().getChannelConnection();

    public void start(String message) {
        ChannelFuture future;

        byte[] bytes = StringWrapping.packer(message);

        ByteBuf msgBuffer = Unpooled.buffer();
        future = serverChannel.writeAndFlush(msgBuffer.writeBytes(bytes));

        try {
            if ("quit".equals(message)) {
                serverChannel.closeFuture().sync();
            }

            if (future != null)
                future.sync();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, String.valueOf(e));

            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        nettyChannel.getInstance().close();
    }
}
