/**
 * 用户相关API - Mock版本
 *
 * 用于在后端接口未完成时进行前端测试
 */

// 导入原始的sendRequest函数（暂时不用）
// import { sendRequest } from './request'

/**
 * 用户登录 - Mock版本
 */
export async function login(userTel, userPwd) {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 500))

  // 模拟登录验证
  if (userTel === 13800138000 && userPwd === '123456') {
    // 村民账号
    return {
      token: 'mock_token_villager_' + Date.now(),
      userInfo: {
        userTel: 13800138000,
        userName: '张三',
        userPower: 'TUP_CM', // 村民
        flagType: 0,
        userCreateTime: Date.now() - 86400000 * 30
      }
    }
  } else if (userTel === 13800138001 && userPwd === '123456') {
    // 村干部账号
    return {
      token: 'mock_token_admin_' + Date.now(),
      userInfo: {
        userTel: 13800138001,
        userName: '李村长',
        userPower: 'TUP_CGM', // 村干部
        flagType: 0,
        userCreateTime: Date.now() - 86400000 * 60
      }
    }
  } else {
    throw new Error('手机号或密码错误')
  }
}

/**
 * 用户注册 - Mock版本
 */
export async function register(userInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  // 模拟注册成功
  console.log('注册信息:', userInfo)
  return { success: true }
}

/**
 * 更新用户信息 - Mock版本
 */
export async function updateUserInfo(userInfo, isDel = false) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('更新用户信息:', userInfo, '是否删除:', isDel)
  return { success: true }
}

/**
 * 获取待通知用户信息列表 - Mock版本
 */
export async function getNotifyUserList() {
  await new Promise(resolve => setTimeout(resolve, 300))

  return [
    {
      userId: 13800138000,
      notifyContent: '您有一条新公告待查看',
      createTime: Date.now() - 3600000
    }
  ]
}
