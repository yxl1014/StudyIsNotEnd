# town-people-provider — 人口家庭信息服务

负责村民家庭成员信息的录入与管理，以及村民提交家庭信息变更申请的审批流程。以 Dubbo Provider 形式注册到 Nacos，被 `town-consumer` 通过 RPC 调用。

- Dubbo 服务端口：**40007**
- 启动类：`PeopleProviderApplication`

---

## 目录结构

```
town-people-provider/
└── src/main/java/provider/
    ├── PeopleProviderApplication.java  # 启动类
    └── impl/
        └── PeopleServiceImpl.java      # 家庭信息服务实现
```

---

## 文件说明

### `PeopleProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `PeopleServiceImpl` 作为 `IPeopleService` 的实现通过 Nacos 注册，监听 Dubbo 端口 40007。

---

### `PeopleServiceImpl.java`

`IPeopleService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库操作 |
| `IUpdateService` | `@DubboReference(check=false, mock=...)` | 审计日志（可选，降级 Mock） |

**业务方法：**

| 方法 | 消息类型 | 权限 | 业务逻辑 |
|------|---------|------|---------|
| `createPeople(token, CreatePeopleReq)` | `TMT_CreatePeopleRsp` | 需要村干部权限 | **批量处理**：遍历请求中的 `infos` 列表，每条记录独立校验：① 身份证号不为空 → ② 检查是否已存在（防重复）→ ③ 写库；每条记录的失败信息附带索引（index）和错误码（`ErrInfo`）写入响应，不影响其他记录的处理（部分成功） |
| `updatePeople(token, UpdatePeopleReq)` | `TMT_UpdatePeopleRsp` | 需要村干部权限 | 身份证号非空校验 → 检查记录是否存在 → 按 `isDel` 执行删除或更新 |
| `listPeopleInfo(token, ListPeopleInfoReq)` | `TMT_ListPeopleInfoRsp` | 需要 Token | 按角色分支查询：村民必须提供身份证号只能查单条；村干部可按身份证号单查，或分页查全部 |
| `createPeopleUpdateApply(token, CreatePeopleUpdateApplyReq)` | `TMT_CreatePeopleUpdateApplyRsp` | 需要 Token | 村民提交家庭信息变更申请，填充申请人手机号（来自 `UserContext`）和申请时间 → 写库 |
| `listPeopleUpdateApply(token, ListPeopleUpdateApplyReq)` | `TMT_ListPeopleUpdateApplyRsp` | 需要 Token | 按 `applyId` 单查时：村民只能查自己的申请（权限校验）；分页查全量时：村民只查自己的，村干部查全部 |
| `delPeopleUpdateApply(token, DelPeopleUpdateApplyReq)` | `TMT_DelPeopleUpdateApplyRsp` | 需要村干部权限 | 查申请记录存在 → 删除申请（无论通过还是拒绝均删除，由前端携带 reason 字段区分）→ 构造 `UpdateInfoDO` 写入审计日志 |

**关键设计：**
- `createPeople` 批量写入时，单条失败不影响其余记录，响应中通过 `ErrInfo` 列表报告各条结果，村干部可一次性导入多名村民
- 家庭成员以**身份证号**作为主键（字符串类型），天然唯一，无需使用自增 ID
- 变更申请审批逻辑：村干部通过 `delPeopleUpdateApply` 删除申请记录（无论批准还是拒绝），需在业务层自行区分处理结果

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40007）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |