# town-update-provider — 变更审计日志服务

负责记录系统中所有关键数据的变更历史，为管理员提供完整的操作审计追踪能力。以 Dubbo Provider 形式注册到 Nacos，被各业务 Provider 通过 Dubbo 调用（**可选依赖**，其他服务配置了 Mock 降级）。

- Dubbo 服务端口：**40008**
- 启动类：`UpdateProviderApplication`

---

## 目录结构

```
town-update-provider/
└── src/main/java/provider/
    ├── UpdateProviderApplication.java  # 启动类
    └── impl/
        └── UpdateServiceImpl.java      # 审计日志服务实现
```

---

## 文件说明

### `UpdateProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `UpdateServiceImpl` 作为 `IUpdateService` 的实现通过 Nacos 注册，监听 Dubbo 端口 40008。

---

### `UpdateServiceImpl.java`

`IUpdateService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`。

**注意：** `updateService()` 方法返回 `null`，意味着本服务自身**不会再写审计日志**，避免递归调用。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库操作 |

**业务方法：**

| 方法 | 调用方 | 权限 | 业务逻辑 |
|------|-------|------|---------|
| `addUpdateInfo(UpdateInfoDO)` | 各业务 Provider（内部调用） | 无权限校验（内部 RPC） | 直接调用 `daoService.update_insert`，写入一条审计记录；返回 `TRC_OK` 或 `TRC_ERR` |
| `listUpdateInfo(token, ListUpdateInfoReq)` | `town-consumer` → `RequestTransfer` | 需要村干部权限（由 `AbstractRpcService` 校验） | `updateId=0` 时分页查全部；`updateId>0` 时查单条；返回 `UpdateInfo` 列表 |

**审计记录结构（`UpdateInfoDO`）：**

| 字段 | 说明 |
|------|------|
| `infoId` | 被修改实体的 ID（如 userId、noticeId） |
| `infoType` | 实体类型枚举（`TUIT_USER`、`TUIT_NOTICE`、`TUIT_QUESTION`、`TUIT_STUDY`、`TUIT_PEOPLE`） |
| `beforeMsg` | 变更前的实体 Protobuf 二进制快照（新增时为 null） |
| `afterMsg` | 变更后的实体 Protobuf 二进制快照（删除时为 null） |
| `updateTime` | 变更发生的毫秒时间戳 |
| `updateUserTel` | 操作人手机号 |
| `updateName` | 操作人姓名 |

**触发时机（由各业务 Provider 在 `AbstractRpcService.handleUpdateInfo` 中触发）：**

| 操作 | 触发服务 |
|------|---------|
| 管理员修改/删除用户 | `town-user-provider` |
| 管理员发布/修改公告 | `town-notice-provider` |
| 管理员处理问题（变更状态/指派） | `town-question-provider` |
| 管理员发布/修改学习资料 | `town-study-provider` |
| 管理员审批家庭变更申请 | `town-people-provider` |

**降级行为：**

当 `town-update-provider` 未启动时，各业务 Provider 的 `IUpdateService` 引用自动切换到 `UpdateServiceMock`：
- `addUpdateInfo`：打印 WARN 日志并返回成功（审计日志静默丢弃，主链路不受影响）
- `listUpdateInfo`：返回 `TRC_UPDATE_SERVICE_NOT_FOUND` 错误码

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40008）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |