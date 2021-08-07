package com.thunder.rpc.client;

import com.thunder.rpc.api.HelloDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);
        HelloTest helloTest = (HelloTest)application.getBean("helloTest");

        HelloDto helloDto = new HelloDto("lh5", "nihao");
        HelloDto helloDto1 = new HelloDto("zzy", "nihao");
        HelloDto helloDto2 = new HelloDto("yyc", "nihao");
        System.out.println(helloTest.sayHello(helloDto));
        System.out.println(helloTest.sayHello(helloDto1));
        System.out.println(helloTest.sayHello(helloDto2));
    }
}
