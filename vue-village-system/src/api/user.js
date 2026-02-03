/**
 * 用户相关API
 *
 * 注意：此文件依赖proto文件生成的代码
 * 在proto文件生成前，这些函数会抛出错误
 */

import { sendRequest } from './request'

/**
 * 用户登录
 * @param {number} userTel - 手机号
 * @param {string} userPwd - 密码
 * @returns {Promise<{token: string, userInfo: Object}>}
 */
export async function login(userTel, userPwd) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, LoginReq, LoginRsp } = proto

  // 构建登录请求
  const loginReq = LoginReq.create({
    userTel: userTel,
    userPwd: userPwd
  })

  // 发送请求
  const response = await sendRequest(MsgType.TMT_LoginReq, loginReq, '')

  // 解析响应
  const loginRsp = LoginRsp.decode(response.msg)

  return {
    token: loginRsp.token,
    userInfo: {
      userTel: loginRsp.userInfo.userTel,
      userName: loginRsp.userInfo.userName,
      userPower: loginRsp.userInfo.userPower,
      flagType: loginRsp.userInfo.flagType,
      userCreateTime: loginRsp.userInfo.userCreateTime
    }
  }
}

/**
 * 用户注册
 * @param {Object} userInfo - 用户信息
 * @returns {Promise<void>}
 */
export async function register(userInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, RegisterReq, UserInfo } = proto

  const userInfoProto = UserInfo.create({
    userTel: userInfo.userTel,
    userName: userInfo.userName,
    userPwd: userInfo.userPwd,
    userPower: userInfo.userPower || 0, // 默认为村民
    flagType: userInfo.flagType || 0
  })

  const registerReq = RegisterReq.create({
    userInfo: userInfoProto
  })

  await sendRequest(MsgType.TMT_RegisterReq, registerReq, '')
}

/**
 * 更新用户信息
 * @param {Object} userInfo - 用户信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateUserInfo(userInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateUserInfoReq, UserInfo } = proto
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()

  const userInfoProto = UserInfo.create(userInfo)

  const updateReq = UpdateUserInfoReq.create({
    userInfo: userInfoProto,
    isDel: isDel
  })

  await sendRequest(MsgType.TMT_UpdateUserInfoReq, updateReq, userStore.token)
}

/**
 * 获取待通知用户信息列表
 * @returns {Promise<Array>}
 */
export async function getNotifyUserList() {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListNotifyUserInfoReq, ListNotifyUserInfoRsp } = proto
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()

  const listReq = ListNotifyUserInfoReq.create({})

  const response = await sendRequest(MsgType.TMT_ListNotifyUserInfoReq, listReq, userStore.token)

  const listRsp = ListNotifyUserInfoRsp.decode(response.msg)

  return listRsp.infosList || []
}
