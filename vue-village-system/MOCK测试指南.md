# 前端Mock测试完整指南

## 🎯 目的

在后端HTTP网关未完成前，使用Mock数据进行前端功能测试。

---

## 📦 已创建的Mock文件

所有Mock文件都在 `src/api/` 目录下：

- ✅ `user.mock.js` - 用户登录、注册、更新
- ✅ `notice.mock.js` - 公告列表、创建、更新、已读
- ✅ `complaint.mock.js` - 投诉列表、创建、更新
- ✅ `study.mock.js` - 学习资料列表、创建、更新、收藏
- ✅ `people.mock.js` - 家庭信息列表、创建、更新

---

## 🚀 快速开始（3步）

### 第1步：修改登录页面使用Mock

打开 `src/views/Login.vue`，修改第7行的导入语句：

```javascript
// 原来的导入（连接真实后端）
import { login } from '@/api/user'

// 改为Mock导入（使用模拟数据）
import { login } from '@/api/user.mock'
```

### 第2步：启动项目

```bash
npm run dev
```

### 第3步：登录测试

访问 http://localhost:5173，使用测试账号登录：

**村民账号**:
- 手机号: `13800138000`
- 密码: `123456`

**村干部账号**:
- 手机号: `13800138001`
- 密码: `123456`

---

## 📝 详细使用说明

### 如何在页面中使用Mock数据

#### 示例1：公告列表页面

打开 `src/views/notice/NoticeList.vue`，找到第11行：

```javascript
// 修改前
import { getNoticeList } from '@/api/notice'

// 修改后
import { getNoticeList } from '@/api/notice.mock'
```

#### 示例2：投诉列表页面

打开 `src/views/complaint/ComplaintList.vue`，找到第11行：

```javascript
// 修改前
import { getComplaintList } from '@/api/complaint'

// 修改后
import { getComplaintList } from '@/api/complaint.mock'
```

#### 示例3：学习资料页面

打开 `src/views/study/StudyList.vue`，找到第11行：

```javascript
// 修改前
import { getStudyList, toggleStarStudy } from '@/api/study'

// 修改后
import { getStudyList, toggleStarStudy } from '@/api/study.mock'
```

---

## 🎨 需要修改的页面清单

### 村民端页面（8个）

| 页面 | 文件路径 | 需要修改的导入 |
|------|---------|---------------|
| 登录页 | `src/views/Login.vue` | `@/api/user` → `@/api/user.mock` |
| 注册页 | `src/views/Register.vue` | `@/api/user` → `@/api/user.mock` |
| 首页 | `src/views/Home.vue` | `@/api/notice` → `@/api/notice.mock` |
| 个人中心 | `src/views/Profile.vue` | `@/api/user` → `@/api/user.mock` |
| 公告列表 | `src/views/notice/NoticeList.vue` | `@/api/notice` → `@/api/notice.mock` |
| 公告详情 | `src/views/notice/NoticeDetail.vue` | `@/api/notice` → `@/api/notice.mock` |
| 投诉列表 | `src/views/complaint/ComplaintList.vue` | `@/api/complaint` → `@/api/complaint.mock` |
| 提交投诉 | `src/views/complaint/ComplaintCreate.vue` | `@/api/complaint` → `@/api/complaint.mock` |
| 学习列表 | `src/views/study/StudyList.vue` | `@/api/study` → `@/api/study.mock` |
| 学习详情 | `src/views/study/StudyDetail.vue` | `@/api/study` → `@/api/study.mock` |
| 我的收藏 | `src/views/study/StudyFavorite.vue` | `@/api/study` → `@/api/study.mock` |

### 村干部后台页面（6个）

| 页面 | 文件路径 | 需要修改的导入 |
|------|---------|---------------|
| 工作台 | `src/views/admin/Dashboard.vue` | `@/api/notice` → `@/api/notice.mock`<br>`@/api/complaint` → `@/api/complaint.mock` |
| 用户管理 | `src/views/admin/UserManage.vue` | `@/api/user` → `@/api/user.mock` |
| 公告管理 | `src/views/admin/NoticeManage.vue` | `@/api/notice` → `@/api/notice.mock` |
| 投诉管理 | `src/views/admin/ComplaintManage.vue` | `@/api/complaint` → `@/api/complaint.mock` |
| 学习管理 | `src/views/admin/StudyManage.vue` | `@/api/study` → `@/api/study.mock` |
| 信息管理 | `src/views/admin/PeopleManage.vue` | `@/api/people` → `@/api/people.mock` |

---

## 📊 Mock数据说明

### 用户数据

**村民账号**:
- 手机号: 13800138000
- 密码: 123456
- 姓名: 张三
- 权限: 村民

**村干部账号**:
- 手机号: 13800138001
- 密码: 123456
- 姓名: 李村长
- 权限: 村干部

### 公告数据（4条）

1. **关于开展春季环境卫生整治的通知** - 置顶、需确认阅读
2. **村委会招聘保洁员公告** - 招聘类
3. **端午节文艺活动通知** - 活动类
4. **农村医保政策解读** - 政策宣传类

### 投诉数据（3条）

1. **环境卫生** - 待处理
2. **基础设施损坏** - 处理中
3. **邻里纠纷** - 已完成

### 学习资料（4条）

1. **2024年中央一号文件解读** - 政策解读（置顶）
2. **春季蔬菜种植技术要点** - 农技推广
3. **老年人健康养生知识** - 健康养生（已收藏）
4. **农村土地承包法律知识** - 法律常识

### 家庭信息（3条）

1. 张三 - 110101199001011234 - A001
2. 李四 - 110101199205155678 - A002
3. 王五 - 110101198803209012 - A003

---

## ⚠️ 重要提示

### Mock数据的特点

1. **数据存在内存中** - 刷新页面后会重置
2. **写操作不会保存** - 创建、更新、删除只会在控制台打印日志
3. **模拟网络延迟** - 每个请求有300-500ms的延迟，模拟真实网络
4. **仅用于前端测试** - 不会影响真实数据库

### 可以测试的功能

✅ **可以测试**:
- 页面布局和样式
- 交互逻辑和流程
- 表单验证
- 权限控制
- 路由跳转
- 数据展示

❌ **无法测试**:
- 真实的数据持久化
- 文件上传下载
- 真实的Token验证
- 跨页面的数据同步

---

## 🔄 切换回真实后端

当后端HTTP网关完成后，只需要将所有Mock导入改回原始导入：

### 方法1：手动修改（推荐）

逐个文件修改导入语句：

```javascript
// 从Mock版本
import { login } from '@/api/user.mock'

// 改回真实版本
import { login } from '@/api/user'
```

### 方法2：全局查找替换

使用编辑器的查找替换功能：

1. 查找: `.mock'`
2. 替换为: `'`
3. 全部替换

---

## 🐛 常见问题

### Q1: 登录后刷新页面又回到登录页？

**原因**: Mock模式下Token存在localStorage，但刷新后Mock数据重置。

**解决**: 这是正常现象，重新登录即可。真实后端不会有这个问题。

### Q2: 创建的数据刷新后消失了？

**原因**: Mock数据存在内存中，刷新页面会重置。

**解决**: 这是正常现象。真实后端会保存到数据库。

### Q3: 如何查看Mock请求的日志？

**解决**: 打开浏览器控制台（F12），查看Console标签页。

### Q4: 可以修改Mock数据吗？

**解决**: 可以！直接编辑 `src/api/*.mock.js` 文件，修改返回的数据。

---

## 💡 测试建议

### 测试流程

1. **登录测试**
   - 测试村民账号登录
   - 测试村干部账号登录
   - 测试错误密码提示

2. **村民端测试**
   - 查看公告列表
   - 查看公告详情
   - 提交投诉
   - 浏览学习资料
   - 收藏学习资料

3. **村干部后台测试**
   - 查看工作台统计
   - 发布公告
   - 处理投诉
   - 上传学习资料
   - 管理用户

4. **权限测试**
   - 用村民账号尝试访问后台（应该被拦截）
   - 用村干部账号访问后台（应该正常）

---

## 📞 需要帮助？

如果遇到问题：

1. 检查浏览器控制台是否有错误
2. 确认导入路径是否正确（`.mock`）
3. 确认Mock文件是否存在
4. 查看本文档的常见问题部分

---

## 🎉 开始测试

现在您可以：

1. 修改登录页面的导入语句
2. 运行 `npm run dev`
3. 使用测试账号登录
4. 测试各个功能模块

祝测试顺利！🚀
