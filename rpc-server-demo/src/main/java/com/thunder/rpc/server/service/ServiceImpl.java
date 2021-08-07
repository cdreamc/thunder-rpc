package com.thunder.rpc.server.service;

import com.thunder.rpc.annotation.RpcProvider;
import com.thunder.rpc.api.HelloDto;

@RpcProvider(serviceName = "HelloService", version = "1.0")
public class ServiceImpl implements HelloService {

    @Override
    public String hello(HelloDto helloDto) {
        return "server返回：" + helloDto.getName() +"," + helloDto.getGreeting();
    }
}
