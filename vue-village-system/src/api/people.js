/**
 * 家庭信息相关API
 */

import { request } from './request'

/**
 * 获取家庭信息列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getPeopleList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListPeopleInfoReq, ListPeopleInfoRsp } = proto.po

  console.log('=== 获取家庭信息列表请求 ===')
  console.log('页码:', page, '每页数量:', size)

  const listReq = ListPeopleInfoReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListPeopleInfoReq, listReq)
  const listRsp = ListPeopleInfoRsp.decode(response.msg)

  console.log('========================================')
  console.log('👨‍👩‍👧‍👦 家庭信息列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('信息数量:', listRsp.infos?.length || 0)
  if (listRsp.infos && listRsp.infos.length > 0) {
    console.log('信息列表:')
    listRsp.infos.forEach((people, index) => {
      console.log(`  [${index + 1}] 姓名:${people.peopleName} 身份证:${people.peopleCardId}`)
    })
  }
  console.log('完整ListPeopleInfoRsp对象:', listRsp)
  console.log('ListPeopleInfoRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
  console.log('========================================')
  console.log(' ')

  return listRsp.infos || []
}

/**
 * 创建家庭信息（村干部）
 * @param {Object} peopleInfo - 家庭信息
 * @returns {Promise<void>}
 */
export async function createPeople(peopleInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreatePeopleReq, PeopleInfo } = proto.po

  console.log('=== 创建家庭信息请求 ===')
  console.log('家庭信息:', peopleInfo)

  const peopleInfoProto = PeopleInfo.create(peopleInfo)

  const createReq = CreatePeopleReq.create({
    infos: [peopleInfoProto]  // ✅ 修复：字段名改为 infos，并且是数组
  })

  await request(MsgType.TMT_CreatePeopleReq, createReq)

  console.log('========================================')
  console.log('✅ 家庭信息创建成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 更新家庭信息（村干部）
 * @param {Object} peopleInfo - 家庭信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updatePeople(peopleInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdatePeopleReq, PeopleInfo } = proto.po

  console.log('=== 更新家庭信息请求 ===')
  console.log('家庭信息:', peopleInfo)
  console.log('是否删除:', isDel)

  const peopleInfoProto = PeopleInfo.create(peopleInfo)

  const updateReq = UpdatePeopleReq.create({
    isDel: isDel,
    infos: peopleInfoProto  // ✅ 修复：字段名改为 infos
  })

  await request(MsgType.TMT_UpdatePeopleReq, updateReq)

  console.log('========================================')
  console.log('✅ 家庭信息更新成功')
  console.log('========================================')
  console.log(' ')
}
