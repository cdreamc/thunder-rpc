package com.thunder.rpc.io.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMessage {

    // request id
    private int requestId;

    // serializer type
    private byte serializerType;

    // message type
    private byte messageType;

    // version
    private byte version;

    // data
    private byte[] content;
}
