package org.example.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    static Logger logger = Logger.getLogger(EchoServerHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
        StringBuilder builder = new StringBuilder();
        builder.append("수신한 문자열 [");
        builder.append(readMessage);
        builder.append("]");
        logger.log(Level.INFO, builder.toString());

        ByteBuf msgBuffer = Unpooled.buffer();
        msgBuffer.writeBytes("Server Response => received data : ".getBytes());

        ctx.write(msgBuffer);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "오류 발생");
        logger.log(Level.INFO, cause.toString());
        ctx.close();
    }
}
