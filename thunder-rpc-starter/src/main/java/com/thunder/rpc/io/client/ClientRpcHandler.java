package com.thunder.rpc.io.client;

import com.thunder.rpc.io.dto.RpcResponse;
import com.thunder.rpc.io.dto.RpcMessage;
import com.thunder.rpc.io.serializer.Serializer;
import com.thunder.rpc.io.serializer.SerializerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientRpcHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                Serializer serializer = SerializerFactory.getSerializerByCode(tmp.getSerializerType());
                RpcResponse rpcResponse = serializer.deserialize(tmp.getContent(), RpcResponse.class);
                ProcessRequestCache.complete(rpcResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
