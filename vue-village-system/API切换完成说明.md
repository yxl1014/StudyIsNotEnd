# API切换完成说明

## ✅ 已完成的工作

### 1. 网关配置修正

#### 修改的文件
- `vite.config.js` - 代理端口从 8080 改为 48080
- `src/utils/request.js` - Content-Type 改为 application/octet-stream
- `src/api/request.js` - 接口路径改为 /gateway/forward

#### 配置详情
```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:48080',  // HTTP网关端口
    changeOrigin: true
  }
}

// src/utils/request.js
headers: {
  'Content-Type': 'application/octet-stream'  // 后端期望的格式
}

// src/api/request.js
const response = await http.post('/gateway/forward', requestBuffer)
```

### 2. 全部页面切换到真实API

已切换的文件列表（共18个）：

#### 用户相关（3个）
- ✅ `src/views/Login.vue` - 登录页面
- ✅ `src/views/Register.vue` - 注册页面
- ✅ `src/views/Profile.vue` - 个人中心

#### 公告相关（3个）
- ✅ `src/views/Home.vue` - 首页（显示最新公告）
- ✅ `src/views/notice/NoticeList.vue` - 公告列表
- ✅ `src/views/notice/NoticeDetail.vue` - 公告详情

#### 投诉相关（3个）
- ✅ `src/views/complaint/ComplaintList.vue` - 投诉列表
- ✅ `src/views/complaint/ComplaintCreate.vue` - 提交投诉
- ✅ `src/views/admin/ComplaintManage.vue` - 投诉管理（管理员）

#### 学习资料相关（4个）
- ✅ `src/views/study/StudyList.vue` - 学习资料列表
- ✅ `src/views/study/StudyDetail.vue` - 学习资料详情
- ✅ `src/views/study/StudyFavorite.vue` - 我的收藏
- ✅ `src/views/admin/StudyManage.vue` - 学习资料管理（管理员）

#### 管理后台相关（3个）
- ✅ `src/views/admin/NoticeManage.vue` - 公告管理
- ✅ `src/views/admin/UserManage.vue` - 用户管理
- ✅ `src/views/admin/PeopleManage.vue` - 家庭信息管理

#### 其他（2个）
- ✅ 所有页面的导入语句已从 `@/api/*.mock.js` 改为 `@/api/*.js`

---

## 🚀 启动和测试指南

### 前置条件检查

#### 1. 后端服务启动顺序

```bash
# 1. 启动Nacos（如果未启动）
# 访问 http://localhost:8848/nacos
# 用户名/密码: nacos/123456

# 2. 确保MySQL和Redis正在运行
# MySQL: localhost:13306
# Redis: localhost:6379

# 3. 启动Dubbo服务提供者
# - town-user-provider (端口: 40001)
# - town-notice-provider
# - town-study-provider
# - town-question-provider
# - town-people-provider
# - town-update-provider
# - town-dao (端口: 40002)

# 4. 启动TCP服务器
# - town-consumer (端口: 18023, 18026)

# 5. 启动HTTP网关（最重要！）
# - town-http-gateway (端口: 48080)
```

#### 2. 验证网关是否启动

```bash
# 方法1：检查端口
lsof -i :48080

# 方法2：使用curl测试
curl -X POST http://localhost:48080/gateway/forward
# 应该返回错误（因为没有发送正确的数据），但说明网关在运行
```

### 启动前端项目

```bash
cd vue-village-system

# 安装依赖（如果还没安装）
npm install

# 启动开发服务器
npm run dev
```

访问：http://localhost:5173

---

## 🧪 测试步骤

### 1. 测试登录功能

#### 测试账号
- **村民账号**：13800138000 / 123456
- **村干部账号**：13800138001 / 123456

#### 测试步骤
1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 在登录页面输入账号密码
4. 点击登录
5. 观察网络请求

#### 预期结果
- ✅ 看到 `gateway/forward` 请求
- ✅ 请求方法：POST
- ✅ Content-Type: application/octet-stream
- ✅ 响应是二进制数据
- ✅ 登录成功，跳转到首页
- ✅ 显示用户名和头像
- ✅ Token保存到localStorage

#### 可能的错误

**错误1：网络错误 (ERR_CONNECTION_REFUSED)**
```
原因：后端服务未启动
解决：检查HTTP网关是否在48080端口运行
```

**错误2：Token无效**
```
原因：Redis未运行或Token验证失败
解决：检查Redis是否正常运行
```

**错误3：协议解析错误**
```
原因：Protobuf版本不一致
解决：确保前后端使用相同的proto文件
```

### 2. 测试公告功能

#### 测试步骤
1. 登录后进入首页
2. 点击"公告栏"
3. 查看公告列表
4. 点击某个公告查看详情

#### 预期结果
- ✅ 公告列表正常显示
- ✅ 可以按类型筛选
- ✅ 公告详情正常显示
- ✅ 置顶公告显示红色标签

### 3. 测试投诉功能

#### 测试步骤
1. 点击"民情反馈"
2. 点击"提交投诉"
3. 填写投诉信息
4. 提交投诉

#### 预期结果
- ✅ 投诉提交成功
- ✅ 返回投诉列表
- ✅ 新投诉显示在列表中
- ✅ 状态为"待处理"

### 4. 测试管理员功能

#### 测试步骤
1. 使用村干部账号登录（13800138001 / 123456）
2. 点击右上角头像 → "管理后台"
3. 测试各个管理功能

#### 预期结果
- ✅ 可以发布公告
- ✅ 可以处理投诉
- ✅ 可以上传学习资料
- ✅ 可以管理家庭信息

---

## 🔍 调试技巧

### 1. 查看Protobuf请求内容

在 `src/api/request.js` 中添加调试代码：

```javascript
// 在第22行后添加
export async function sendRequest(msgType, msgData, token = '') {
  try {
    const proto = await import('@/proto/proto.js').catch(() => {
      throw new Error('Protobuf文件未生成，请先运行: npm run proto')
    })

    const { RequestMsg, ResponseMsg, RespCode } = proto

    // 调试：打印请求信息
    console.log('📤 发送请求:', {
      msgType: msgType,
      token: token ? '***' : '(空)',
      msgData: msgData
    })

    const requestMsg = RequestMsg.create({
      msgType: msgType,
      token: token,
      msg: msgData ? msgData.constructor.encode(msgData).finish() : new Uint8Array()
    })

    const requestBuffer = RequestMsg.encode(requestMsg).finish()

    // 调试：打印二进制数据长度
    console.log('📦 请求数据大小:', requestBuffer.length, 'bytes')

    const response = await http.post('/gateway/forward', requestBuffer)

    const responseMsg = ResponseMsg.decode(new Uint8Array(response.data))

    // 调试：打印响应信息
    console.log('📥 接收响应:', {
      errCode: responseMsg.errCode,
      msgType: responseMsg.msgType,
      success: responseMsg.errCode === RespCode.TRC_OK
    })

    if (responseMsg.errCode !== RespCode.TRC_OK) {
      const errorMsg = getErrorMessage(responseMsg.errCode, RespCode)
      throw new Error(errorMsg)
    }

    return responseMsg

  } catch (error) {
    console.error('❌ 请求失败:', error)
    throw error
  }
}
```

### 2. 查看后端日志

关注后端控制台输出：
- TCP连接日志
- Token验证日志
- 请求处理日志
- 错误日志

### 3. 使用浏览器开发者工具

#### Network标签
- 查看请求URL
- 查看请求头
- 查看请求体（二进制）
- 查看响应状态码
- 查看响应体（二进制）

#### Console标签
- 查看前端日志
- 查看错误信息
- 查看调试输出

#### Application标签
- 查看localStorage中的Token
- 查看用户信息

---

## 📊 完整的请求流程

```
前端 (Vue)
  ↓
1. 用户操作（登录、查看公告等）
  ↓
2. 调用API函数（如 login(userTel, userPwd)）
  ↓
3. 构建Protobuf消息（如 LoginReq）
  ↓
4. 包装成RequestMsg (msgType + token + msg)
  ↓
5. 序列化为二进制 (Uint8Array)
  ↓
6. HTTP POST /api/gateway/forward
  ↓
Vite代理 (localhost:5173 → localhost:48080)
  ↓
HTTP网关 (town-http-gateway:48080)
  ↓
7. 转发到TCP服务器 (Netty Client)
  ↓
TCP服务器 (town-consumer:18023)
  ↓
8. 解码Protobuf (RequestMsg)
  ↓
9. 验证Token (TokenLoginInHandler)
  ↓
10. 分发任务 (TaskDistributeInHandler)
  ↓
11. 调用Dubbo服务 (ServiceManager)
  ↓
业务服务 (town-user-provider等)
  ↓
12. 返回ResponseMsg
  ↓
13. 编码为Protobuf二进制
  ↓
TCP → HTTP网关 → 前端
  ↓
14. 解码ResponseMsg
  ↓
15. 检查errCode
  ↓
16. 解析业务响应 (如 LoginRsp)
  ↓
17. 更新UI
  ↓
完成
```

---

## ⚠️ 常见问题

### Q1: 登录后立即提示Token无效？
**原因**：Redis中没有Token或Token已过期
**解决**：
1. 检查Redis是否正常运行
2. 检查后端是否正确保存Token到Redis
3. 检查Token有效期设置（默认24小时）

### Q2: 请求一直pending，没有响应？
**原因**：后端服务未启动或网络不通
**解决**：
1. 检查HTTP网关是否在48080端口运行
2. 检查TCP服务器是否在18023端口运行
3. 检查Dubbo服务是否都已注册到Nacos

### Q3: 提示"协议解析错误"？
**原因**：前后端Protobuf版本不一致
**解决**：
1. 确保前后端使用相同的proto文件
2. 重新生成proto.js：`npm run proto`
3. 重启前端项目

### Q4: 某些功能正常，某些功能报错？
**原因**：部分Dubbo服务未启动
**解决**：
1. 检查Nacos控制台，确认所有服务都已注册
2. 启动缺失的服务
3. 查看后端日志，确认服务调用是否成功

### Q5: 管理员功能无法访问？
**原因**：权限不足
**解决**：
1. 确认使用村干部账号登录（13800138001）
2. 检查userPower字段是否为 'TUP_CGM'
3. 检查后端权限验证逻辑

---

## 📝 后续工作

### 需要后端配合的功能

1. **文件上传接口**
   - 公告附件上传
   - 学习资料文件上传
   - 投诉图片上传

2. **文件下载接口**
   - 公告附件下载
   - 学习资料下载

3. **数据导入导出**
   - 家庭信息Excel导入
   - 数据导出功能

### 前端优化建议

1. **错误处理优化**
   - 添加更友好的错误提示
   - 添加重试机制
   - 添加离线提示

2. **性能优化**
   - 添加请求缓存
   - 添加分页加载
   - 优化大列表渲染

3. **用户体验优化**
   - 添加加载动画
   - 添加骨架屏
   - 优化移动端适配

---

## ✅ 切换完成检查清单

- [x] Vite代理端口配置：48080
- [x] Content-Type配置：application/octet-stream
- [x] 接口路径配置：/gateway/forward
- [x] 所有Vue文件切换到真实API
- [x] Protobuf文件已生成
- [x] 所有API函数已实现
- [ ] 后端服务已启动（需要您启动）
- [ ] 测试登录功能（需要您测试）
- [ ] 测试各模块功能（需要您测试）

---

## 🎉 总结

前端配置已全部完成，所有页面都已切换到真实API模式。现在只需要：

1. **启动后端服务**（特别是HTTP网关）
2. **启动前端项目**（npm run dev）
3. **开始测试**

如果遇到任何问题，请查看上面的"常见问题"部分或查看浏览器控制台的错误信息。

祝测试顺利！🚀
