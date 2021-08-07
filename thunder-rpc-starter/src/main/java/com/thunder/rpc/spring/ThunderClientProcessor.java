package com.thunder.rpc.spring;

import com.thunder.rpc.annotation.RpcConsumer;
import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.client.NettyClient;
import com.thunder.rpc.model.ServiceMetadata;
import com.thunder.rpc.proxy.ProxyFactory;
import com.thunder.rpc.registery.ServiceDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Objects;

public class ThunderClientProcessor implements BeanPostProcessor {

    private RpcProperties rpcProperties;
    private NettyClient nettyClient;

    public ThunderClientProcessor(NettyClient nettyClient, RpcProperties rpcProperties) {
        this.nettyClient = nettyClient;
        this.rpcProperties = rpcProperties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = bean.getClass();
        Field[] fileds = targetClass.getDeclaredFields();
        for (Field field : fileds) {
            RpcConsumer rpcConsumer = field.getAnnotation(RpcConsumer.class);
            if (Objects.nonNull(rpcConsumer)) {
                ServiceMetadata serviceMetadata = ServiceMetadata.builder()
                        .serviceName(rpcConsumer.serviceName())
                        .clazz(field.getType())
                        .build();
                ProxyFactory proxyFactory = ProxyFactory.getProxyFactory(rpcProperties.getProxy());
                Object clientProxy = proxyFactory.getProxy(nettyClient, rpcProperties, serviceMetadata);
                field.setAccessible(true);
                try {
                    field.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
