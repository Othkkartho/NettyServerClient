package org.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = Logger.getLogger(EchoClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String sendMessage = "Hello Netty";

        ByteBuf msgBuffer = Unpooled.buffer();
        msgBuffer.writeBytes(sendMessage.getBytes());

        logger.log(Level.INFO, () -> "Client 전송한 문자열 [" + sendMessage + "]");

        ctx.writeAndFlush(msgBuffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

        logger.log(Level.INFO, () -> "Client 수신한 문자열 [" + readMessage + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, cause.toString());
        ctx.close();
    }
}
