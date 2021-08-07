package com.thunder.rpc.io.serializer.protostuff;

import com.thunder.rpc.io.dto.RpcResponse;
import com.thunder.rpc.io.serializer.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {

    /**
     * Avoid re applying buffer space every time serialization
     */
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    public static void main(String[] args) {

        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        byte[] res = new byte[]{10, 36, 99, 54, 98, 99, 55, 100, 52, 97, 45, 57, 57, 49, 101, 45, 52, 57, 98, 56, 45, 57, 57, 52, 100, 45, 53, 97, 50, 97, 50, 55, 99, 101, 56, 52, 97, 97, 16, -56, 1, 26, 29, 84, 104, 101, 32, 114, 101, 109, 111, 116, 101, 32, 99, 97, 108, 108, 32, 105, 115, 32, 115, 117, 99, 99, 101, 115, 115, 102, 117, 108, 35, 74, 50, 99, 111, 109, 46, 116, 104, 117, 110, 100, 101, 114, 46, 114, 112, 99, 46, 115, 101, 114, 118, 101, 114, 46, 115, 101, 114, 118, 105, 99, 101, 46, 83, 101, 114, 118, 105, 99, 101, 73, 109, 112, 108, 64, 49, 57, 49, 49, 100, 100, 56, 36};
        System.out.println(protostuffSerializer.deserialize(res, RpcResponse.class));
    }

}
