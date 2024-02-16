package org.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.example.util.Wrapping;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final Logger logger = Logger.getLogger(EchoClientHandler.class.getName());
    private final SystemInfo info = new SystemInfo();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        HardwareAbstractionLayer sendMessage = info.getHardware();
        byte[] packer = Wrapping.packer(sendMessage);

        ByteBuf msgBuffer = Unpooled.buffer();
        msgBuffer.writeBytes(packer);

        ChannelFuture cf = ctx.writeAndFlush(msgBuffer);
        
        cf.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess())
                logger.log(Level.INFO, "클라이언트에서 전송 성공");
            else
                logger.log(Level.WARNING, "클라이언트에서 전송 실패");
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) {
        int length = msg.readableBytes();
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++)
            bytes[i] = msg.getByte(i);

        Wrapping.unpacked(bytes);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, String.valueOf(cause));
        ctx.close();
    }
}
