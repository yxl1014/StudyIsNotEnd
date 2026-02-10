/**
 * 用户相关API
 *
 * 注意：此文件依赖proto文件生成的代码
 * 在proto文件生成前，这些函数会抛出错误
 */

import {sendRequest} from './request'

/**
 * 用户登录
 * @param {number} userTel - 手机号
 * @param {string} userPwd - 密码
 * @returns {Promise<{token: string, userInfo: Object}>}
 */
export async function login(userTel, userPwd) {
    const proto = await import('@/proto/proto.js')
    const {MsgType, LoginReq, LoginRsp} = proto.po

    console.log('=== 登录请求 ===')
    console.log('手机号:', userTel)

    // 构建登录请求
    const loginReq = LoginReq.create({
        userTel: userTel,
        userPwd: userPwd
    })

    // 发送请求
    const response = await sendRequest(MsgType.TMT_LoginReq, loginReq, '')

    // 解析响应
    const loginRsp = LoginRsp.decode(response.msg)

    console.log('========================================')
    console.log('🔓 登录响应 - 解码后的业务数据')
    console.log('========================================')
    console.log('Token:', loginRsp.token)
    console.log('用户信息:')
    console.log('  - 手机号:', loginRsp.userInfo.userTel)
    console.log('  - 用户名:', loginRsp.userInfo.userName)
    console.log('  - 权限:', loginRsp.userInfo.userPower, '(0=村民, 1=村干部)')
    console.log('  - 状态:', loginRsp.userInfo.flagType, '(0=正常, 1=冻结)')
    console.log('  - 注册时间:', new Date(loginRsp.userInfo.userCreateTime).toLocaleString())
    console.log('完整LoginRsp对象:', loginRsp)
    console.log('LoginRsp JSON:', JSON.stringify(loginRsp.toJSON(), null, 2))
    console.log('========================================')
    console.log(' ')

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
    const {MsgType, RegisterReq, UserInfo} = proto.po

    console.log('=== 注册请求 ===')
    console.log('注册信息:', userInfo)

    const userInfoProto = UserInfo.create({
        userTel: userInfo.userTel,
        userName: userInfo.userName,
        userPwd: userInfo.userPwd,
        userTown: userInfo.userTown || '', // 用户所属村庄
        userPower: userInfo.userPower || 0, // 默认为村民
        flagType: userInfo.flagType || 0
    })

    const registerReq = RegisterReq.create({
        userInfo: userInfoProto
    })

    await sendRequest(MsgType.TMT_RegisterReq, registerReq, '')

    console.log('========================================')
    console.log('✅ 注册成功')
    console.log('========================================')
    console.log(' ')
}

/**
 * 更新用户信息
 * @param {Object} userInfo - 用户信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateUserInfo(userInfo, isDel = false) {
    const proto = await import('@/proto/proto.js')
    const {MsgType, UpdateUserInfoReq, UserInfo} = proto.po
    const {useUserStore} = await import('@/stores/user')
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
    const {MsgType, ListNotifyUserInfoReq, ListNotifyUserInfoRsp} = proto.po
    const {useUserStore} = await import('@/stores/user')
    const userStore = useUserStore()

    const listReq = ListNotifyUserInfoReq.create({})

    const response = await sendRequest(MsgType.TMT_ListNotifyUserInfoReq, listReq, userStore.token)

    const listRsp = ListNotifyUserInfoRsp.decode(response.msg)

    return listRsp.infosList || []
}

/**
 * 获取用户信息列表（管理员）
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @param {number} userTel - 用户电话（可选，用于查询特定用户）
 * @returns {Promise<Array>}
 */
export async function getUserList(page = 1, size = 10, userTel = null) {
    const proto = await import('@/proto/proto.js')
    const {MsgType, ListUserInfoReq, ListUserInfoRsp} = proto.po
    const {useUserStore} = await import('@/stores/user')
    const userStore = useUserStore()

    console.log('=== 获取用户列表请求 ===')
    console.log('页码:', page, '每页数量:', size, '查询电话:', userTel || '(全部)')

    const listReq = ListUserInfoReq.create({
        page: page,
        size: size
    })

    if (userTel) {
        listReq.userTel = userTel
    }

    const response = await sendRequest(MsgType.TMT_ListUserInfoReq, listReq, userStore.token)
    const listRsp = ListUserInfoRsp.decode(response.msg)

    console.log('========================================')
    console.log('👥 用户列表响应 - 解码后的业务数据')
    console.log('========================================')
    console.log('用户数量:', listRsp.userInfos?.length || 0)
    if (listRsp.userInfos && listRsp.userInfos.length > 0) {
        console.log('用户列表:')
        listRsp.userInfos.forEach((user, index) => {
            console.log(`  [${index + 1}] 电话:${user.userTel} 姓名:${user.userName} 权限:${user.userPower} 状态:${user.flagType}`)
        })
    }
    console.log('完整ListUserInfoRsp对象:', listRsp)
    console.log('ListUserInfoRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
    console.log('========================================')
    console.log(' ')

    return listRsp.userInfos || []
}
