package org.example.noclosetest.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.util.StringWrapping;
import org.example.util.nettyLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = nettyLogger.getInstance().getLogConnection();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        String s = StringWrapping.unpacked(byteBuf);

        logger.log(Level.INFO, s);

        String message = "Response : '" + s + "' received\n";

        byte[] bytes = StringWrapping.packer(message);

        ByteBuf msgBuffer = Unpooled.buffer();
        channelHandlerContext.channel().writeAndFlush(msgBuffer.writeBytes(bytes));
        if ("quit".equals(s)) {
            channelHandlerContext.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "오류 발생");
        logger.log(Level.INFO, cause.getLocalizedMessage());
        ctx.close();
    }
}
