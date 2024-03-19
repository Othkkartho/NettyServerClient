package org.example.noclosetest.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.example.util.nettyLogger;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {
    private static final Logger logger = nettyLogger.getInstance().getLogConnection();
    private final ChannelGroup allChannels = new DefaultChannelGroup("server", GlobalEventExecutor.INSTANCE);

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8800;

        new EchoServer().serverRun(host, port);
    }

    public void serverRun(String host, int port) {
        bossEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        workerEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("worker"));

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture future;
            try {
                // Channel 생성 후 기다림
                future = bootstrap.bind(new InetSocketAddress(host, port)).sync();
                Channel channel = future.channel();
                allChannels.add(channel);
                logger.log(Level.INFO, "서버시작");
                
                // Channel이 닫힐 때까지 대기
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, String.valueOf(e));

                Thread.currentThread().interrupt();
            }
        } finally {
            allChannels.close().awaitUninterruptibly();
            workerEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
            bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }
    }
}
