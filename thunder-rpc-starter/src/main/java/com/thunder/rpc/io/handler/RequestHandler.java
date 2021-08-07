package com.thunder.rpc.io.handler;

import com.thunder.rpc.io.dto.RpcRequest;
import com.thunder.rpc.model.ServiceMetadata;
import com.thunder.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RequestHandler {
    private final ServiceProvider serviceProvider;

    public RequestHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object handle(RpcRequest request) {
        String serviceName = request.getServiceName();
        ServiceMetadata serviceMetadata = serviceProvider.getService(serviceName);
        return invokeTargetMethod(request, serviceMetadata.getService());
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", request.getServiceName(), request.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("invoke target method error");
        }
        return result;
    }


}
