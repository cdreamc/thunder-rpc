package com.thunder.rpc.config;

import com.thunder.rpc.io.client.NettyClient;
import com.thunder.rpc.io.server.NettyServer;
import com.thunder.rpc.provider.ServiceProvider;
import com.thunder.rpc.registery.ServiceDiscovery;
import com.thunder.rpc.registery.ServiceRegistry;
import com.thunder.rpc.registery.zk.ZookeeperDiscovery;
import com.thunder.rpc.registery.zk.ZookeeperRegistry;
import com.thunder.rpc.spring.ThunderClientProcessor;
import com.thunder.rpc.spring.ThunderRpcStarter;
import com.thunder.rpc.utils.ZookeeperUtil;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(value = RpcProperties.class)
@Configuration
public class RpcAutoConfiguration {

    @Bean
    public ThunderRpcStarter initServerStarter(RpcProperties rpcProperties) {

        // 先默认是 zk
        CuratorFramework zkClient = ZookeeperUtil.getZkClient(rpcProperties.getZkConfig());
        ServiceRegistry serviceRegistry = new ZookeeperRegistry(zkClient);
        ServiceProvider serviceProvider = new ServiceProvider(serviceRegistry);
        NettyServer nettyServer = new NettyServer(serviceProvider);
        return new ThunderRpcStarter(serviceProvider, nettyServer, rpcProperties);
    }

    @Bean
    public ThunderClientProcessor initClientStarter(RpcProperties rpcProperties) {
        // 先默认是 zk
        CuratorFramework zkClient = ZookeeperUtil.getZkClient(rpcProperties.getZkConfig());
        ServiceDiscovery serviceDiscovery = new ZookeeperDiscovery(zkClient);
        NettyClient nettyClient = new NettyClient(serviceDiscovery);
        return new ThunderClientProcessor(nettyClient, rpcProperties);
    }
}
