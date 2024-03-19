package org.example.noclosetest.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.example.util.StringWrapping;
import org.example.util.nettyLogger;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient {
    private static final Logger logger =  nettyLogger.getInstance().getLogConnection();

    private Channel serverChannel;
    private EventLoopGroup eventLoopGroup;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8800;

        EchoClient echoClient = new EchoClient();

        try {
            echoClient.clientRun(host, port);
            logger.log(Level.INFO, "연결 완료");
            echoClient.start();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, String.valueOf(e));

            Thread.currentThread().interrupt();
        } finally {
            echoClient.close();
        }
    }

    public void clientRun(String host, int port) throws InterruptedException {
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

        serverChannel = bootstrap.connect().sync().channel();
    }

    public void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        String message;
        ChannelFuture future;

        while (true) {
            message = scanner.nextLine();
            message.concat("\n");

            byte[] bytes = StringWrapping.packer(message);

            ByteBuf msgBuffer = Unpooled.buffer();
            future = serverChannel.writeAndFlush(msgBuffer.writeBytes(bytes));

            if ("quit".equals(message)) {
                serverChannel.closeFuture().sync();
                break;
            }
        }

        if (future != null)
            future.sync();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
