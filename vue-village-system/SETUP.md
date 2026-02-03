# 村务管理系统前端 - 快速开始指南

## ✅ 项目已完成配置

Proto文件已成功配置，JavaScript代码已生成！

## 🚀 立即运行项目

### 1. 安装依赖

```bash
cd vue-village-system
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问: **http://localhost:5173**

### 3. 测试账号

**村民账号**（测试用）:
- 手机号: 13800138000
- 密码: 123456

**村干部账号**（测试用）:
- 手机号: 13800138001
- 密码: 123456

（实际账号请咨询后端开发人员）

---

## 📋 已完成的配置

### ✅ Proto文件配置
- `proto/base.proto` - 基础定义
- `proto/protocol.proto` - 协议定义
- `proto/entity.proto` - 实体定义

### ✅ 生成的代码
- `src/proto/proto.js` (736KB) - Protobuf JavaScript代码
- `src/proto/proto.d.ts` (276KB) - TypeScript类型定义

### ✅ 支持的消息类型
- 用户相关: Login, Register, UpdateUserInfo
- 公告相关: CreateNotice, UpdateNotice, ListNotice, SetNoticeRead
- 投诉相关: CreateQuestion, UpdateQuestion, ListQuestion
- 学习相关: CreateStudy, UpdateStudy, ListStudy, StarStudy
- 家庭信息: CreatePeople, UpdatePeople, ListPeopleInfo
- 更新记录: ListUpdateInfo

---

## 🎯 功能模块

### 村民端功能
- ✅ 用户登录/注册
- ✅ 查看公告列表和详情
- ✅ 标记公告已读
- ✅ 提交投诉
- ✅ 查看投诉处理进度
- ✅ 浏览学习资料
- ✅ 收藏学习资料
- ✅ 查看个人信息

### 村干部后台功能
- ✅ 工作台（数据统计）
- ✅ 用户管理（查看、冻结/解冻）
- ✅ 公告管理（发布、编辑、删除、置顶）
- ✅ 投诉管理（查看、处理、回复）
- ✅ 学习资料管理（上传、编辑、删除）
- ✅ 家庭信息管理（录入、批量导入、导出）

---

## ⚙️ 后端接口要求

### HTTP网关接口

后端需要提供一个统一的HTTP接口：

```
POST http://localhost:8080/api/gateway
Content-Type: application/x-protobuf
```

**请求格式**:
```protobuf
message RequestMsg {
  MsgType msgType = 1;    // 消息类型
  string token = 2;        // 用户Token
  bytes msg = 3;           // 具体消息内容
}
```

**响应格式**:
```protobuf
message ResponseMsg {
  MsgType msgType = 1;     // 消息类型
  RespCode errCode = 2;    // 错误码
  bytes msg = 3;           // 响应内容
}
```

### 跨域配置

后端需要配置CORS，允许前端访问：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); // Vue开发服务器
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

---

## 📁 项目结构

```
vue-village-system/
├── proto/                  # Protobuf定义文件 ✅
│   ├── base.proto
│   ├── protocol.proto
│   └── entity.proto
├── src/
│   ├── api/               # API接口封装 ✅
│   │   ├── request.js     # Protobuf请求封装
│   │   ├── user.js
│   │   ├── notice.js
│   │   ├── complaint.js
│   │   ├── study.js
│   │   └── people.js
│   ├── proto/             # 生成的Protobuf代码 ✅
│   │   ├── proto.js       # 736KB
│   │   └── proto.d.ts     # 276KB
│   ├── router/            # 路由配置 ✅
│   ├── stores/            # 状态管理 ✅
│   ├── utils/             # 工具函数 ✅
│   ├── views/             # 页面组件 ✅
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   ├── Home.vue
│   │   ├── Profile.vue
│   │   ├── notice/        # 公告模块
│   │   ├── complaint/     # 投诉模块
│   │   ├── study/         # 学习模块
│   │   └── admin/         # 村干部后台
│   ├── App.vue
│   └── main.js
├── package.json
├── vite.config.js
└── README.md
```

---

## 🔧 常见命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 重新生成Protobuf代码（当proto文件更新时）
npm run proto
```

---

## 🐛 常见问题

### 1. 启动失败：找不到proto模块

**错误**: `Cannot find module '@/proto/proto.js'`

**解决**: 确保已运行 `npm run proto` 生成代码

### 2. 跨域错误

**错误**: `Access to XMLHttpRequest has been blocked by CORS policy`

**解决**:
- 开发环境：已配置代理，无需处理
- 生产环境：后端需要配置CORS

### 3. Token过期

**现象**: 自动跳转到登录页

**解决**: 重新登录即可，Token有效期为3天

### 4. 后端连接失败

**错误**: `Network Error` 或 `ERR_CONNECTION_REFUSED`

**解决**:
1. 确保后端服务已启动（端口8080）
2. 检查后端是否提供了 `/api/gateway` 接口
3. 检查防火墙设置

---

## 📞 技术支持

如有问题，请联系：
- 前端开发：[您的联系方式]
- 后端开发：[后端同事联系方式]

---

## 🎉 开始使用

现在您可以运行以下命令启动项目：

```bash
cd vue-village-system
npm install
npm run dev
```

然后在浏览器访问: **http://localhost:5173**

祝您使用愉快！
