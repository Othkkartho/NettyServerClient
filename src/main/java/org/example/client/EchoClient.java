package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient {
    private final Logger logger = Logger.getLogger(EchoClient.class.getName());

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8800;

        new EchoClient().clientRun(host, port);
    }

    public void clientRun(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, String.valueOf(e));

            Thread.currentThread().interrupt();
        } finally {
            group.shutdownGracefully();
        }
    }
}
