# 村镇数字化服务平台 (Town Digital Service Platform)

基于微服务架构的农村数字化管理平台，为村民与村干部提供公告通知、问题上报、学习资料、家庭信息管理等一站式数字化服务。

---

## 目录

- [项目架构](#项目架构)
- [技术栈](#技术栈)
- [模块说明](#模块说明)
- [核心数据模型](#核心数据模型)
- [通信协议](#通信协议)
- [服务配置](#服务配置)
- [部署说明](#部署说明)
- [请求流程示例](#请求流程示例)

---

## 项目架构

```
┌─────────────────────────────────────────────────────────────┐
│                    客户端 (移动端 / Web)                      │
└──────────────────┬──────────────────────────────────────────┘
                   │ 二进制 Protobuf 协议
       ┌───────────┴────────────┐
       ▼                        ▼
┌─────────────────┐     ┌──────────────────┐
│  HTTP 网关       │     │  Netty TCP 服务器  │
│  port: 48080    │     │  port: 18023      │
│ /gateway/forward│     │                  │
└────────┬────────┘     └────────┬─────────┘
         └──────────┬────────────┘
                    ▼
            ┌───────────────┐
            │ town-consumer │  消息路由中心
            └───────┬───────┘
                    │ Dubbo RPC
     ┌──────────────┼──────────────────┐
     ▼              ▼                  ▼
┌─────────┐  ┌──────────┐       ┌──────────────┐
│  user   │  │  notice  │  ...  │    people    │
│-provider│  │-provider │       │   -provider  │
└─────────┘  └──────────┘       └──────────────┘
     │                                │
     └──────────────┬─────────────────┘
                    ▼
            ┌───────────────┐
            │   town-dao    │  统一数据访问层
            └───────┬───────┘
          ┌─────────┴──────────┐
          ▼                    ▼
    ┌──────────┐         ┌──────────┐
    │  MySQL   │         │  Redis   │
    └──────────┘         └──────────┘
```

**整体分层：**

| 层次 | 组件 |
|------|------|
| 接入层 | HTTP Gateway (REST) + Netty TCP Server |
| 消息路由层 | town-consumer (消息分发) |
| 业务服务层 | 6 个业务 Provider (用户/公告/问题/学习/人口/更新) |
| 数据访问层 | town-dao (MyBatis + Redis) |
| 存储层 | MySQL + Redis |
| 服务注册发现 | Nacos |

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 构建 | Maven | - |
| 核心框架 | Spring Boot | 3.2.3 |
| RPC 框架 | Apache Dubbo | 3.3.1 |
| 服务注册 | Nacos | - |
| 序列化协议 | Protocol Buffers | 4.33.2 |
| 网络框架 | Netty | 4.2.7 |
| ORM | MyBatis | 3.0.3 |
| 分页 | PageHelper | 5.3.3 |
| 数据库 | MySQL | - |
| 缓存 | Redis | - |
| 认证 | JWT (JJWT) | 0.11.5 |
| Bean 映射 | MapStruct | 1.5.5 |
| 代码简化 | Lombok | - |
| 日志 | Log4j2 + SLF4j | - |

---

## 模块说明

### town-api — 公共 API 与数据模型

所有模块的共享库，包含：

- **实体类** (`entity/`)：`UserInfoDO`、`NoticeInfoDO`、`QuestionInfoDO`、`StudyInfoDO`、`PeopleInfoDO`、`UpdateInfoDO` 等
- **服务接口** (`townInterface/`)：`IDaoService`、`IUserService`、`INoticeService`、`IQuestionService`、`IStudyService`、`IPeopleService`、`IUpdateService`
- **Proto 定义** (`proto/source/`)：`base.proto`（枚举/基础类型）、`entity.proto`（领域对象）、`protocol.proto`（请求/响应协议）
- **工具类**：JWT 工具、随机数、时间工具、`BizException`

---

### town-dao — 统一数据访问层

Dubbo 端口：**40002**

集中管理所有数据库和缓存操作，由其他所有业务 Provider 通过 Dubbo 调用。

- **DaoManager**：实现 `IDaoService`，注入 14 个 MyBatis Mapper
- **MapStruct 转换**：Protobuf ↔ 数据库实体双向转换
- **Redis 集成**：JWT Token 存储与过期管理
- **分页**：PageHelper 透明分页

---

### town-user-provider — 用户服务

Dubbo 端口：**40003**

| 功能 | 描述 |
|------|------|
| 登录 | 校验密码，生成 JWT Token 并存入 Redis |
| 注册 | 创建新村民账户 |
| 更新用户信息 | 仅管理员可操作，触发变更审计日志 |
| 查询用户列表 | 村民只能查自己，村干部可查所有 |
| 推送/查询通知 | 用户通知消息管理 |

用户角色：
- `TUP_CM`（0）= 普通村民
- `TUP_CGM`（1）= 村干部/管理员

用户状态：`Normal`（正常）/ `Frozen`（冻结）/ `Flagged`（标记）

---

### town-notice-provider — 公告通知服务

Dubbo 端口：**40004**

| 功能 | 描述 |
|------|------|
| 发布公告 | 仅管理员，支持置顶、附件 |
| 修改/删除公告 | 仅管理员 |
| 查询公告列表 | 支持分页 |
| 确认已读 | 支持"需要确认"类公告的已读状态记录 |
| 查询已读列表 | 获取用户已读公告 ID 列表 |

公告类型：通知公告 / 招募公告 / 活动公告 / 政务公开 / 公共事务

---

### town-question-provider — 问题上报服务

Dubbo 端口：**40005**

| 功能 | 描述 |
|------|------|
| 提交问题 | 村民上报问题，支持图片附件 |
| 更新问题 | 村民可修改待处理问题；管理员可指派处理人、变更状态 |
| 查询问题列表 | 村民只见自己的；管理员可见未指派或指派给自己的 |

问题处理流程：`待处理 → 处理中 → 已解决`（或`已关闭/无法解决`）

问题分类：环境卫生 / 邻里纠纷 / 基础设施 / 干部行为

---

### town-study-provider — 学习资料服务

Dubbo 端口：**40006**

| 功能 | 描述 |
|------|------|
| 发布学习资料 | 仅管理员，支持置顶 |
| 修改/删除资料 | 仅管理员 |
| 查询资料列表 | 支持分页，自动累加阅读量 |
| 收藏/取消收藏 | 用户收藏管理 |
| 查询我的收藏 | 获取用户收藏列表 |

资料分类：政策解读 / 农技推广 / 健康养生 / 法律常识

---

### town-people-provider — 人口家庭信息服务

Dubbo 端口：**40007**

| 功能 | 描述 |
|------|------|
| 新增家庭成员 | 仅管理员，支持批量导入 |
| 修改家庭成员信息 | 仅管理员 |
| 查询家庭信息 | 村民只见本户；管理员可见全部 |
| 提交信息变更申请 | 村民申请修改自己的家庭信息 |
| 查询变更申请 | 获取待审批申请列表 |
| 审批/拒绝申请 | 管理员审核操作 |

家庭成员以身份证号为主键，按户号（`peopleHouseId`）分组。

---

### town-update-provider — 变更审计日志服务

Dubbo 端口：**40008**

记录所有关键数据的变更历史：

- 变更对象类型：用户 / 公告 / 问题 / 学习资料 / 人口信息
- 以 Protobuf 二进制格式存储变更前/后快照
- 其他业务服务对此服务的依赖为**可选**（配置了 Mock 降级），服务不可用时不影响主链路

---

### town-consumer — Netty TCP 服务器 / 消息路由中心

TCP 端口：**18023**（主）/ **18026**（备）

作为所有客户端的接入点，接收二进制协议消息并路由到对应服务：

- **LengthFieldBasedFrameDecoder**：处理 TCP 粘包（最大消息 1MB）
- **ProtobufDecoder**：将字节流解码为 `RequestMsg`
- **TokenLoginInHandler**：JWT Token 验证
- **TaskDistributeInHandler**：根据 `msgType` 路由到对应服务方法
- **ProtobufEncoder / LengthFieldPrepender**：编码响应

核心组件：
- `ServiceManager`：持有所有 Dubbo 服务引用
- `RequestTransfer`：115 个路由方法，将消息类型映射到服务调用
- `RespBodyParsers`：25 种响应体反序列化映射

---

### town-http-gateway — HTTP 转 TCP 网关

HTTP 端口：**48080**

为不支持直连 TCP 的客户端（如 Web 浏览器）提供 REST 接入：

```
POST /gateway/forward
Content-Type: application/octet-stream
Body: 二进制 Protobuf 编码的 RequestMsg
```

内部通过 `NettyTcpClient` 将请求转发至 Netty TCP Server，再将响应原样返回给 HTTP 客户端。

---

## 核心数据模型

### 用户 (UserInfo)

| 字段 | 类型 | 说明 |
|------|------|------|
| userTel | long | 手机号（主键） |
| userName | string | 姓名 |
| userPwd | string | 密码 |
| userTown | string | 所属村庄 |
| userPower | enum | 角色（村民/干部） |
| flagType | enum | 账户状态 |
| userCreateTime | long | 注册时间（毫秒） |

### 公告 (NoticeInfo)

| 字段 | 类型 | 说明 |
|------|------|------|
| noticeId | int | 自增主键 |
| noticeType | enum | 公告类型 |
| noticeTitle | string | 标题 |
| noticeContext | string | 正文 |
| isTop | bool | 是否置顶 |
| isAcceptRead | bool | 是否需要确认已读 |
| noticeAtt | bytes | 二进制附件 |
| writerTel | long | 发布人手机号 |

### 问题上报 (QuestionInfo)

| 字段 | 类型 | 说明 |
|------|------|------|
| questionId | int | 自增主键 |
| questionType | enum | 问题分类 |
| questionCtx | string | 问题描述 |
| questPhoto | bytes | 图片附件 |
| nodeType | enum | 处理状态 |
| choiceUser | long | 指派处理人 |

### 学习资料 (StudyInfo)

| 字段 | 类型 | 说明 |
|------|------|------|
| studyId | int | 自增主键 |
| studyType | enum | 资料分类 |
| studyTitle | string | 标题 |
| studyTip | string | 摘要 |
| studyContent | string | 全文 |
| isTop | bool | 是否首页推荐 |
| readCount | int | 阅读量 |

### 变更审计 (UpdateInfo)

| 字段 | 类型 | 说明 |
|------|------|------|
| updateId | int | 自增主键 |
| infoId | long | 被修改实体 ID |
| infoType | enum | 实体类型 |
| beforeMsg | bytes | 变更前 Protobuf 快照 |
| afterMsg | bytes | 变更后 Protobuf 快照 |
| updateTime | long | 变更时间 |
| updateUserTel | long | 操作人手机号 |

---

## 通信协议

所有客户端与服务端通信均使用统一的 Protobuf 二进制协议：

```protobuf
// 请求包装
message RequestMsg {
  TownMsgType msgType = 1;   // 消息类型（55 种）
  string token = 2;           // JWT Token
  bytes msg = 3;              // 序列化的请求体
}

// 响应包装
message ResponseMsg {
  TownRespCode errCode = 1;   // 响应码（31 种）
  TownMsgType msgType = 2;    // 对应消息类型
  bytes msg = 3;              // 序列化的响应体
}
```

### 主要消息类型（共 55 种）

| 领域 | 请求类型示例 |
|------|-------------|
| 用户 | TMT_LoginReq/Rsp、TMT_RegisterReq/Rsp |
| 公告 | TMT_CreateNoticeReq/Rsp、TMT_ListNoticeReq/Rsp |
| 问题 | TMT_CreateQuestionReq/Rsp、TMT_UpdateQuestionReq/Rsp |
| 学习 | TMT_CreateStudyReq/Rsp、TMT_StarStudyReq/Rsp |
| 家庭 | TMT_CreatePeopleReq/Rsp、TMT_ListPeopleInfoReq/Rsp |
| 审计 | TMT_ListUpdateInfoReq/Rsp |
| 通知 | TMT_ListNotifyUserInfoReq/Rsp |

### 核心响应码

| 码值 | 常量 | 含义 |
|------|------|------|
| 0 | TRC_OK | 成功 |
| 1 | TRC_ERR | 通用错误 |
| 2 | TRC_TOKEN_NOT_EXIST | Token 缺失 |
| 3 | TRC_TOKEN_INVALID | Token 无效/过期 |
| 4 | TRC_PARSE_PROTOCOL_ERR | 协议解析失败 |
| 100 | TRC_DB_DATA_NOT_FOUND | 数据不存在 |
| 204 | TRC_USER_POWER_NOT_ENOUGH | 权限不足 |

---

## 服务配置

### 基础设施地址

| 服务 | 地址 |
|------|------|
| MySQL | `43.142.255.18:3306` / 数据库：`town` |
| Redis | `43.142.255.18:6379` |
| Nacos | `43.142.255.18:8848` / 分组：`town-test` |

### Dubbo 服务端口

| 模块 | Dubbo 端口 |
|------|-----------|
| town-dao | 40002 |
| town-user-provider | 40003 |
| town-notice-provider | 40004 |
| town-question-provider | 40005 |
| town-study-provider | 40006 |
| town-people-provider | 40007 |
| town-update-provider | 40008 |

### 接入层端口

| 组件 | 端口 |
|------|------|
| Netty TCP Server（主） | 18023 |
| Netty TCP Server（备） | 18026 |
| HTTP Gateway | 48080 |

---

## 部署说明

### 依赖项

启动前请确保以下服务已就绪：

1. **MySQL** - 创建 `town` 数据库并执行表结构脚本
2. **Redis** - 无需特殊配置，默认 0 号库
3. **Nacos** - 服务注册中心，默认端口 8848

### 启动顺序

```
1. town-dao-provider        # 数据访问层，其他服务依赖它
2. town-update-provider     # 审计日志服务（可选，其他服务有降级 Mock）
3. town-user-provider
   town-notice-provider     # 以下各服务可并行启动
   town-question-provider
   town-study-provider
   town-people-provider
4. town-consumer            # 消息路由中心，需要所有 Provider 就绪
5. town-http-gateway        # HTTP 网关，需要 town-consumer 就绪
```

### 构建

```bash
# 根目录构建所有模块
mvn clean package -DskipTests

# 单独构建某个模块
mvn clean package -pl town-consumer -am -DskipTests
```

---

## 请求流程示例

以**村民登录**为例：

```
1. 客户端构造 RequestMsg:
   - msgType = TMT_LoginReq
   - token = ""（登录前无 Token）
   - msg = LoginReq { userTel, userPwd }

2. 发送至 HTTP Gateway (POST /gateway/forward) 或直连 Netty (18023)

3. town-consumer 接收:
   - TokenLoginInHandler 跳过（登录不需要 Token 验证）
   - TaskDistributeInHandler 路由至 RequestTransfer.login()

4. 通过 Dubbo 调用 town-user-provider:
   - 查询 MySQL 校验用户名密码
   - 生成 JWT Token
   - 将 Token 存入 Redis（带 TTL）

5. 返回 ResponseMsg:
   - errCode = TRC_OK
   - msgType = TMT_LoginRsp
   - msg = LoginRsp { token, userInfo }
```

---

## 架构设计亮点

- **统一数据访问层**：所有业务服务通过 Dubbo 调用同一个 `town-dao`，避免各服务直连数据库，便于统一管控和优化
- **二进制协议**：基于 Protobuf 的自定义二进制协议，相比 JSON 显著降低带宽消耗，适合弱网环境
- **可选依赖降级**：`town-update-provider` 对业务服务为可选依赖，配置了 Mock 实现，审计服务不可用时主链路不受影响
- **完整审计日志**：每次关键数据变更均记录变更前后的 Protobuf 快照，支持历史追溯
- **双端接入**：同时支持 TCP 直连（移动端）和 HTTP 转发（Web 端），客户端适配灵活
- **RBAC 权限控制**：村民与村干部角色细粒度权限管控，关键操作均在服务层校验

---

*本项目为学习与实践性项目，展示了微服务架构在基层政务信息化场景中的应用。*