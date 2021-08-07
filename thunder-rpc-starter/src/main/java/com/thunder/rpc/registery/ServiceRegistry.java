package com.thunder.rpc.registery;

import com.thunder.rpc.model.ServiceMetadata;

public interface ServiceRegistry {

    /**
     * register service
     *
     * @param serviceName service name
     * @param serviceMetadata service metadata
     */
    void registerService(String serviceName, ServiceMetadata serviceMetadata);

    void unRegisterService(String serviceName, ServiceMetadata serviceMetadata);
}
