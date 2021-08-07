package com.thunder.rpc.utils;

import com.thunder.rpc.config.RpcProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryOneTime;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZookeeperUtil {

    public static final String ZK_REGISTER_ROOT_PATH = "/thunder-rpc";
    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;

    public static CuratorFramework getZkClient(RpcProperties.ZkConfig zkConfig) {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        zkClient = CuratorFrameworkFactory.builder()
                .connectString((Objects.isNull(zkConfig) || StringUtils.isBlank(zkConfig.getZkUrl())) ? DEFAULT_ZK_ADDRESS : zkConfig.getZkUrl())
                .retryPolicy(new RetryOneTime(3000))
                .build();

        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }
}
