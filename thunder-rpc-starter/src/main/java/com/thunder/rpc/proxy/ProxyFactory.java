package com.thunder.rpc.proxy;

import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.client.NettyClient;
import com.thunder.rpc.model.ServiceMetadata;

public abstract class ProxyFactory {

    public abstract Object getProxy(NettyClient nettyClient, RpcProperties rpcProperties, ServiceMetadata serviceMetadata);

    public static ProxyFactory getProxyFactory(String name) {
        switch (name) {
            case "jdk":
                return JdkProxyFactory.getInstance();
            default:
                return JdkProxyFactory.getInstance();
        }
    }
}
