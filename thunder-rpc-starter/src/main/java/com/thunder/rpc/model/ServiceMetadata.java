package com.thunder.rpc.model;

import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@Builder
public class ServiceMetadata {
    private InetSocketAddress address;
    private String serviceName;
    private String serializer;
    private Class<?> clazz;
    private Object service;
}
