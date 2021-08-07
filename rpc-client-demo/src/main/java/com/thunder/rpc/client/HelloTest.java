package com.thunder.rpc.client;

import com.thunder.rpc.annotation.RpcConsumer;
import com.thunder.rpc.api.HelloDto;
import com.thunder.rpc.api.HelloService;
import org.springframework.stereotype.Component;

@Component
public class HelloTest {

    @RpcConsumer(serviceName = "HelloService", version = "1.0")
    private HelloService helloService;

    public String sayHello(HelloDto helloDto) {
        return helloService.hello(helloDto);
    }
}
