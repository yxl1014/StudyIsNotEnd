# 村务管理系统 - 前端

基于 Vue 3 + Vite + Element Plus + Protobuf.js 开发的村务管理系统前端。

## 功能模块

### 1. 用户模块
- 登录/注册
- 角色权限管理（村民/村干部）
- 用户信息管理
- Token认证

### 2. 公告模块
- 公告列表查看
- 公告详情
- 公告发布（村干部）
- 公告编辑/删除
- 类型筛选
- 已读标记

### 3. 投诉模块
- 投诉提交
- 投诉列表
- 进度查询
- 投诉处理（村干部）
- 分配责任人
- 统计导出

### 4. 学习模块
- 学习资料列表
- 资料详情
- 资料上传（村干部）
- 分类搜索
- 收藏功能
- 在线预览

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **数据序列化**: Protocol Buffers (protobuf.js)

## 项目结构

```
vue-village-system/
├── public/                 # 静态资源
├── proto/                  # Protobuf定义文件（从后端复制）
├── src/
│   ├── api/               # API接口封装
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── proto/             # 生成的Protobuf代码
│   ├── router/            # 路由配置
│   ├── stores/            # Pinia状态管理
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 开发指南

### 安装依赖

```bash
npm install
```

### 配置Protobuf

1. 从后端项目复制 `.proto` 文件到 `proto/` 目录
2. 生成JavaScript代码：

```bash
npm run proto
```

### 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:5173

### 构建生产版本

```bash
npm run build
```

## 与后端对接

### HTTP接口

后端需要提供一个统一的HTTP接口：

```
POST /api/gateway
Content-Type: application/x-protobuf
```

接收和返回Protobuf二进制数据。

### 请求格式

```protobuf
message RequestMsg {
  MsgType msgType = 1;    // 消息类型
  string token = 2;        // 用户Token
  bytes msg = 3;           // 具体消息内容（序列化后的Protobuf）
}
```

### 响应格式

```protobuf
message ResponseMsg {
  MsgType msgType = 1;     // 消息类型
  RespCode errCode = 2;    // 错误码
  bytes msg = 3;           // 响应内容（序列化后的Protobuf）
}
```

## 开发规范

### 命名规范

- 组件名：PascalCase (如 `NoticeList.vue`)
- 文件名：kebab-case (如 `user-card.vue`)
- 变量名：camelCase (如 `userName`)
- 常量名：UPPER_SNAKE_CASE (如 `API_BASE_URL`)

### 代码风格

- 使用 Composition API
- 使用 `<script setup>` 语法
- 优先使用组合式函数 (Composables)
- 统一使用单引号

### Git提交规范

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

## 常见问题

### 1. Protobuf生成失败

确保已安装 `protobufjs-cli`:

```bash
npm install -g protobufjs-cli
```

### 2. 跨域问题

开发环境已配置代理，生产环境需要后端配置CORS。

### 3. Token过期

Token过期会自动跳转到登录页，需要重新登录。

## 联系方式

如有问题，请联系开发团队。
