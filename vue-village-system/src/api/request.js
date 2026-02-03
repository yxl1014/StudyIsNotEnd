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

    const { RequestMsg, ResponseMsg, RespCode } = proto

    // 1. 构建RequestMsg
    const requestMsg = RequestMsg.create({
      msgType: msgType,
      token: token,
      msg: msgData ? msgData.constructor.encode(msgData).finish() : new Uint8Array()
    })

    // 2. 序列化为二进制
    const requestBuffer = RequestMsg.encode(requestMsg).finish()

    // 3. 发送HTTP请求
    const response = await http.post('/gateway', requestBuffer)

    // 4. 解析响应
    const responseMsg = ResponseMsg.decode(new Uint8Array(response.data))

    // 5. 检查错误码
    if (responseMsg.errCode !== RespCode.TRC_OK) {
      const errorMsg = getErrorMessage(responseMsg.errCode, RespCode)
      throw new Error(errorMsg)
    }

    return responseMsg

  } catch (error) {
    console.error('请求失败:', error)
    throw error
  }
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
