# Protobuf 配置说明

## 步骤1：从后端获取proto文件

请将后端项目中的所有 `.proto` 文件复制到此目录。

通常这些文件位于后端项目的：
- `town-api/src/main/proto/` 目录

需要复制的文件可能包括：
- `common.proto` - 公共定义
- `user.proto` - 用户相关
- `notice.proto` - 公告相关
- `question.proto` - 投诉相关
- `study.proto` - 学习资料相关
- `people.proto` - 家庭信息相关

## 步骤2：生成JavaScript代码

在项目根目录运行：

```bash
npm run proto
```

这会生成：
- `src/proto/proto.js` - Protobuf JavaScript代码
- `src/proto/proto.d.ts` - TypeScript类型定义

## 步骤3：验证生成结果

检查 `src/proto/proto.js` 文件是否包含以下内容：
- RequestMsg
- ResponseMsg
- MsgType枚举
- RespCode枚举
- 各种业务消息类型（LoginReq, LoginRsp等）

## 注意事项

1. **proto文件必须与后端保持一致**
   - 每次后端更新proto文件后，需要重新复制并生成

2. **生成的文件不要手动修改**
   - proto.js 和 proto.d.ts 是自动生成的
   - 已添加到 .gitignore，不会提交到版本控制

3. **如果生成失败**
   - 检查是否安装了 protobufjs-cli: `npm install -g protobufjs-cli`
   - 检查proto文件语法是否正确
   - 查看错误信息并修复

## 示例proto文件结构

```protobuf
syntax = "proto3";

package village;

// 消息类型枚举
enum MsgType {
  TMT_UNKNOWN = 0;
  TMT_LoginReq = 1;
  TMT_LoginRsp = 2;
  // ...
}

// 错误码枚举
enum RespCode {
  TRC_OK = 0;
  TRC_ERR = 1;
  // ...
}

// 请求消息
message RequestMsg {
  MsgType msgType = 1;
  string token = 2;
  bytes msg = 3;
}

// 响应消息
message ResponseMsg {
  MsgType msgType = 1;
  RespCode errCode = 2;
  bytes msg = 3;
}

// 登录请求
message LoginReq {
  int32 userTel = 1;
  string userPwd = 2;
}

// 登录响应
message LoginRsp {
  string token = 1;
  UserInfo userInfo = 2;
}
```
