/**
 * 学习资料相关API
 */

import { request } from './request'

/**
 * 获取学习资料列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getStudyList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListStudyReq, ListStudyRsp } = proto

  const listReq = ListStudyReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListStudyReq, listReq)
  const listRsp = ListStudyRsp.decode(response.msg)

  return listRsp.studysList || []
}

/**
 * 创建学习资料（村干部）
 * @param {Object} studyInfo - 学习资料信息
 * @returns {Promise<void>}
 */
export async function createStudy(studyInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreateStudyReq, StudyInfo } = proto

  const studyInfoProto = StudyInfo.create(studyInfo)

  const createReq = CreateStudyReq.create({
    studyInfo: studyInfoProto
  })

  await request(MsgType.TMT_CreateStudyReq, createReq)
}

/**
 * 更新学习资料（村干部）
 * @param {Object} studyInfo - 学习资料信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateStudy(studyInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateStudyReq, StudyInfo } = proto

  const studyInfoProto = StudyInfo.create(studyInfo)

  const updateReq = UpdateStudyReq.create({
    studyInfo: studyInfoProto,
    isDel: isDel
  })

  await request(MsgType.TMT_UpdateStudyReq, updateReq)
}

/**
 * 收藏/取消收藏学习资料
 * @param {number} studyId - 学习资料ID
 * @param {boolean} isStar - 是否收藏
 * @returns {Promise<void>}
 */
export async function toggleStarStudy(studyId, isStar) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, StarStudyReq } = proto

  const starReq = StarStudyReq.create({
    studyId: studyId,
    isStar: isStar
  })

  await request(MsgType.TMT_StarStudyReq, starReq)
}

/**
 * 获取我的收藏列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getMyStarList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListUserStarStudyReq, ListUserStarStudyRsp } = proto

  const listReq = ListUserStarStudyReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListUserStarStudyReq, listReq)
  const listRsp = ListUserStarStudyRsp.decode(response.msg)

  return listRsp.starsList || []
}
