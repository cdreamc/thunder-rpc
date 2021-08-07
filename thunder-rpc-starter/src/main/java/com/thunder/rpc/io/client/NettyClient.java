package com.thunder.rpc.io.client;

import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.dto.RpcRequest;
import com.thunder.rpc.io.dto.RpcResponse;
import com.thunder.rpc.io.dto.RpcMessage;
import com.thunder.rpc.io.protocol.RpcMessageDecoder;
import com.thunder.rpc.io.protocol.RpcMessageEncoder;
import com.thunder.rpc.io.serializer.Serializer;
import com.thunder.rpc.io.serializer.SerializerEnum;
import com.thunder.rpc.io.serializer.SerializerFactory;
import com.thunder.rpc.registery.ServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final Map<InetSocketAddress, Channel> channelMap;
    private final ServiceDiscovery serviceDiscovery;

    public NettyClient(ServiceDiscovery serviceDiscovery) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new ClientRpcHandler());
                    }
                });
        channelMap = new ConcurrentHashMap<>();
        this.serviceDiscovery = serviceDiscovery;
    }

    @SneakyThrows
    public Channel connect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        try {
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalStateException();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return completableFuture.get();
    }

    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest, RpcProperties rpcProperties) {
        CompletableFuture<RpcResponse> requestFuture = new CompletableFuture<>();
        InetSocketAddress address = serviceDiscovery.getAddress(rpcRequest.getServiceName());
        Channel channel = getChannel(address);
        if (channel.isActive()) {
            ProcessRequestCache.put(rpcRequest.getRequestId(), requestFuture);
            String serializerStr = rpcProperties.getSerializer();
            Serializer serializer = SerializerFactory.getSerializerByCode(serializerStr);
            RpcMessage rpcMessage = RpcMessage.builder()
                    .serializerType(SerializerEnum.getCodeByName(rpcProperties.getSerializer()))
                    .messageType((byte)('1'))
                    .content(serializer.serialize(rpcRequest))
                    .version((byte)'0')
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("send message success, msg:{}", rpcMessage);
                } else {
                    future.channel().close();
                    requestFuture.completeExceptionally(future.cause());
                    log.error("send message fail", future.cause());
                }
            });
            return requestFuture;
        }
        throw new IllegalStateException("channel is not active");
    }

    private Channel getChannel(InetSocketAddress address) {
        Channel channel = channelMap.get(address);
        if (Objects.isNull(channel)) {
            channel = connect(address);
            channelMap.put(address, channel);
        }
        return channel;
    }
}
