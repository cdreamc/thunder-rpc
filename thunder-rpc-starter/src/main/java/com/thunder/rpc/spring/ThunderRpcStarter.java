package com.thunder.rpc.spring;

import com.thunder.rpc.annotation.RpcProvider;
import com.thunder.rpc.config.RpcProperties;
import com.thunder.rpc.io.server.NettyServer;
import com.thunder.rpc.model.ServiceMetadata;
import com.thunder.rpc.provider.ServiceProvider;
import com.thunder.rpc.utils.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

public class ThunderRpcStarter implements ApplicationListener<ContextRefreshedEvent> {

    private final ServiceProvider serviceProvider;
    private final RpcProperties rpcProperties;
    private final NettyServer nettyServer;

    public ThunderRpcStarter(ServiceProvider serviceProvider, NettyServer server, RpcProperties rpcProperties) {
        this.serviceProvider = serviceProvider;
        this.rpcProperties = rpcProperties;
        this.nettyServer = server;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            registerService(context);
            nettyServer.init(rpcProperties);
        }
    }

    private void registerService(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RpcProvider.class);
        if (beanMap.size() > 0) {
            for (Map.Entry<String, Object> bean : beanMap.entrySet()) {
                Class<?> clazz = bean.getValue().getClass();
                RpcProvider rpcProvider = clazz.getAnnotation(RpcProvider.class);
                String serviceName = StringUtils.isBlank(rpcProvider.serviceName()) ? bean.getKey() : rpcProvider.serviceName();
                ServiceMetadata serviceMetadata = ServiceMetadata.builder()
                        .serviceName(serviceName)
                        .address(new InetSocketAddress(IpUtil.getLocalAddress(), rpcProperties.getServerConfig().getPort()))
                        .clazz(clazz)
                        .service(bean.getValue())
                        .build();
                serviceProvider.publishService(serviceMetadata);
            }
        }
    }
}
