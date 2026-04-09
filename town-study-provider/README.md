# town-study-provider — 学习资料服务

负责政策解读、农技知识、健康养生、法律常识等学习内容的发布与管理，支持阅读量统计和用户收藏功能。以 Dubbo Provider 形式注册到 Nacos，被 `town-consumer` 通过 RPC 调用。

- Dubbo 服务端口：**40006**
- 启动类：`StudyProviderApplication`

---

## 目录结构

```
town-study-provider/
└── src/main/java/provider/
    ├── StudyProviderApplication.java  # 启动类
    └── impl/
        └── StudyServiceImpl.java      # 学习资料服务实现
```

---

## 文件说明

### `StudyProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `StudyServiceImpl` 作为 `IStudyService` 的实现通过 Nacos 注册，监听 Dubbo 端口 40006。

---

### `StudyServiceImpl.java`

`IStudyService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库操作 |
| `IUpdateService` | `@DubboReference(check=false, mock=...)` | 审计日志（可选，降级 Mock） |

**业务方法：**

| 方法 | 消息类型 | 权限 | 业务逻辑 |
|------|---------|------|---------|
| `createStudy(token, CreateStudyReq)` | `TMT_CreateStudyRsp` | 需要村干部权限 | 校验 title、tip、content 均不为空 → 强制 `studyId=null`（自增）→ 填充当前时间戳 → 写库 → 按时间戳回查刚插入的记录（获取自增 ID）→ 构造 `UpdateInfoDO`（仅 afterMsg）→ 返回 |
| `updateStudy(token, UpdateStudyReq)` | `TMT_UpdateStudyRsp` | 需要村干部权限 | 校验 studyId 有效 → 查旧数据快照 → 按 `isDel` 删除或更新（更新时强制不允许修改 `studyCreateTime` 和 `readCount`）→ 构造审计日志 → 返回 |
| `listStudy(token, ListStudyReq)` | `TMT_ListStudyRsp` | 需要 Token | `studyId=0` 时分页查全部；`studyId>0` 时查单条并**自动将阅读量 +1**（读取后单独 update 一次 `readCount`）→ 返回资料列表 |
| `starStudy(token, StarStudyReq)` | `TMT_StarStudyRsp` | 需要 Token | 取消收藏（`isCancel=true`）：查收藏记录存在则删除，不存在则直接返回成功（幂等）；收藏：先检查资料是否存在 → 检查是否已收藏（防重复，返回 `TRC_STUDY_IS_STAR`）→ 插入 `user_star_study_info` 记录 |
| `listUserStarStudy(token, ListUserStarStudyReq)` | `TMT_ListUserStarStudyRsp` | 需要 Token | 分页查当前用户的收藏记录 → 逐条按 `studyId` 查完整资料信息（若资料已删则跳过）→ 返回资料列表 |

**关键设计：**
- 阅读量统计在单条查询时触发，分页列表查询不累加阅读量，避免列表翻页时数据膨胀
- `starStudy` 取消收藏时若记录不存在直接返回成功，实现幂等操作
- `listUserStarStudy` 会过滤掉已被删除的资料（`study_selectById` 返回 null 时 `continue`）

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40006）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |