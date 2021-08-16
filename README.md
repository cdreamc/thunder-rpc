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
    proxy: jdk
```
<br>

* serializer：序列化方式，可选值：protostuff，kryo
* zk-config.root-path：使用 zookeeper 作为注册中心时，创建服务的存储节点的目录前缀
* zk-config.zk-url: zookeeper 注册地址
* server-config.port：netty 通信服务器使用的端口号
* proxy：代理方式，可选值：jdk，cglib
