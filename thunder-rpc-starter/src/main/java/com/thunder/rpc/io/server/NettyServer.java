package com.thunder.rpc.io.server;

import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.handler.IMIdleStateHandler;
import com.thunder.rpc.io.protocol.RpcMessageDecoder;
import com.thunder.rpc.io.protocol.RpcMessageEncoder;
import com.thunder.rpc.provider.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

@Slf4j
public class NettyServer {

    private ServiceProvider serviceProvider;

    public NettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void init(RpcProperties rpcProperties) {
        Executors.newSingleThreadExecutor().execute(()->start(rpcProperties));
    }

    private void start(RpcProperties rpcProperties) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 开启Nagle算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 开启心跳
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IMIdleStateHandler());
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(new RpcMessageDecoder());
                            pipeline.addLast(new ServerRpcServiceHandler(serviceProvider));
                        }
                    });
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(host, rpcProperties.getServerConfig().getPort()).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
            log.info("netty server started!!!");
        } catch (InterruptedException | UnknownHostException e) {
            log.error("exception occurs when server start", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
