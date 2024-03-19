package org.example.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.example.noclosetest.client.EchoClientHandler;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nettyChannel {
    private final Logger logger =  nettyLogger.getInstance().getLogConnection();
    private static Channel serverChannel = null;
    private static nettyChannel channelInstance = null;
    private static EventLoopGroup eventLoopGroup = null;

    private nettyChannel(String host, int port) {
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new EchoClientHandler());
                    }
                });

        try {
            serverChannel = bootstrap.connect().sync().channel();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, String.valueOf(e));

            Thread.currentThread().interrupt();
        }
        
        logger.log(Level.INFO, "연결 완료");
    }

    public static nettyChannel getInstance() {
        if (channelInstance == null) {
            channelInstance = new nettyChannel("localhost", 8800);
        }

        return channelInstance;
    }

    public Channel getChannelConnection() {
        return serverChannel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
