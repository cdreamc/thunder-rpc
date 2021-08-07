package com.thunder.rpc.provider;

import com.thunder.rpc.model.ServiceMetadata;
import com.thunder.rpc.registery.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProvider {

    private final ServiceRegistry serviceRegistry;
    private final Map<String, ServiceMetadata> serviceMap;

    public ServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.serviceMap = new ConcurrentHashMap<>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Map.Entry<String, ServiceMetadata> service : serviceMap.entrySet()) {
                serviceRegistry.unRegisterService(service.getKey(), service.getValue());
            }
        }));
    }

    public void addService(ServiceMetadata serviceMetadata) {
        serviceMap.put(serviceMetadata.getServiceName(), serviceMetadata);
    }

    public ServiceMetadata getService(String serviceName) {
        ServiceMetadata serviceMetadata = serviceMap.get(serviceName);
        if (Objects.isNull(serviceMetadata)) {
            throw new RuntimeException("ne service " + serviceName);
        }
        return serviceMetadata;
    }

    public void publishService(ServiceMetadata serviceMetadata) {
        this.addService(serviceMetadata);
        serviceRegistry.registerService(serviceMetadata.getServiceName(), serviceMetadata);
    }
}
