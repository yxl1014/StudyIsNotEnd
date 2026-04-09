# town-notice-provider — 公告通知服务

负责村级公告的发布、修改、查询以及已读确认功能。以 Dubbo Provider 形式注册到 Nacos，被 `town-consumer` 通过 RPC 调用。

- Dubbo 服务端口：**40004**
- 启动类：`NoticeProviderApplication`

---

## 目录结构

```
town-notice-provider/
└── src/main/java/provider/
    ├── NoticeProviderApplication.java  # 启动类
    └── impl/
        └── NoticeServiceImpl.java      # 公告服务实现
```

---

## 文件说明

### `NoticeProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `NoticeServiceImpl` 作为 `INoticeService` 的实现通过 Nacos 注册，监听 Dubbo 端口 40004。

---

### `NoticeServiceImpl.java`

`INoticeService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`（提供 Token 解析、权限校验、审计日志写入）。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库操作 |
| `IUpdateService` | `@DubboReference(check=false, mock=...)` | 审计日志（可选，不可用时降级 Mock） |

**业务方法：**

| 方法 | 消息类型 | 权限 | 业务逻辑 |
|------|---------|------|---------|
| `createNotice(token, CreateNoticeReq)` | `TMT_CreateNoticeRsp` | 需要村干部权限 | 校验 title/context 不为空 → 强制将 noticeId 置 null（使用数据库自增 ID）→ 填充当前时间戳、发布人手机号和姓名（来自 `UserContext`）→ 写库 → 按发布人+时间查询刚插入的记录（获取自增 ID）→ 构造 `UpdateInfoDO`（仅 afterMsg，无 beforeMsg，表示新增）→ 返回 |
| `updateNotice(token, UpdateNoticeReq)` | `TMT_UpdateNoticeRsp` | 需要村干部权限 | 参数非空校验 → 查旧公告快照 → 按 `isDel` 执行删除或更新（更新时强制不允许修改发布时间和发布人）→ 构造 `UpdateInfoDO`（含变更前后快照）→ 返回 |
| `listNotice(ListNoticeReq)` | `TMT_ListNoticeRsp` | 无需 Token | `noticeId=0` 时分页查全部；`noticeId>0` 时查单条。无 Token 即可访问（村民无需登录也能查看公告） |
| `setNoticeRead(token, SetNoticeReadReq)` | `TMT_SetNoticeReadRsp` | 需要 Token | 校验公告是否存在 → 检查公告 `isAcceptRead` 是否为 true（不支持确认的公告返回 `TRC_NOTICE_CAN_NOT_ACCEPT`）→ 检查是否已确认（防重复，返回 `TRC_NOTICE_IS_ACCEPT`）→ 插入 `user_read_notice_info` 记录（含确认时间戳）→ 返回 |
| `listNoticeRead(token, ListNoticeReadReq)` | `TMT_ListNoticeReadRsp` | 需要 Token | 查当前用户所有已读记录 → 提取 noticeId 列表返回（用于客户端判断哪些公告已确认） |

**关键设计：**
- `listNotice` 不需要 Token，公告对所有人公开可见
- 创建公告后通过"发布人+创建时间"组合查询刚插入的记录，因为数据库使用自增 ID，插入时不知道 ID，需要二次查询
- 修改公告时强制锁定发布时间、发布人手机号、发布人姓名，防止被篡改

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40004）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |