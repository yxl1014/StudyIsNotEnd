# town-dao — 统一数据访问层

本模块是整个平台的**唯一数据库访问入口**，以 Dubbo Provider 形式对外暴露 `IDaoService` 接口。所有业务 Provider（用户、公告、问题等）均通过 Dubbo RPC 调用本模块操作数据库和 Redis，无任何模块直接连接数据库。

- Dubbo 服务端口：**40002**
- Spring Boot 启动类：`TownDaoApplication`

---

## 目录结构

```
town-dao/
└── src/main/java/com/town/
    ├── TownDaoApplication.java      # 启动类
    ├── config/
    │   ├── MyBatisConfig.java       # MyBatis 配置
    │   └── RedisConfig.java         # Redis 配置
    ├── convert/                     # Protobuf ↔ DO 转换器
    │   ├── commonUtil/
    │   │   └── ProtoCommonMapper.java
    │   ├── NoticeInfoConvert.java
    │   ├── NotifyUserInfoConvert.java
    │   ├── PeopleInfoConvert.java
    │   ├── PeopleUpdateApplyConvert.java
    │   ├── QuestionHandlingInfoConvert.java
    │   ├── QuestionInfoConvert.java
    │   ├── StudyInfoConvert.java
    │   ├── UpdateInfoConvert.java
    │   ├── UserInfoConvert.java
    │   ├── UserReadNoticeInfoConvert.java
    │   └── UserStarStudyInfoConvert.java
    ├── mapper/                      # MyBatis Mapper 接口
    │   ├── NoticeInfoMapper.java
    │   ├── NotifyUserInfoMapper.java
    │   ├── PeopleInfoMapper.java
    │   ├── PeopleUpdateApplyMapper.java
    │   ├── QuestionHandlingInfoMapper.java
    │   ├── QuestionInfoMapper.java
    │   ├── StudyInfoMapper.java
    │   ├── UpdateInfoMapper.java
    │   ├── UserInfoMapper.java
    │   ├── UserReadNoticeInfoMapper.java
    │   └── UserStarStudyInfoMapper.java
    ├── provider/
    │   └── DaoManager.java          # IDaoService 实现，Dubbo Provider
    └── redis/
        └── RedisManager.java        # Redis 操作封装
```

---

## 启动类

| 文件 | 说明 |
|------|------|
| `TownDaoApplication.java` | Spring Boot 启动类，启用 Dubbo 自动配置，注册 `DaoManager` 为 Dubbo 服务，监听端口 40002 |

---

## config/ — 基础设施配置

| 文件 | 说明 |
|------|------|
| `MyBatisConfig.java` | MyBatis 配置类，使用 `@MapperScan("com.town.mapper")` 扫描所有 Mapper 接口并注册为 Spring Bean。`application.yml` 中配置了 `map-underscore-to-camel-case: true`，数据库下划线列名自动映射到 Java 驼峰字段名 |
| `RedisConfig.java` | Redis 配置类，创建 `RedisTemplate<String, Object>` Bean。Key 使用 `StringRedisSerializer`（纯字符串），Value 使用 `GenericJackson2JsonRedisSerializer`（JSON 序列化），保证存入 Redis 的对象可跨 JVM 反序列化 |

---

## convert/ — Protobuf ↔ DO 类型转换器

基于 MapStruct 注解处理器，**在编译期**生成类型安全的转换代码，无运行时反射开销。每个 Convert 接口提供 `toDO(proto)` 和 `toProto(entity)` 两个方法。

| 文件 | 转换对象 | 特殊处理 |
|------|---------|---------|
| `ProtoCommonMapper.java` | 公共转换工具，被其他 Convert 通过 `uses` 引用 | `enumToInt`：Protobuf 枚举 → int 序数；`byteStringToBytes`：`ByteString` ↔ `byte[]` 互转 |
| `UserInfoConvert.java` | `UserInfo` ↔ `UserInfoDO` | `userPower` 和 `flagType` 字段：Protobuf 枚举 ↔ int，使用 `expression` 处理可选字段的 null 安全转换 |
| `NoticeInfoConvert.java` | `NoticeInfo` ↔ `NoticeInfoDO` | 处理 `noticeAtt`（附件 bytes）和 `noticeType` 枚举的双向转换 |
| `QuestionInfoConvert.java` | `QuestionInfo` ↔ `QuestionInfoDO` | 处理 `questPhoto` 字节附件、`questionType` 和 `nodeType` 枚举转换 |
| `StudyInfoConvert.java` | `StudyInfo` ↔ `StudyInfoDO` | 处理 `studyType` 枚举和 `isOpen`、`isTop` 布尔字段 |
| `PeopleInfoConvert.java` | `PeopleInfo` ↔ `PeopleInfoDO` | 直接字段映射（无枚举），身份证号为字符串主键 |
| `PeopleUpdateApplyConvert.java` | `PeopleUpdateApply` ↔ `PeopleUpdateApplyDO` | 申请状态枚举转换 |
| `UpdateInfoConvert.java` | `UpdateInfo` ↔ `UpdateInfoDO` | `beforeMsg`/`afterMsg` 字节快照和 `infoType` 枚举转换 |
| `NotifyUserInfoConvert.java` | `NotifyUserInfo` ↔ `NotifyUserInfoDO` | 简单字段映射 |
| `UserReadNoticeInfoConvert.java` | `UserReadNoticeInfo` ↔ `UserReadNoticeInfoDO` | 简单字段映射 |
| `UserStarStudyInfoConvert.java` | `UserStarStudyInfo` ↔ `UserStarStudyInfoDO` | 简单字段映射 |
| `QuestionHandlingInfoConvert.java` | `QuestionHandlingInfo` ↔ `QuestionHandlingInfoDO` | 处理结果类型枚举转换 |

---

## mapper/ — MyBatis Mapper 接口

使用 MyBatis 注解方式（`@Select`、`@Insert`、`@Update`、`@Delete`）定义 SQL，支持动态 SQL（`<script>` + `<if>`）。所有分页查询在 `DaoManager` 中通过 `PageHelper.startPage(page, size)` 调用。

| 文件 | 对应数据表 | 主要方法 |
|------|-----------|---------|
| `UserInfoMapper.java` | `user_info` | `selectById(Long)`、`selectAll()`、`insert(UserInfoDO)`、`update(UserInfoDO)`（动态字段更新）、`delete(Long)` |
| `NoticeInfoMapper.java` | `notice_info` | `selectById(Integer)`、`selectAll()`、`selectByWriterAndCreateTime(Long, Long)`（用于发布后查询刚插入的公告）、`insert`、`update`（动态字段）、`delete` |
| `QuestionInfoMapper.java` | `question_info` | `selectById`、`selectByWriterTel(Long)`（按上报人查）、`selectByChoiceUser(Long)`（按指派人查）、`selectByWriterTelAndType`、`selectByChoiceUserAndType`（按状态过滤）、`insert`、`update` |
| `StudyInfoMapper.java` | `study_info` | `selectById`、`selectByCreateTime(Long)`（发布后定位）、`selectAll()`、`insert`、`update`、`delete` |
| `PeopleInfoMapper.java` | `people_info` | `selectById(String)`（身份证号）、`selectAll()`、`insert`、`update`、`delete(String)` |
| `PeopleUpdateApplyMapper.java` | `people_update_apply` | `selectById`、`selectAll()`、`selectAllByUserTel(Long)`（按申请人查）、`insert`、`update`、`delete` |
| `UpdateInfoMapper.java` | `update_info` | `selectById`、`selectAll()`、`insert` |
| `NotifyUserInfoMapper.java` | `notify_user_info` | `selectById`、`selectByUserId(Long)`、`insert`、`update`、`delete(Integer)`、`deleteAll()`（拉取通知后清空） |
| `UserReadNoticeInfoMapper.java` | `user_read_notice_info` | `selectById`、`selectByUserTel(Long)`、`selectByUserTelAndNoticeId(Long, Integer)`（防止重复已读）、`insert` |
| `UserStarStudyInfoMapper.java` | `user_star_study_info` | `selectById`、`selectByIdAndTel(Integer, Long)`（查是否已收藏）、`selectByUserTel(Long)`、`insert`、`update`、`delete` |
| `QuestionHandlingInfoMapper.java` | `question_handling_info` | `selectById`、`selectAll()`、`insert`、`update`、`delete` |

---

## provider/ — Dubbo 服务实现

| 文件 | 说明 |
|------|------|
| `DaoManager.java` | `IDaoService` 的唯一实现，标注 `@DubboService(timeout=10000, retries=0)`。注入全部 11 个 Mapper 和 11 个 Convert，聚合所有数据访问操作。按功能分区：User（5 个方法）、Notice（7 个）、Update（4 个）、UserReadNotice（4 个）、Question（7 个）、Notify（6 个）、Study（7 个）、Star（6 个）、People（5 个）、Apply（6 个），以及 Redis（7 个）共约 60 余个方法，是整个平台的数据访问总汇 |

---

## redis/ — Redis 操作封装

| 文件 | 说明 |
|------|------|
| `RedisManager.java` | 对 `RedisTemplate` 的统一封装，实现 `IRedisService`，提供完整的 Redis 数据结构操作：**Key**（exists、delete、expire、getExpire）、**String**（set with/without TTL、get）、**Hash**（hSet、hGet、hDel、hGetAll）、**List**（lPush、rPop、lRange）、**Set**（sAdd、sMembers、sIsMember）、**ZSet**（zAdd、zRange、zRemove）。目前业务只用到 String 类型（Token 存取），Hash/List/Set/ZSet 预留扩展 |

---

## 资源文件

| 文件 | 说明 |
|------|------|
| `application.yml` | 配置 MySQL 数据源（`43.142.255.18:3306/town`）、Redis 连接、Dubbo 服务注册（Nacos）、MyBatis 驼峰映射、PageHelper 分页插件 |
| `log4j2.xml` | Log4j2 日志配置，定义控制台和文件输出格式与日志级别 |