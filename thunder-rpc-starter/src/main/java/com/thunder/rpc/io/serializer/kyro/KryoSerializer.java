package com.thunder.rpc.io.serializer.kyro;

import com.thunder.rpc.io.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KryoSerializer implements Serializer {


    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }

}
