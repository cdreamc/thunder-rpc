package com.thunder.rpc.io.protocol;

import com.thunder.rpc.constants.RpcConstants;
import com.thunder.rpc.io.dto.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * custom protocol decoder
 * <pre>
 *   0        1        2              3                4                   8                    12
 *   +--------+--------+--------------+----------------+----+----+----+----+----+----+----+-----+
 *   | magic  |version |  messageType | serializerType |    RequestId      |   contentLength    |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------++
 *   |                                                                                          |
 *   |                                         body                                             |
 *   |                                                                                          |
 *   |                                        ... ...                                           |
 *   +------------------------------------------------------------------------------------------+
 * </pre>
 * <p>
 * </p>
 *
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(RpcConstants.MAGIC_NUMBER);
        byteBuf.writeByte(rpcMessage.getVersion());
        byteBuf.writeByte(rpcMessage.getMessageType());
        byteBuf.writeByte(rpcMessage.getSerializerType());
        byteBuf.writeInt(ATOMIC_INTEGER.addAndGet(1));
        byteBuf.writeInt(rpcMessage.getContent().length);
        byteBuf.writeBytes(rpcMessage.getContent());
    }
}
