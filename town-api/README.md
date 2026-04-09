# town-api — 公共 API 与数据模型

本模块是整个项目的共享库，**不独立部署**，作为 Maven 依赖被其他所有模块引用。包含所有跨模块共享的实体类、服务接口、Protobuf 生成代码和工具类。

---

## 目录结构

```
town-api/
├── proto/source/                # Protobuf 源定义文件
│   ├── base.proto
│   ├── entity.proto
│   └── protocol.proto
└── src/main/java/
    ├── entity/                  # 数据库实体类（DO）
    ├── exception/               # 业务异常
    ├── mock/                    # 降级 Mock 实现
    ├── po/                      # Protobuf 自动生成的 Java 类
    ├── serviceEntity/           # RPC 服务基础抽象
    ├── townInterface/           # Dubbo 服务接口定义
    └── util/                    # 通用工具类
```

---

## proto/source/ — Protobuf 协议定义

所有网络通信消息的原始定义文件，由 `protoc` 编译器生成 `po/` 目录下的 Java 类。

| 文件 | 说明 |
|------|------|
| `base.proto` | 基础枚举类型：用户角色（`TUserPower`）、账户状态（`TUserFlagType`）、公告类型（`TNoticeType`）、问题类型（`QuestionType`/`QuestionNodeType`）、学习类型（`StudyType`）、变更类型（`TUpdateInfoType`）、响应码（`RespCode`，31 种）、消息类型枚举（`MsgType`，55 种） |
| `entity.proto` | 领域对象消息定义：`UserInfo`、`NoticeInfo`、`QuestionInfo`、`StudyInfo`、`PeopleInfo`、`UpdateInfo`、`NotifyUserInfo`、`UserReadNoticeInfo`、`UserStarStudyInfo`、`PeopleUpdateApply`、`QuestionHandlingInfo` |
| `protocol.proto` | 请求/响应协议包装：`RequestMsg`（含 msgType、token、msg）、`ResponseMsg`（含 errCode、msgType、msg），以及所有业务操作的 Req/Rsp 消息对（共 28 对） |

---

## entity/ — 数据库实体类（DO）

MyBatis 从数据库查询后映射到的 Java 对象，字段命名与数据库列一一对应。

| 文件 | 对应数据表 | 说明 |
|------|-----------|------|
| `UserInfoDO.java` | `user_info` | 用户信息：手机号（主键）、姓名、密码、所属村庄、角色（int）、注册时间、账户状态 |
| `NoticeInfoDO.java` | `notice_info` | 公告信息：公告 ID、类型、标题、正文、发布人、时间、是否置顶、是否需要确认已读、二进制附件 |
| `QuestionInfoDO.java` | `question_info` | 问题上报：问题 ID、类型、描述、图片附件、上报人、处理状态、指派处理人、时间 |
| `StudyInfoDO.java` | `study_info` | 学习资料：资料 ID、类型、标题、摘要、全文、是否公开、是否置顶、阅读量、创建时间 |
| `PeopleInfoDO.java` | `people_info` | 家庭成员：身份证号（主键）、姓名、户号、备注描述 |
| `UpdateInfoDO.java` | `update_info` | 变更审计日志：变更 ID、被修改实体 ID、实体类型、变更前/后 Protobuf 字节快照、时间、操作人 |
| `NotifyUserInfoDO.java` | `notify_user_info` | 用户通知消息：通知 ID、目标用户手机号、通知内容 |
| `UserReadNoticeInfoDO.java` | `user_read_notice_info` | 用户已读公告记录：记录 ID、用户手机号、公告 ID |
| `UserStarStudyInfoDO.java` | `user_star_study_info` | 用户收藏学习资料：记录 ID、用户手机号、资料 ID |
| `PeopleUpdateApplyDO.java` | `people_update_apply` | 家庭信息变更申请：申请 ID、申请人手机号、身份证号、申请描述、状态 |
| `QuestionHandlingInfoDO.java` | `question_handling_info` | 问题处理记录：记录 ID、问题 ID、处理人、处理结果 |
| `TokenInfoDO.java` | — | JWT Token 携带的用户上下文信息（存入 Redis）：手机号、sessionCode、登录时间、角色、状态、姓名 |

---

## exception/ — 业务异常

| 文件 | 说明 |
|------|------|
| `BizException.java` | 自定义运行时异常，携带 `code`（int）和 `msg`（String）两个字段，用于在业务逻辑中抛出可识别的错误 |

---

## mock/ — 降级 Mock 实现

| 文件 | 说明 |
|------|------|
| `UpdateServiceMock.java` | `IUpdateService` 的 Mock 实现。当 `town-update-provider` 服务未启动时，Dubbo 自动调用此类：`addUpdateInfo` 打印警告日志后返回 `TRC_OK`（静默丢弃）；`listUpdateInfo` 返回 `TRC_UPDATE_SERVICE_NOT_FOUND` 错误码 |

---

## po/ — Protobuf 自动生成代码

由 `protoc` 根据 `.proto` 文件自动生成，**不要手动修改**。

### 外层容器类（每个 .proto 文件对应一个）

| 文件 | 说明 |
|------|------|
| `BaseProto.java` | `base.proto` 的外层容器，包含所有枚举类型的注册信息 |
| `EntityProto.java` | `entity.proto` 的外层容器，包含所有领域对象消息的注册信息 |
| `ProtocolProto.java` | `protocol.proto` 的外层容器，包含所有请求/响应消息的注册信息 |

### 枚举类

| 文件 | 说明 |
|------|------|
| `MsgType.java` | 消息类型枚举，55 种，如 `TMT_LoginReq`、`TMT_CreateNoticeReq` 等，用于请求路由 |
| `RespCode.java` | 响应码枚举，31 种，如 `TRC_OK`、`TRC_TOKEN_INVALID`、`TRC_USER_NOT_EXIST` 等 |
| `TUserPower.java` | 用户角色枚举：`TUP_CM`（村民）、`TUP_CGM`（村干部） |
| `TUserFlagType.java` | 用户账户状态枚举：正常、冻结、标记 |
| `TNoticeType.java` | 公告类型枚举：通知、招募、活动、政务公开、公共事务 |
| `QuestionType.java` | 问题分类枚举：环境卫生、邻里纠纷、基础设施、干部行为 |
| `QuestionNodeType.java` | 问题处理状态枚举：待处理、处理中、已解决、已关闭 |
| `QuestionHandlingType.java` | 问题处理结果类型枚举 |
| `StudyType.java` | 学习资料分类枚举：政策解读、农技推广、健康养生、法律常识 |
| `TUpdateInfoType.java` | 变更审计记录的实体类型枚举：用户、公告、问题、学习资料、人口 |

### 请求/响应消息对（每对包含 Req.java、RspOrBuilder.java 等）

| 业务 | 请求消息 | 响应消息 |
|------|---------|---------|
| 登录 | `LoginReq` | `LoginRsp`（含 token 和 UserInfo） |
| 注册 | `RegisterReq` | `RegisterRsp` |
| 更新用户 | `UpdateUserInfoReq` | `UpdateUserInfoRsp` |
| 查询用户 | `ListUserInfoReq` | `ListUserInfoRsp` |
| 发布公告 | `CreateNoticeReq` | `CreateNoticeRsp` |
| 更新公告 | `UpdateNoticeReq` | `UpdateNoticeRsp` |
| 查询公告 | `ListNoticeReq` | `ListNoticeRsp` |
| 确认已读 | `SetNoticeReadReq` | `SetNoticeReadRsp` |
| 查询已读 | `ListNoticeReadReq` | `ListNoticeReadRsp` |
| 提交问题 | `CreateQuestionReq` | `CreateQuestionRsp` |
| 更新问题 | `UpdateQuestionReq` | `UpdateQuestionRsp` |
| 查询问题 | `ListQuestionReq` | `ListQuestionRsp` |
| 查询处理结果 | `ListQuestionHandlingReq` | `ListQuestionHandlingRsp` |
| 发布学习资料 | `CreateStudyReq` | `CreateStudyRsp` |
| 更新学习资料 | `UpdateStudyReq` | `UpdateStudyRsp` |
| 查询学习资料 | `ListStudyReq` | `ListStudyRsp` |
| 收藏资料 | `StarStudyReq` | `StarStudyRsp` |
| 查询收藏 | `ListUserStarStudyReq` | `ListUserStarStudyRsp` |
| 新增家庭成员 | `CreatePeopleReq` | `CreatePeopleRsp` |
| 更新家庭成员 | `UpdatePeopleReq` | `UpdatePeopleRsp` |
| 查询家庭信息 | `ListPeopleInfoReq` | `ListPeopleInfoRsp` |
| 提交变更申请 | `CreatePeopleUpdateApplyReq` | `CreatePeopleUpdateApplyRsp` |
| 查询变更申请 | `ListPeopleUpdateApplyReq` | `ListPeopleUpdateApplyRsp` |
| 审批变更申请 | `DelPeopleUpdateApplyReq` | `DelPeopleUpdateApplyRsp` |
| 查询审计日志 | `ListUpdateInfoReq` | `ListUpdateInfoRsp` |
| 查询通知 | `ListNotifyUserInfoReq` | `ListNotifyUserInfoRsp` |
| 协议包装 | `RequestMsg` | `ResponseMsg` |
| 心跳测试 | `RequestTest` | `ResponseTest` |

> 每个消息类型都有对应的 `XxxOrBuilder.java` 接口文件，是 Protobuf 生成的 Builder 模式接口，无需关注。

---

## serviceEntity/ — RPC 服务基础抽象

| 文件 | 说明 |
|------|------|
| `AbstractRpcService.java` | 所有业务 Provider 的抽象基类。提供模板方法 `execute(msgType, token, supplier)`，封装了：① 解析 Token 并绑定到 `UserContext`；② 权限校验（需要管理员权限的操作列表在构造器中初始化）；③ 执行业务逻辑（调用传入的 `supplier`）；④ 自动写入变更审计日志；⑤ 统一异常兜底。各 Provider 只需实现业务逻辑即可 |
| `BizResult.java` | 业务操作的返回值包装类，包含 `RespCode code`（响应码）、`Message msg`（Protobuf 响应消息体）、`UpdateInfoDO updateInfoDO`（可选的审计日志对象）。提供三个静态工厂方法：`ok(msg)`、`ok(msg, updateInfo)`、`error(code)` |
| `UserContext.java` | 基于 `ThreadLocal` 的当前请求用户上下文，存储 `TokenInfoDO`。提供 `set`、`get`、`getRequired`、`clear` 方法，以及 `getUserTel()`、`getUserPower()`、`getUserFlagType()`、`getUserName()` 快捷方法。**在每次 RPC 请求结束时必须调用 `clear()` 清理（由 `AbstractRpcService` 负责）** |

---

## townInterface/ — Dubbo 服务接口

所有 Dubbo 服务 Provider 必须实现、Consumer 通过 `@DubboReference` 注入的接口定义。

| 文件 | 说明 |
|------|------|
| `IDaoService.java` | 数据访问服务接口，定义所有实体的 CRUD 方法（user_、notice_、quest_、study_、star_、people_、apply_、update_、read_、notify_ 前缀）和 Redis 操作方法（redis_），以及各实体的 Protobuf ↔ DO 双向转换方法（`toDO`/`toProto`） |
| `IUserService.java` | 用户服务接口：`login`、`register`、`updateUserInfo`、`listUserInfo`、`AddNotifyUserInfo`、`listNotifyUserInfo` |
| `INoticeService.java` | 公告服务接口：`createNotice`、`updateNotice`、`listNotice`、`setNoticeRead`、`listNoticeRead` |
| `IQuestionService.java` | 问题服务接口：`createQuestion`、`updateQuestion`、`listQuestion` |
| `IStudyService.java` | 学习资料服务接口：`createStudy`、`updateStudy`、`listStudy`、`starStudy`、`listUserStarStudy` |
| `IPeopleService.java` | 家庭信息服务接口：`createPeople`、`updatePeople`、`listPeopleInfo`、`createPeopleUpdateApply`、`listPeopleUpdateApply`、`delPeopleUpdateApply` |
| `IUpdateService.java` | 变更审计服务接口：`addUpdateInfo`（写入一条审计记录）、`listUpdateInfo`（查询审计日志） |
| `IRedisService.java` | Redis 操作接口（内部使用，由 `RedisManager` 实现），定义 Key、String、Hash、List、Set、ZSet 操作签名 |

---

## util/ — 通用工具类

| 文件 | 说明 |
|------|------|
| `JwtUtil.java` | JWT Token 工具类。使用 HMAC-SHA256 算法，密钥硬编码（256 位），Token 有效期 1 天。`generateToken(subject, TokenInfoDO)` 将用户信息序列化为 JSON 写入 Claims；`parseTokenInfo(token)` 反序列化取出；`validateToken(token)` 校验有效性 |
| `TokenResolver.java` | Token 解析器，封装 `JwtUtil.parseTokenInfo`，解析失败时返回 `null` 而非抛异常，供 `AbstractRpcService` 安全调用 |
| `ConstValue.java` | 全局常量：`Redis_Prefix_Token`（Redis Key 前缀 `"rpt"`）、`Redis_Token_Expire_Sec`（Token 过期时间 3 天 = 259200 秒） |
| `CommonEntityBuilder.java` | `ResponseMsg` 构造工厂类，提供静态方法：`buildOk(msgType, msg)`、`buildOk(msgType, BizResult)`、`buildError(msgType)`、`buildError(msgType, code)`、`buildNoBodyResp(code)`（无消息体响应，用于认证失败等场景） |
| `RandomUtil.java` | 随机工具类，`RandomCode(len)` 生成指定长度的随机数字字符串，用于生成 Token 的 `sessionCode` 字段（6 位） |
| `TimeUtil.java` | 时间工具类（基于 `java.time`，线程安全）。提供：当前毫秒/秒时间戳、`LocalDateTime` 与时间戳互转、格式化/解析、今天开始/结束时间、时间加减、两时间差值计算等方法 |