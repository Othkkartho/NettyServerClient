package org.example.connecttest.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.example.util.Wrapping;
import org.example.util.nettyLogger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = nettyLogger.getInstance().getLogConnection();
    private final SystemInfo info = new SystemInfo();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        int length = msg.readableBytes();
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++)
            bytes[i] = msg.getByte(i);

        Map<String, Object> map = Wrapping.unpacked(bytes);

        for (Map.Entry<String, Object> entry : map.entrySet())
            logger.log(Level.INFO, () -> "[Key]: " + entry.getKey() + ", [Value]: " + entry.getValue());

        HardwareAbstractionLayer sendMessage = info.getHardware();
        byte[] packer = Wrapping.packer(sendMessage);

        ByteBuf msgBuffer = Unpooled.buffer();
        msgBuffer.writeBytes(packer);

        ChannelFuture cf = ctx.writeAndFlush(msgBuffer);

        cf.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess())
                logger.log(Level.INFO, "서버에서 전송 성공");
            else
                logger.log(Level.WARNING, "서버에서 전송 실패");
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "오류 발생");
        logger.log(Level.INFO, cause.getLocalizedMessage());
        ctx.close();
    }
}
