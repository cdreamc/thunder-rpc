package com.thunder.rpc.registery.zk;

import com.thunder.rpc.model.ServiceMetadata;
import com.thunder.rpc.registery.ServiceRegistry;
import com.thunder.rpc.utils.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ZookeeperRegistry implements ServiceRegistry {

    private final CuratorFramework zkClient;

    public ZookeeperRegistry(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String serviceName, ServiceMetadata serviceMetadata) {
        String servicePath = ZookeeperUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        try {
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create().creatingParentsIfNeeded().forPath(servicePath);
            }
            String ipPath = servicePath + serviceMetadata.getAddress().toString();
            if (zkClient.checkExists().forPath(ipPath) == null) {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ipPath, serviceMetadata.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("create service node [{}] fail", serviceMetadata.getAddress(), e);
        }
    }

    @Override
    public void unRegisterService(String serviceName, ServiceMetadata serviceMetadata) {
        String ipPath = ZookeeperUtil.ZK_REGISTER_ROOT_PATH + "/" + serviceName + serviceMetadata.getAddress().toString();
        try {
            zkClient.delete().forPath(ipPath);
        } catch (Exception e) {
            log.error("delete service node [{}] fail", serviceMetadata.getAddress(), e);
        }
    }
}
