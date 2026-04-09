# town-consumer — Netty TCP 服务器 / 消息路由中心

本模块是客户端接入的核心枢纽，通过 Netty 提供 TCP 二进制协议服务，负责接收来自客户端（或 HTTP 网关）的 Protobuf 消息，进行 Token 验证，然后路由到对应的 Dubbo 业务服务，并将响应原路返回。

- TCP 主端口：**18023**
- TCP 备用端口：**18026**
- 启动类：`TownConsumerApplication`

---

## 目录结构

```
town-consumer/
└── src/main/java/demo/
    ├── TownConsumerApplication.java           # 启动类
    ├── NettyServerBoot.java                   # Netty 服务启动器
    ├── aspect/
    │   └── DubboExceptionAspect.java          # Dubbo 异常 AOP 切面
    ├── manager/
    │   └── ServiceManager.java                # Dubbo 服务引用聚合
    ├── netty/
    │   ├── NettyConfig.java                   # Netty Bean 配置
    │   ├── NettyProperties.java               # Netty 参数配置类
    │   ├── NettyServerHandler.java            # Channel Pipeline 初始化
    │   └── handler/
    │       ├── in/
    │       │   ├── ServerListenerHandler.java  # 连接生命周期监听
    │       │   ├── TokenLoginInHandler.java    # JWT Token 验证
    │       │   └── TaskDistributeInHandler.java # 消息路由分发
    │       └── util/
    │           ├── MsgHandler.java             # 消息处理器函数式接口
    │           └── RespBodyParsers.java        # 响应体反序列化工具
    └── transfer/
        └── RequestTransfer.java               # Dubbo 调用代理层
```

---

## 文件说明

### 启动相关

| 文件 | 说明 |
|------|------|
| `TownConsumerApplication.java` | Spring Boot 启动类。本模块作为 Dubbo Consumer（不对外暴露服务），通过 `@DubboReference` 注入各业务 Provider 的接口代理 |
| `NettyServerBoot.java` | Netty TCP 服务启动器，标注 `@Component`。`@PostConstruct` 时调用 `serverBootstrap.bind(port).sync()` 启动主端口（18023）和备用端口（18026）；`@PreDestroy` 时优雅关闭两个 EventLoopGroup |

---

### aspect/ — AOP 切面

| 文件 | 说明 |
|------|------|
| `DubboExceptionAspect.java` | 全局 Dubbo 异常捕获切面，切入点为 `RequestTransfer` 的所有返回 `ResponseMsg` 的公共方法。捕获 `RpcException`（服务不可达）→ 返回 `TRC_SERVICE_UNAVAILABLE`；捕获其他异常 → 返回 `TRC_ERR`。防止 Dubbo 调用异常穿透到 Netty 层导致连接异常 |

---

### manager/ — 服务聚合

| 文件 | 说明 |
|------|------|
| `ServiceManager.java` | Dubbo 服务引用的聚合容器，持有七个业务服务的引用：`userService`（`IUserService`）、`daoService`（`IDaoService`）、`noticeService`（`INoticeService`）、`updateService`（`IUpdateService`）、`questionService`（`IQuestionService`）、`studyService`（`IStudyService`）、`peopleService`（`IPeopleService`）。各 handler 和 transfer 通过注入 `ServiceManager` 来访问所有服务 |

---

### netty/ — Netty 核心配置

| 文件 | 说明 |
|------|------|
| `NettyConfig.java` | Spring `@Configuration` 类，创建三个 Bean：① `boosGroup`（`NioEventLoopGroup`，处理 Accept 连接，线程数来自配置）；② `workerGroup`（`NioEventLoopGroup`，处理 I/O 读写）；③ `serverBootstrap`（`ServerBootstrap`，绑定两个 EventLoopGroup，使用 `NioServerSocketChannel`，设置连接超时，指定 `NettyServerHandler` 作为 `ChildHandler`） |
| `NettyProperties.java` | Netty 配置参数类，绑定 `application.yml` 中 `netty.*` 前缀的配置项：`boss`（Boss 线程数，默认 4）、`worker`（Worker 线程数，默认 2）、`timeout`（连接超时毫秒）、`port`（主端口 18023）、`portSalve`（备用端口 18026）、`host`（服务地址） |
| `NettyServerHandler.java` | `ChannelInitializer<SocketChannel>` 实现，负责为每个新建连接初始化 Pipeline。按顺序注册的 Handler（见下方流水线说明） |

**Netty Pipeline 流水线（每个连接）：**

```
客户端入站方向 →
  [logging]            LoggingHandler(DEBUG)          调试日志
  [frameDecoder]       LengthFieldBasedFrameDecoder    TCP 粘包拆包（最大 1MB，4字节长度头）
  [protobufDecoder]    ProtobufDecoder(RequestMsg)     字节流 → RequestMsg 对象
  [serverHandler]      ServerListenerHandler           连接生命周期日志
  [tokenHandler]       TokenLoginInHandler             JWT Token 验证
  [distributeHandler]  TaskDistributeInHandler         消息类型路由到业务服务
                                               ↓ (ctx.writeAndFlush)
客户端出站方向 ←
  [frameEncoder]       LengthFieldPrepender(4)         添加 4 字节长度头
  [protobufEncoder]    ProtobufEncoder                 ResponseMsg → 字节流
```

---

### netty/handler/in/ — 入站处理器

| 文件 | 说明 |
|------|------|
| `ServerListenerHandler.java` | 连接生命周期监听器。`handlerAdded`：打印新连接 ID 日志；`channelRead0`：直接 `fireChannelRead` 传递给下一个 Handler；`handlerRemoved`：打印设备下线日志；`exceptionCaught`：打印异常并关闭连接 |
| `TokenLoginInHandler.java` | JWT Token 验证处理器。逻辑：① Token 为空且消息类型不是 Login/Register → 返回 `TRC_TOKEN_NOT_EXIST`；② Token 非空 → 解析 JWT → 解析失败返回 `TRC_TOKEN_INVALID`；③ 到 Redis 验证 Token（`Redis_Prefix_Token + userTel`）→ 不存在或与 JWT 内容不一致返回 `TRC_TOKEN_INVALID`；④ 通过校验 → `ctx.fireChannelRead(msg)` 传给下一个 Handler |
| `TaskDistributeInHandler.java` | 核心消息路由分发器。初始化时（`init()`）将 26 种 `MsgType` 注册到 `EnumMap<MsgType, MsgHandler>` 中，每个 Handler 是一个 lambda：反序列化请求体 → 调用 `RequestTransfer` 对应方法 → 得到 `ResponseMsg`。`channelRead0` 中：按 `msg.getMsgType()` 查 handlerMap → 执行对应 MsgHandler → 将响应写回客户端；处理 `InvalidProtocolBufferException`（协议解析失败）和其他异常 |

**`TaskDistributeInHandler` 支持的消息类型：**

| 分类 | 消息类型 | Token 模式 |
|------|---------|-----------|
| 用户 | LoginReq、RegisterReq | `noTokenHandle`（无 Token） |
| 用户 | UpdateUserInfoReq、ListUserInfoReq、ListNotifyUserInfoReq | `handle`（含 Token） |
| 公告 | ListNoticeReq | `noTokenHandle` |
| 公告 | CreateNoticeReq、UpdateNoticeReq、SetNoticeReadReq、ListNoticeReadReq | `handle` |
| 审计 | ListUpdateInfoReq | `handle` |
| 问题 | CreateQuestionReq、UpdateQuestionReq、ListQuestionReq | `handle` |
| 学习 | CreateStudyReq、UpdateStudyReq、ListStudyReq、StarStudyReq、ListUserStarStudyReq | `handle` |
| 家庭 | CreatePeopleReq、UpdatePeopleReq、ListPeopleInfoReq、CreatePeopleUpdateApplyReq、ListPeopleUpdateApplyReq、DelPeopleUpdateApplyReq | `handle` |

---

### netty/handler/util/ — 工具类

| 文件 | 说明 |
|------|------|
| `MsgHandler.java` | `@FunctionalInterface`，定义消息处理器签名：`ResponseMsg handle(RequestMsg msg, ChannelHandlerContext ctx) throws Exception`。被 `TaskDistributeInHandler` 中的 `EnumMap` 使用，每个消息类型对应一个 lambda 实现 |
| `RespBodyParsers.java` | 响应体反序列化工具，将 `ResponseMsg.msg` 字段（`bytes`）解析为对应的 Protobuf 消息对象，用于日志打印。按 `msgType` 分 25 种路由，解析失败返回 null（仅用于调试日志，不影响实际响应） |

---

### transfer/ — Dubbo 调用代理

| 文件 | 说明 |
|------|------|
| `RequestTransfer.java` | Dubbo 服务调用的统一代理层，注入 `ServiceManager`，将各消息类型的处理请求转发到对应的 Dubbo 服务方法。每个方法仅一行：`serviceManager.xxxService.xxxMethod(params)`。所有方法被 `DubboExceptionAspect` 统一拦截异常。包含 26 个方法，覆盖所有消息类型 |

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Netty 参数配置（boss/worker 线程数、端口）、Dubbo Consumer 配置（不注册自身、从 Nacos 发现服务） |
| `log4j2.xml` | 日志配置 |

---

## 测试文件

| 文件 | 说明 |
|------|------|
| `src/test/java/client/NettyTest.java` | Netty TCP 客户端测试，直接连接 18023 端口发送 Protobuf 二进制消息，用于本地联调验证 |