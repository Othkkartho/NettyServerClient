package org.example.noclosetest.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.util.StringWrapping;
import org.example.util.nettyLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final Logger logger =  nettyLogger.getInstance().getLogConnection();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        String s = StringWrapping.unpacked(byteBuf);

        logger.log(Level.INFO, s);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "오류 발생");
        ctx.close();
    }
}
