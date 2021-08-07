package com.thunder.rpc.io.server;

import com.thunder.rpc.io.dto.RpcRequest;
import com.thunder.rpc.io.dto.RpcResponse;
import com.thunder.rpc.io.dto.RpcMessage;
import com.thunder.rpc.io.dto.RpcResponseCodeEnum;
import com.thunder.rpc.io.handler.RequestHandler;
import com.thunder.rpc.io.serializer.Serializer;
import com.thunder.rpc.io.serializer.SerializerEnum;
import com.thunder.rpc.io.serializer.SerializerFactory;
import com.thunder.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerRpcServiceHandler extends ChannelInboundHandlerAdapter {

    private RequestHandler requestHandler;

    public ServerRpcServiceHandler(ServiceProvider serviceProvider) {
        this.requestHandler = new RequestHandler(serviceProvider);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                byte[] content = rpcMessage.getContent();
                byte serializerType = rpcMessage.getSerializerType();
                Serializer serializer = SerializerFactory.getSerializerByCode(SerializerEnum.getNameByCode(serializerType));
                RpcRequest rpcRequest = serializer.deserialize(content, RpcRequest.class);
                Object result = requestHandler.handle(rpcRequest);
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    RpcResponse rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                    byte[] responseContent = serializer.serialize(rpcResponse);
                    rpcMessage.setContent(responseContent);
                } else {
                    RpcResponse rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    byte[] responseContent = serializer.serialize(rpcResponse);
                    rpcMessage.setContent(responseContent);
                    log.error("not writable now, message dropped");
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception, e:{}", cause.getMessage());
        ctx.close();
    }
}
