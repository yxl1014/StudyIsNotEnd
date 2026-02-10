import http from '@/utils/request'

/**
 * 发送Protobuf请求的通用方法
 * 注意：这个文件需要在后端提供proto文件后，配合生成的proto.js使用
 *
 * 使用示例：
 * import { RequestMsg, ResponseMsg, MsgType, LoginReq, LoginRsp } from '@/proto/proto'
 *
 * const loginReq = LoginReq.create({ userTel: 123, userPwd: 'xxx' })
 * const response = await sendRequest(MsgType.TMT_LoginReq, loginReq, '')
 * const loginRsp = LoginRsp.decode(response.msg)
 */

/**
 * 发送Protobuf请求
 * @param {number} msgType - 消息类型（MsgType枚举值）
 * @param {Object} msgData - 消息数据（Protobuf Message对象）
 * @param {string} token - 用户Token
 * @returns {Promise<ResponseMsg>} 返回ResponseMsg对象
 */
export async function sendRequest(msgType, msgData, token = '') {
  try {
    // 动态导入proto（避免在proto文件未生成时报错）
    const proto = await import('@/proto/proto.js').catch(() => {
      throw new Error('Protobuf文件未生成，请先运行: npm run proto')
    })

    const { RequestMsg, ResponseMsg, RespCode, MsgType } = proto.po

    console.log('========================================')
    console.log('📤 发送请求 - 明文数据')
    console.log('========================================')
    console.log('消息类型编号:', msgType)
    console.log('消息类型名称:', getMsgTypeName(msgType, MsgType))
    console.log('Token:', token || '(无Token)')

    // 打印请求数据的明文
    if (msgData) {
      console.log('请求数据类型:', msgData.constructor.name)
      console.log('请求数据对象:', msgData)
      try {
        const jsonData = msgData.toJSON ? msgData.toJSON() : msgData
        console.log('请求数据JSON:', JSON.stringify(jsonData, null, 2))
      } catch (e) {
        console.log('请求数据无法转换为JSON')
      }
    } else {
      console.log('请求数据: (空)')
    }
    console.log('========================================')

    // 1. 获取msgData的类型并编码
    let msgBytes = new Uint8Array()
    if (msgData) {
      // 使用 constructor.encode 进行编码（protobufjs 生成的类）
      const MessageType = msgData.constructor
      msgBytes = MessageType.encode(msgData).finish()
    }

    // 2. 构建RequestMsg
    const requestMsg = RequestMsg.create({
      msgType: msgType,
      token: token,
      msg: msgBytes
    })

    // 3. 序列化为二进制
    const requestBuffer = RequestMsg.encode(requestMsg).finish()

    console.log('📦 二进制数据长度:', requestBuffer.length, 'bytes')

    // 4. 将 Uint8Array 转换为 ArrayBuffer（axios 需要这样才能正确发送二进制数据）
    const arrayBuffer = requestBuffer.buffer.slice(
      requestBuffer.byteOffset,
      requestBuffer.byteOffset + requestBuffer.byteLength
    )

    // 5. 发送HTTP请求
    const response = await http.post('/gateway/forward', arrayBuffer)

    // 6. 解析响应
    const responseMsg = ResponseMsg.decode(new Uint8Array(response.data))

    console.log('========================================')
    console.log('📥 接收响应 - 明文数据')
    console.log('========================================')
    console.log('消息类型编号:', responseMsg.msgType)
    console.log('消息类型名称:', getMsgTypeName(responseMsg.msgType, MsgType))
    console.log('错误码:', responseMsg.errCode, '(' + getErrorMessage(responseMsg.errCode, RespCode) + ')')
    console.log('错误信息:', responseMsg.errMsg || '(无)')
    console.log('响应数据长度:', responseMsg.msg ? responseMsg.msg.length : 0, 'bytes')
    console.log('完整ResponseMsg对象:', responseMsg)
    console.log('========================================')

    // 7. 检查错误码
    if (responseMsg.errCode !== RespCode.TRC_OK) {
      const errorMsg = getErrorMessage(responseMsg.errCode, RespCode)
      console.error('❌ 请求失败:', errorMsg)
      throw new Error(errorMsg)
    }

    console.log('✅ 请求成功')
    console.log(' ')
    return responseMsg

  } catch (error) {
    console.error('❌ 请求异常:', error)
    throw error
  }
}

/**
 * 获取消息类型名称
 */
function getMsgTypeName(msgType, MsgType) {
  for (const key in MsgType) {
    if (MsgType[key] === msgType) {
      return key
    }
  }
  return '未知类型'
}

/**
 * 获取错误信息
 */
function getErrorMessage(errCode, RespCode) {
  const errorMap = {
    [RespCode.TRC_ERR]: '请求失败',
    [RespCode.TRC_TOKEN_NOT_EXIST]: 'Token不存在，请重新登录',
    [RespCode.TRC_TOKEN_INVALID]: 'Token无效，请重新登录',
    [RespCode.TRC_USER_NOT_EXIST]: '用户不存在',
    [RespCode.TRC_USER_EXIST]: '用户已存在',
    [RespCode.TRC_PASSWORD_ERR]: '密码错误',
    [RespCode.TRC_USER_POWER_NOT_ENOUGH]: '权限不足',
    [RespCode.TRC_PARAM_NULL]: '参数为空',
    [RespCode.TRC_DB_ERROR]: '数据库错误',
    [RespCode.TRC_REDIS_ERROR]: 'Redis错误',
    [RespCode.TRC_REQUEST_BODY_NULL]: '请求体为空',
    [RespCode.TRC_PARSE_PROTOCOL_ERR]: '协议解析错误',
    [RespCode.TRC_USER_IS_BAN]: '用户已被冻结'
  }

  return errorMap[errCode] || `未知错误: ${errCode}`
}

/**
 * 简化的请求方法（自动处理token）
 */
export async function request(msgType, msgData) {
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()
  return sendRequest(msgType, msgData, userStore.token)
}
