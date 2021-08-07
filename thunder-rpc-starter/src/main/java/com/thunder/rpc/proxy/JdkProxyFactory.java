package com.thunder.rpc.proxy;

import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.client.NettyClient;
import com.thunder.rpc.io.dto.RpcRequest;
import com.thunder.rpc.io.dto.RpcResponse;
import com.thunder.rpc.io.dto.RpcResponseCodeEnum;
import com.thunder.rpc.model.ServiceMetadata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JdkProxyFactory extends ProxyFactory {

    private static JdkProxyFactory proxyFactory = new JdkProxyFactory();

    public static ProxyFactory getInstance() {
        return proxyFactory;
    }

    @Override
    public Object getProxy(NettyClient nettyClient, RpcProperties rpcProperties, ServiceMetadata serviceMetadata) {

        return Proxy
                .newProxyInstance(serviceMetadata.getClazz().getClassLoader(), new Class[]{serviceMetadata.getClazz()},
                        new ClientInvocationHandler(nettyClient, rpcProperties, serviceMetadata));
    }

    private class ClientInvocationHandler implements InvocationHandler {
        private NettyClient nettyClient;
        private RpcProperties rpcProperties;
        private ServiceMetadata serviceMetadata;

        public ClientInvocationHandler(NettyClient nettyClient, RpcProperties rpcProperties, ServiceMetadata serviceMetadata) {
            this.nettyClient = nettyClient;
            this.rpcProperties = rpcProperties;
            this.serviceMetadata= serviceMetadata;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .methodName(method.getName())
                    .parameters(args)
                    .paramTypes(method.getParameterTypes())
                    .serviceName(serviceMetadata.getServiceName())
                    .build();
            CompletableFuture<RpcResponse> responseFuture =  nettyClient.sendRpcRequest(rpcRequest, rpcProperties);
            RpcResponse rpcResponse = responseFuture.get();
            if (rpcResponse == null) {
                throw new RuntimeException();
            }
            if (rpcResponse.getCode() == RpcResponseCodeEnum.SUCCESS.getCode()) {
                return rpcResponse.getData();
            }
            throw new RuntimeException();
        }
    }
}
