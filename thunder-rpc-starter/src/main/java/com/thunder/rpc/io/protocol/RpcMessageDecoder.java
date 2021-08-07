package com.thunder.rpc.io.protocol;

import com.thunder.rpc.constants.RpcConstants;
import com.thunder.rpc.io.dto.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RpcMessageDecoder extends ByteToMessageDecoder {

    private static final int DATA_BEGIN_INDEX = 13;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < DATA_BEGIN_INDEX) {
            return;
        }
        while (true) {
            byteBuf.markReaderIndex();
            if (byteBuf.readByte() == RpcConstants.MAGIC_NUMBER) {
                break;
            }
            byteBuf.resetReaderIndex();
            if (byteBuf.readableBytes() < DATA_BEGIN_INDEX) {
                return;
            }
        }
        byte version = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        int requestId = byteBuf.readInt();
        int contentLength = byteBuf.readInt();
        byte[] data = new byte[contentLength];
        byteBuf.readBytes(data);

        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .content(data)
                .requestId(requestId)
                .serializerType(serializerType)
                .version(version)
                .build();
        list.add(rpcMessage);
    }
}
