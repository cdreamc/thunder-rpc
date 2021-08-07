package com.thunder.rpc.registery;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServiceDiscovery {
    List<InetSocketAddress> discoverService(String serviceName);

    InetSocketAddress getAddress(String serviceName);
}
