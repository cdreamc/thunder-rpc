package com.thunder.rpc.io.serializer;

import com.thunder.rpc.io.serializer.kyro.KryoSerializer;
import com.thunder.rpc.io.serializer.protostuff.ProtostuffSerializer;

public class SerializerFactory {

    private static final ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
    private static final KryoSerializer kryoSerializer = new KryoSerializer();

    public static Serializer getSerializerByCode(String name) {
        switch (name) {
            case "kryo":
                return kryoSerializer;
            default:
                return protostuffSerializer;
        }
    }

    public static Serializer getSerializerByCode(byte code) {
        switch (code) {
            case (byte)'2':
                return kryoSerializer;
            default:
                return protostuffSerializer;
        }
    }
}
