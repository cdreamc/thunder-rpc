package com.thunder.rpc.registery.zk;

import com.thunder.rpc.registery.ServiceDiscovery;
import com.thunder.rpc.utils.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class ZookeeperDiscovery implements ServiceDiscovery {

    private final CuratorFramework zkClient;

    public ZookeeperDiscovery(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public List<InetSocketAddress> discoverService(String serviceName) {
        String servicePath = ZookeeperUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        List<InetSocketAddress> serviceUrlList = Collections.emptyList();
        try {
            List<String> urls = zkClient.getChildren().forPath(servicePath);
            serviceUrlList = zkClient.getChildren().forPath(servicePath).stream().map(serviceStr->{
                String[] socketAddressArray = serviceStr.split(":");
                if (socketAddressArray.length < 2) {
                    return null;
                }
                String host = socketAddressArray[0];
                int port = Integer.parseInt(socketAddressArray[1]);
                return new InetSocketAddress(host, port);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("get child nodes for path [{}] fail", servicePath);
        }
        return serviceUrlList;
    }

    @Override
    public InetSocketAddress getAddress(String serviceName) {
        List<InetSocketAddress> list = discoverService(serviceName);
        return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
    }
}
