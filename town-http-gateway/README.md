# town-http-gateway — HTTP 转 TCP 网关

本模块为不支持直连 TCP 协议的客户端（如 Web 浏览器、HTTP 调试工具）提供 REST 接入能力。将接收到的二进制 HTTP 请求体，通过 Netty TCP 客户端原样转发到 `town-consumer` 的 TCP 服务器，再将响应原样返回给 HTTP 调用方。

- HTTP 端口：**48080**
- 启动类：`GatewayApplication`

---

## 目录结构

```
town-http-gateway/
└── src/main/java/gateway/
    ├── GatewayApplication.java          # 启动类
    ├── config/
    │   └── NettyClientConfig.java       # TCP 客户端配置参数
    ├── controller/
    │   └── BinaryGatewayController.java # REST 入口
    ├── service/
    │   └── BinaryForwardService.java    # 转发服务层
    └── tcp/
        ├── NettyTcpClient.java          # Netty TCP 客户端
        └── NettyTcpClientHandler.java   # TCP 响应接收 Handler
```

---

## 文件说明

### `GatewayApplication.java`

Spring Boot 启动类，启动 HTTP 服务（端口 48080）。本模块**不依赖 Dubbo**，仅通过 TCP 转发与 `town-consumer` 通信，因此不需要注册到 Nacos。

---

### config/

| 文件 | 说明 |
|------|------|
| `NettyClientConfig.java` | TCP 客户端配置类，绑定 `application.yml` 中 `netty.client.*` 前缀的配置项：`host`（town-consumer 地址）、`port`（18023）、`connectTimeout`（连接超时毫秒，默认 3000）、`readTimeout`（等待响应超时秒数，默认 5）。由 `NettyTcpClient` 注入使用 |

---

### controller/

| 文件 | 说明 |
|------|------|
| `BinaryGatewayController.java` | REST 控制器，映射 `POST /gateway/forward`。请求和响应的 `Content-Type` 均为 `application/octet-stream`（原始二进制流）。接收 `@RequestBody byte[] body`（客户端发送的 Protobuf 编码的 `RequestMsg`），调用 `BinaryForwardService.forward(body)`，返回 `byte[]`（Protobuf 编码的 `ResponseMsg`）。客户端需要自己负责 Protobuf 的序列化和反序列化 |

---

### service/

| 文件 | 说明 |
|------|------|
| `BinaryForwardService.java` | 转发服务层，注入 `NettyTcpClient`，`forward(byte[])` 方法直接调用 `tcpClient.sendAndReceive(data)` 并返回结果。作为 Controller 和 TCP 客户端之间的薄服务层，便于未来扩展（如限流、鉴权等） |

---

### tcp/

| 文件 | 说明 |
|------|------|
| `NettyTcpClient.java` | Netty TCP 客户端，标注 `@Component`。在构造函数中初始化 `Bootstrap`（使用 `NioEventLoopGroup`、`NioSocketChannel`，开启 `TCP_NODELAY`），Pipeline 中注册 `LengthFieldPrepender(4)`（添加4字节长度头）和 `LengthFieldBasedFrameDecoder`（与 town-consumer 的帧格式一致）以及 `NettyTcpClientHandler`。`sendAndReceive(byte[] data)` 方法：每次请求建立新连接 → 发送数据 → 阻塞等待响应（`CountDownLatch`）→ 关闭连接 → 返回响应字节 |
| `NettyTcpClientHandler.java` | Netty 入站 Handler，处理来自 TCP 服务器的响应。使用 `CountDownLatch(1)` 实现同步等待：`channelRead0` 读取响应字节写入 `response` 字段并 `countDown()`；`getResponse(timeoutSeconds)` 阻塞等待 latch，超时则抛出异常 |

**转发完整流程：**

```
客户端 HTTP POST /gateway/forward
    Body: Protobuf 编码的 RequestMsg（二进制）
         ↓
BinaryGatewayController.forward(byte[])
         ↓
BinaryForwardService.forward(byte[])
         ↓
NettyTcpClient.sendAndReceive(byte[])
    1. 建立 TCP 连接到 town-consumer:18023
    2. Pipeline 添加 LengthFieldPrepender，自动在数据前加 4 字节长度头
    3. 发送数据：channel.writeAndFlush(Unpooled.wrappedBuffer(data))
    4. 阻塞等待 NettyTcpClientHandler.getResponse(timeout)
    5. town-consumer 处理后返回响应帧
    6. LengthFieldBasedFrameDecoder 解析帧，去掉长度头
    7. NettyTcpClientHandler.channelRead0 读取响应字节
    8. channel.close() 关闭连接
    9. 返回响应字节
         ↓
HTTP 响应 200 OK
    Body: Protobuf 编码的 ResponseMsg（二进制）
```

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | HTTP 端口（48080）、TCP 客户端配置（town-consumer 地址和端口、超时时间） |

---

## 测试文件

| 文件 | 说明 |
|------|------|
| `src/test/java/client/BinaryGatewayControllerTest.java` | HTTP 网关集成测试，使用 `TestRestTemplate` 向 `/gateway/forward` 发送 Protobuf 编码的请求，验证整个 HTTP → TCP → 业务服务的完整链路 |