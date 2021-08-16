# thunder-rpc 一款基于 Springboot + Netty 实现的 RPC 框架

## 使用方法
### 1.引入 thunder-rpc 包
>        <dependency>
>            <groupId>com.example</groupId>
>            <artifactId>thunder-rpc-starter</artifactId>
>            <version>${project.version}</version>
>            <scope>compile</scope>
>        </dependency>

### 2.配置
#### 2.1 客户端
1. 在 resource 目录下新建 application.yml 文件
```
thunder:
  rpc:
    serializer: protostuff
    zk-config:
      root-path: /thunder-rpc
      zk-url: 127.0.0.1:2181
    server-config:
      port: 9999
    proxy: jdk
```
<br>

* serializer：序列化方式，可选值：protostuff，kryo
* zk-config.root-path：使用 zookeeper 作为注册中心时，创建服务的存储节点的目录前缀
* zk-config.zk-url: zookeeper 注册地址
* server-config.port：netty 通信服务器使用的端口号
* proxy：代理方式，可选值：jdk，cglib

#### 2.2 服务端
在 resource 目录下新建 application.yml 文件
```
thunder:
  rpc:
    serializer: protostuff
    zk-config:
      root-path: /thunder-rpc
      zk-url: 127.0.0.1:2181
    server-config:
      port: 9999
```
具体含义同客户端


### 3.使用注解
#### 3.1 服务端
示例代码：
```
@RpcProvider(serviceName = "HelloService", version = "1.0")
public class ServiceImpl implements HelloService {

    @Override
    public String hello(HelloDto helloDto) {
        return "server返回：" + helloDto.getName() +"," + helloDto.getGreeting();
    }
}
```
使用 RpcProvider 注解对应的类即可，即可自动实现注册服务到注册中心

#### 3.2 客户端
示例代码：
```
@Component
public class HelloTest {

    @RpcConsumer(serviceName = "HelloService", version = "1.0")
    private HelloService helloService;

    public String sayHello(HelloDto helloDto) {
        return helloService.hello(helloDto);
    }
}
```
使用 RpcConsumer 注解修饰对应的成员变量，注意 serviceName 需要和 服务端一致
