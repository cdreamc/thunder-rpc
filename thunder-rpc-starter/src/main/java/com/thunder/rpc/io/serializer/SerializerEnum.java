package com.thunder.rpc.io.serializer;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum SerializerEnum {

    UNKONWN((byte) '0', "UNKNOWN"),
    PROTOSTUFF((byte) '1', "protoStuff"),
    KYRO((byte) '2', "kyro");

    @Getter
    public final byte code;
    @Getter
    public final String name;

    private static final Map<Byte, String> code2Name = new HashMap<>();
    private static final Map<String, Byte> name2Code = new HashMap<>();
    static {
        for (SerializerEnum item : SerializerEnum.values()) {
            code2Name.put(item.code, item.name);
            name2Code.put(item.name, item.code);
        }
    }

    SerializerEnum(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(byte code) {
        return code2Name.getOrDefault(code, UNKONWN.getName());
    }

    public static Byte getCodeByName(String name) {
        return name2Code.getOrDefault(name, UNKONWN.getCode());
    }
}
