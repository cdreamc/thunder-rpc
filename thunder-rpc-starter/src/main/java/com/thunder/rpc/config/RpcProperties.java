package com.thunder.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thunder.rpc")
public class RpcProperties {

    private ZkConfig zkConfig = new ZkConfig();
    private ServerConfig serverConfig = new ServerConfig();
    private String serializer;
    private String proxy;

    @Data
    public static class ZkConfig {
        private String zkUrl;
        private String rootPath;
    }

    @Data
    public static class ServerConfig {
        private int port;
    }
}
