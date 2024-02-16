package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {
    private final Logger logger = Logger.getLogger(EchoServer.class.getName());

    public static void main(String[] args) {
        int port = 8800;

        new EchoServer().serverRun(port);
    }

    public void serverRun(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture future;
            try {
                future = bootstrap.bind(port).sync();
                logger.log(Level.INFO, "서버시작");
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, String.valueOf(e));

                Thread.currentThread().interrupt();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
