# town-question-provider — 问题上报服务

负责村民问题上报、处理流转和状态管理。村民可提交问题，村干部可指派处理人和变更状态。以 Dubbo Provider 形式注册到 Nacos，被 `town-consumer` 通过 RPC 调用。

- Dubbo 服务端口：**40005**
- 启动类：`QuestionProviderApplication`

---

## 目录结构

```
town-question-provider/
└── src/main/java/provider/
    ├── QuestionProviderApplication.java  # 启动类
    └── impl/
        └── QuestionServiceImpl.java      # 问题服务实现
```

---

## 文件说明

### `QuestionProviderApplication.java`

Spring Boot 启动类，启用 Dubbo 服务自动注册。启动后 `QuestionServiceImpl` 作为 `IQuestionService` 的实现通过 Nacos 注册，监听 Dubbo 端口 40005。

---

### `QuestionServiceImpl.java`

`IQuestionService` 接口的完整实现，标注 `@DubboService(timeout=10000, retries=0)`，继承 `AbstractRpcService`。

**依赖的 Dubbo 服务：**

| 服务 | 注入方式 | 说明 |
|------|---------|------|
| `IDaoService` | `@DubboReference` | 数据库操作 |
| `IUpdateService` | `@DubboReference(check=false, mock=...)` | 审计日志（可选，降级 Mock） |

**业务方法：**

| 方法 | 消息类型 | 权限 | 业务逻辑 |
|------|---------|------|---------|
| `createQuestion(token, CreateQuestionReq)` | `TMT_CreateQuestionRsp` | 需要 Token | 强制写入：`questionId=null`（自增）、`questionWriterTel`=当前用户手机号、`nodeType=TQNT_PRE`（待处理）、`choiceUser=null`（未指派）、`questionTime`=当前时间戳 → 写库 |
| `updateQuestion(token, UpdateQuestionReq)` | `TMT_UpdateQuestionRsp` | 需要 Token | 按角色分支调用 `updateByCm` 或 `updateByCgb` |
| `listQuestion(token, ListQuestionReq)` | `TMT_ListQuestionRsp` | 需要 Token | 按角色和是否携带 `nodeType` 过滤条件分四路查询 |

**`updateQuestion` 角色分支逻辑：**

| 分支 | 触发条件 | 逻辑（`updateByCm` - 村民） |
|------|---------|------------------------------|
| 村民修改 | `UserContext.getUserPower() == TUP_CM` | 校验问题状态必须是"待处理"（`TQNT_PRE`），否则返回 `TRC_QUESTION_IS_IN_OPT`；强制不允许修改上报人、状态、指派人、时间；只能改类型、描述和图片 |
| 干部修改 | `UserContext.getUserPower() == TUP_CGM` | 无状态限制，可修改任意字段（包括指派处理人和变更状态）；检查修改前后是否有差异（无变化返回 `TRC_QUESTION_NOT_UPDATE`）；修改成功后写入变更审计日志 |

**`listQuestion` 查询分支：**

| 角色 | 有 nodeType 过滤 | 无 nodeType 过滤 |
|------|----------------|----------------|
| 村民 `TUP_CM` | `selectByWriterTelAndType`（按自己手机号+状态） | `selectByWriterTel`（按自己手机号） |
| 村干部 `TUP_CGM` | `selectByChoiceUserAndType`（按指派给我的+状态） | `selectByChoiceUser`（按指派给我的） |

**问题状态流转：**

```
TQNT_PRE（待处理）
    → TQNT_ING（处理中）  [村干部指派后]
    → TQNT_END（已解决）  [村干部标记完成]
    → TQNT_CLOSE（已关闭）[不可处理情况]
```

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | Dubbo 服务配置（端口 40005）、Nacos 注册中心地址 |
| `log4j2.xml` | 日志配置 |