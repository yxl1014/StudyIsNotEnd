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
  const { MsgType, ListPeopleInfoReq, ListPeopleInfoRsp } = proto

  const listReq = ListPeopleInfoReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListPeopleInfoReq, listReq)
  const listRsp = ListPeopleInfoRsp.decode(response.msg)

  return listRsp.peoplesList || []
}

/**
 * 创建家庭信息（村干部）
 * @param {Object} peopleInfo - 家庭信息
 * @returns {Promise<void>}
 */
export async function createPeople(peopleInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreatePeopleReq, PeopleInfo } = proto

  const peopleInfoProto = PeopleInfo.create(peopleInfo)

  const createReq = CreatePeopleReq.create({
    peopleInfo: peopleInfoProto
  })

  await request(MsgType.TMT_CreatePeopleReq, createReq)
}

/**
 * 更新家庭信息（村干部）
 * @param {Object} peopleInfo - 家庭信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updatePeople(peopleInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdatePeopleReq, PeopleInfo } = proto

  const peopleInfoProto = PeopleInfo.create(peopleInfo)

  const updateReq = UpdatePeopleReq.create({
    peopleInfo: peopleInfoProto,
    isDel: isDel
  })

  await request(MsgType.TMT_UpdatePeopleReq, updateReq)
}
