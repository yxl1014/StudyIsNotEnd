# 村务管理系统前端

基于 Vue 3 + Element Plus + Protobuf 构建的村务管理平台，分为**村民端**和**村干部后台**两套界面，通过 Protobuf 二进制协议与后端通信。

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 前端框架，使用 Composition API |
| Vue Router | 4.2 | 路由管理，含权限守卫 |
| Pinia | 2.1 | 状态管理 |
| Element Plus | 2.5 | UI 组件库 |
| Axios | 1.6 | HTTP 请求 |
| Protobufjs | 7.2 | 二进制通信协议 |
| Vite | 5.0 | 构建工具 |

---

## 项目结构

```
vue-village-system/
├── proto/                          # Protobuf 协议定义
│   ├── base.proto                  # 基础类型定义
│   ├── protocol.proto              # 消息类型 & 错误码
│   └── entity.proto                # 业务实体定义
│
├── src/
│   ├── api/                        # API 接口封装
│   │   ├── request.js              # Protobuf 请求基础封装
│   │   ├── user.js                 # 用户接口
│   │   ├── notice.js               # 公告接口
│   │   ├── complaint.js            # 投诉接口
│   │   ├── study.js                # 学习资料接口
│   │   ├── people.js               # 家庭信息接口
│   │   └── *.mock.js               # 各模块 Mock 数据
│   │
│   ├── proto/                      # 生成的 Protobuf 代码
│   │   ├── proto.js                # JS 代码（736KB）
│   │   └── proto.d.ts              # TypeScript 类型（276KB）
│   │
│   ├── router/
│   │   └── index.js                # 路由配置 + 权限守卫
│   │
│   ├── stores/
│   │   └── user.js                 # 用户状态（Pinia）
│   │
│   ├── utils/
│   │   ├── format.js               # 日期/格式化工具
│   │   └── request.js              # Axios 封装
│   │
│   ├── views/
│   │   ├── Login.vue               # 登录页
│   │   ├── Register.vue            # 注册页
│   │   ├── Home.vue                # 村民首页
│   │   ├── Profile.vue             # 个人中心
│   │   ├── notice/
│   │   │   ├── NoticeList.vue      # 公告列表
│   │   │   └── NoticeDetail.vue    # 公告详情
│   │   ├── complaint/
│   │   │   ├── ComplaintList.vue   # 投诉列表
│   │   │   └── ComplaintCreate.vue # 提交投诉
│   │   ├── study/
│   │   │   ├── StudyList.vue       # 资料列表
│   │   │   ├── StudyDetail.vue     # 资料详情
│   │   │   └── StudyFavorite.vue   # 我的收藏
│   │   └── admin/                  # 村干部后台
│   │       ├── Layout.vue          # 后台整体布局
│   │       ├── Dashboard.vue       # 工作台
│   │       ├── UserManage.vue      # 用户管理
│   │       ├── NoticeManage.vue    # 公告管理
│   │       ├── ComplaintManage.vue # 投诉管理
│   │       ├── StudyManage.vue     # 学习管理
│   │       └── PeopleManage.vue    # 信息管理
│   │
│   ├── App.vue
│   └── main.js
│
├── index.html
├── vite.config.js
└── package.json
```

---

## 功能模块

### 村民端（8 个页面）

#### 用户模块
- 手机号 + 密码登录
- 手机号注册
- 个人中心（查看信息、修改密码）

#### 公告模块
- 公告列表（支持类型筛选）
- 公告详情（查看内容、附件下载）
- 已读标记（需确认阅读的公告）

#### 投诉模块
- 我的投诉列表
- 提交投诉（选择类别、填写内容、上传图片）
- 查看处理进度（待处理 / 处理中 / 已完成）

#### 学习模块
- 资料列表（分类浏览、关键词搜索）
- 资料详情（在线预览）
- 我的收藏（收藏 / 取消收藏）

---

### 村干部后台（7 个页面）

#### 工作台
- 数据统计（用户数、公告数、投诉数、资料数）
- 快捷操作入口
- 待处理投诉 & 最新公告动态

#### 用户管理
- 用户列表（按姓名或手机号搜索）
- 账号冻结 / 解冻

#### 公告管理
- 发布 / 编辑 / 删除公告
- 支持置顶和确认阅读设置

公告表单字段：

| 字段 | 必填 | 说明 |
|------|------|------|
| 公告标题 | 是 | — |
| 公告类型 | 是 | 通知类 / 招聘类 / 活动类 / 政策宣传类 / 公开政务类 |
| 公告内容 | 是 | 最多 2000 字 |
| 有效期限 | 否 | 不设置则永久有效 |
| 附件上传 | 否 | PDF、Word、图片，最多 3 个，每个不超过 10MB |
| 是否置顶 | 否 | 开关 |
| 需要确认阅读 | 否 | 开关 |

#### 投诉管理
- 投诉列表（按状态筛选）
- 处理投诉（分配责任人、填写处理说明）

#### 学习管理
- 上传 / 编辑 / 删除资料
- 控制公开/私密及首页推荐

#### 信息管理
- 录入家庭信息（姓名、身份证号、房屋编号、备注）
- 批量导入（Excel 模板）
- 数据导出（Excel 报表）
- 按姓名或身份证号搜索

---

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

### 3. 构建生产版本

```bash
npm run build
```

### 4. 重新生成 Protobuf 代码

当 `proto/` 下的 `.proto` 文件有变动时执行：

```bash
npm run proto
```

---

## 测试账号

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 村民 | 13800138000 | 123456 |
| 村干部 | 13800138001 | 123456 |

> 实际生产账号请咨询后端开发人员。

---

## 后端接口规范

### 统一网关地址

```
POST http://localhost:8080/api/gateway
Content-Type: application/x-protobuf
```

### 请求结构

```protobuf
message RequestMsg {
  MsgType msgType = 1;   // 消息类型
  string  token    = 2;  // 用户 Token（登录后获取）
  bytes   msg      = 3;  // 具体消息内容（序列化的 Protobuf）
}
```

### 响应结构

```protobuf
message ResponseMsg {
  MsgType  msgType = 1;  // 消息类型
  RespCode errCode = 2;  // 错误码（TRC_OK 表示成功）
  bytes    msg     = 3;  // 响应内容（序列化的 Protobuf）
}
```

### 消息类型清单

| 模块 | 请求 | 响应 | 说明 |
|------|------|------|------|
| 用户 | TMT_LoginReq | TMT_LoginRsp | 登录 |
| 用户 | TMT_RegisterReq | TMT_RegisterRsp | 注册 |
| 用户 | TMT_UpdateUserInfoReq | TMT_UpdateUserInfoRsp | 更新用户信息 |
| 公告 | TMT_CreateNoticeReq | TMT_CreateNoticeRsp | 创建公告 |
| 公告 | TMT_UpdateNoticeReq | TMT_UpdateNoticeRsp | 编辑公告 |
| 公告 | TMT_ListNoticeReq | TMT_ListNoticeRsp | 获取公告列表 |
| 公告 | TMT_SetNoticeReadReq | TMT_SetNoticeReadRsp | 标记已读 |
| 投诉 | TMT_CreateQuestionReq | TMT_CreateQuestionRsp | 提交投诉 |
| 投诉 | TMT_UpdateQuestionReq | TMT_UpdateQuestionRsp | 更新投诉 |
| 投诉 | TMT_ListQuestionReq | TMT_ListQuestionRsp | 获取投诉列表 |
| 学习 | TMT_CreateStudyReq | TMT_CreateStudyRsp | 上传资料 |
| 学习 | TMT_UpdateStudyReq | TMT_UpdateStudyRsp | 编辑资料 |
| 学习 | TMT_ListStudyReq | TMT_ListStudyRsp | 获取资料列表 |
| 学习 | TMT_StarStudyReq | TMT_StarStudyRsp | 收藏资料 |
| 信息 | TMT_CreatePeopleReq | TMT_CreatePeopleRsp | 录入家庭信息 |
| 信息 | TMT_UpdatePeopleReq | TMT_UpdatePeopleRsp | 更新家庭信息 |
| 信息 | TMT_ListPeopleInfoReq | TMT_ListPeopleInfoRsp | 获取信息列表 |

### 错误码

| 错误码 | 说明 |
|--------|------|
| TRC_OK | 成功 |
| TRC_ERR | 通用错误 |
| TRC_TOKEN_NOT_EXIST | Token 不存在 |
| TRC_TOKEN_INVALID | Token 无效 |
| TRC_USER_NOT_EXIST | 用户不存在 |
| TRC_USER_EXIST | 用户已存在 |
| TRC_PASSWORD_ERR | 密码错误 |
| TRC_USER_POWER_NOT_ENOUGH | 权限不足 |
| TRC_USER_IS_BAN | 账号已冻结 |
| TRC_DB_ERROR | 数据库错误 |
| TRC_REDIS_ERROR | Redis 错误 |

---

## 开发配置

### Vite 代理（开发环境）

`vite.config.js` 已配置 `/api` 代理，开发时无需处理跨域：

```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### 后端 CORS 配置（生产环境）

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

### 文件上传接口（附件功能需后端支持）

```
POST /api/upload
Content-Type: multipart/form-data

响应: { "url": "文件访问地址" }
```

---

## 代码规范

- 使用 Vue 3 Composition API + `<script setup>` 语法
- 组件文件名：PascalCase（如 `NoticeList.vue`）
- 变量名：camelCase（如 `userName`）
- 常量名：UPPER_SNAKE_CASE（如 `API_BASE_URL`）
- Token 有效期：3 天，过期后自动跳转登录页

---

## 常见问题

**Q: 启动报错 `Cannot find module '@/proto/proto.js'`**

运行以下命令重新生成 Protobuf 代码：
```bash
npm run proto
```

**Q: 请求报跨域错误**

开发环境已通过 Vite 代理处理，无需额外配置。生产环境需后端配置 CORS。

**Q: 请求报 `Network Error` 或 `ERR_CONNECTION_REFUSED`**

确认后端服务已在 `8080` 端口启动，且提供了 `/api/gateway` 接口。

**Q: 页面自动跳转到登录页**

Token 已过期（有效期 3 天），重新登录即可。
