# town-user-provider — 用户服务

负责用户注册、登录、信息管理和消息通知功能。以 Dubbo Provider 形式注册到 Nacos，被 `town-consumer` 通过 RPC 调用。

- Dubbo 服务端口：**40003**
- 启动类：`TownProviderApplication`

---

## 目录结构

```
town-user-provider/
└── src/main/java/provider/
    ├── TownProviderApplication.java   # 启动类
    └── impl/
        └── UserServiceImpl.java       # 用户服务实现
```

---

## 文件说明

### `TownProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `UserServiceImpl` 作为 `IUserService` 的实现通过 Nacos 注册到服务中心，监听 Dubbo 端口 40003。

---

### `UserServiceImpl.java`

`IUserService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`（提供模板方法、权限校验、Token 解析和审计日志写入）。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库和 Redis 操作 |
| `IUpdateService` | `@DubboReference(check=false, mock=...)` | 审计日志（可选，不可用时降级为 Mock） |

**业务方法：**

| 方法 | 消息类型 | 权限 | 业务逻辑 |
|------|---------|------|---------|
| `login(LoginReq)` | `TMT_LoginRsp` | 无需 Token | 按手机号查用户 → 校验密码 → 检查封号状态 → 构造 `TokenInfoDO`（含 sessionCode）→ 生成 JWT → 存入 Redis（TTL 3 天）→ 返回 token 和用户信息 |
| `register(RegisterReq)` | `TMT_RegisterRsp` | 无需 Token | 校验必填字段 → 检查手机号是否已注册 → 写入当前时间戳 → 插入数据库 |
| `updateUserInfo(token, UpdateUserInfoReq)` | `TMT_UpdateUserInfoRsp` | 需要村干部权限 | 参数非空校验 → 查旧数据快照 → 根据 `isDel` 执行删除或更新 → 强制该用户 Redis Token 失效（需重新登录）→ 构造 `UpdateInfoDO`（含变更前后快照）→ 通过 `AbstractRpcService` 写入审计日志 |
| `listUserInfo(token, ListUserInfoReq)` | `TMT_ListUserInfoRsp` | 需要 Token | 村民只返回自身信息；村干部可按手机号单查或分页查全部 |
| `AddNotifyUserInfo(NotifyUserInfoDO)` | — | 内部调用 | 直接调用 `daoService.notify_insert` 向指定用户写入一条通知（被其他服务调用） |
| `listNotifyUserInfo(token, ListNotifyUserInfoReq)` | `TMT_ListNotifyUserInfoRsp` | 需要 Token | 查出当前用户所有通知 → 清空通知表（一次性拉取模式）→ 返回通知列表 |
| `getUserName()` | — | 内部 | 生成 5 位随机数字字符串（测试用途） |

**关键设计：**
- 登录时生成的 `sessionCode`（6 位随机数）写入 `TokenInfoDO` 并存入 Redis，`TokenLoginInHandler` 验证 Token 时会同时比对 Redis 中的值，实现 Token 可主动失效（如修改用户信息后强制重登）
- `updateUserInfo` 修改用户后立即删除 Redis 中对应的 Token，被修改用户下次请求时 Token 校验不通过，强制重新登录

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40003）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |