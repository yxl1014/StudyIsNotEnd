# Mock数据使用说明

## 📝 说明

在后端HTTP网关未完成前，使用Mock数据进行前端测试。

## 🔄 如何切换Mock模式

### 方法1：修改导入路径（推荐）

在需要测试的页面组件中，将API导入路径改为Mock版本：

```javascript
// 原始导入（连接真实后端）
import { login } from '@/api/user'

// 改为Mock导入（使用模拟数据）
import { login } from '@/api/user.mock'
```

### 方法2：全局替换

如果想全局使用Mock数据，可以修改各个页面组件的导入语句。

## 📦 已提供的Mock文件

- `user.mock.js` - 用户相关Mock数据
- `notice.mock.js` - 公告相关Mock数据
- `complaint.mock.js` - 投诉相关Mock数据
- `study.mock.js` - 学习资料Mock数据
- `people.mock.js` - 家庭信息Mock数据

## 🔑 测试账号

### 村民账号
- 手机号: `13800138000`
- 密码: `123456`

### 村干部账号
- 手机号: `13800138001`
- 密码: `123456`

## 📋 Mock数据说明

### 公告数据
- 4条模拟公告
- 包含不同类型：通知类、招聘类、活动类、政策宣传类
- 第一条公告设置为置顶

### 投诉数据
- 3条模拟投诉
- 包含不同状态：待处理、处理中、已完成
- 包含不同类别：环境卫生、基础设施、邻里纠纷

### 学习资料
- 4条模拟学习资料
- 包含不同分类：政策解读、农技推广、健康养生、法律常识
- 包含阅读次数、收藏状态

### 家庭信息
- 3条模拟家庭信息
- 包含姓名、身份证号、房屋编号、备注

## 🚀 使用示例

### 登录页面使用Mock

```javascript
// src/views/Login.vue
<script setup>
// 使用Mock数据
import { login } from '@/api/user.mock'

const handleLogin = async () => {
  const result = await login(form.userTel, form.userPwd)
  // ... 其他逻辑
}
</script>
```

### 公告列表使用Mock

```javascript
// src/views/notice/NoticeList.vue
<script setup>
// 使用Mock数据
import { getNoticeList } from '@/api/notice.mock'

const loadNotices = async () => {
  const list = await getNoticeList(page.value, size.value)
  // ... 其他逻辑
}
</script>
```

## ⚠️ 注意事项

1. **Mock数据仅用于前端测试**，不会真正保存到数据库
2. **刷新页面后数据会重置**，因为数据存在内存中
3. **后端接口完成后**，记得将导入路径改回原始版本
4. **所有写操作**（创建、更新、删除）只会在控制台打印日志

## 🔄 切换回真实后端

当后端HTTP网关完成后，将所有Mock导入改回原始导入：

```javascript
// 从Mock版本
import { login } from '@/api/user.mock'

// 改回真实版本
import { login } from '@/api/user'
```

## 💡 提示

- 可以在浏览器控制台查看Mock数据的日志输出
- 可以根据需要修改Mock数据的内容
- 可以添加更多Mock数据来测试不同场景
