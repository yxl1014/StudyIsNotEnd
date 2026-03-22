/**
 * 学习资料相关API
 */

import { request } from './request'

/**
 * 获取学习资料列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @param {number} studyId - 学习资料ID（可选，传入则获取单个资料并增加阅读数）
 * @returns {Promise<Array>}
 */
export async function getStudyList(page = 1, size = 10, studyId = 0) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListStudyReq, ListStudyRsp } = proto.po

  console.log('=== 获取学习资料列表请求 ===')
  console.log('页码:', page, '每页数量:', size, '资料ID:', studyId || '(全部)')

  const listReq = ListStudyReq.create({
    studyId: studyId,  // ✅ 关键：传递 studyId
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListStudyReq, listReq)
  const listRsp = ListStudyRsp.decode(response.msg)

  console.log('========================================')
  console.log('📚 学习资料列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('资料数量:', listRsp.infos?.length || 0)
  if (studyId > 0) {
    console.log('⚠️ 注意：传入了 studyId=' + studyId + '，后端会自动增加该资料的阅读数')
  }
  if (listRsp.infos && listRsp.infos.length > 0) {
    console.log('资料列表:')
    listRsp.infos.forEach((study, index) => {
      console.log(`  [${index + 1}] ID:${study.studyId} 标题:${study.studyTitle} 类型:${study.studyType}`)
    })
  }
  console.log('完整ListStudyRsp对象:', listRsp)
  console.log('ListStudyRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
  console.log('========================================')
  console.log(' ')

  return listRsp.infos || []
}

/**
 * 创建学习资料（村干部）
 * @param {Object} studyInfo - 学习资料信息
 * @returns {Promise<void>}
 */
export async function createStudy(studyInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreateStudyReq, StudyInfo } = proto.po

  console.log('=== 创建学习资料请求 ===')
  console.log('资料信息:', studyInfo)

  const studyInfoProto = StudyInfo.create(studyInfo)

  const createReq = CreateStudyReq.create({
    studyInfo: studyInfoProto
  })

  await request(MsgType.TMT_CreateStudyReq, createReq)

  console.log('========================================')
  console.log('✅ 学习资料创建成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 更新学习资料（村干部）
 * @param {Object} studyInfo - 学习资料信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateStudy(studyInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateStudyReq, StudyInfo } = proto.po

  console.log('=== 更新学习资料请求 ===')
  console.log('资料信息:', studyInfo)
  console.log('是否删除:', isDel)

  const studyInfoProto = StudyInfo.create(studyInfo)

  const updateReq = UpdateStudyReq.create({
    studyInfo: studyInfoProto,
    isDel: isDel
  })

  await request(MsgType.TMT_UpdateStudyReq, updateReq)

  console.log('========================================')
  console.log('✅ 学习资料更新成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 收藏/取消收藏学习资料
 * @param {number} studyId - 学习资料ID
 * @param {boolean} isStar - 是否收藏（true=收藏，false=取消收藏）
 * @returns {Promise<void>}
 */
export async function toggleStarStudy(studyId, isStar) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, StarStudyReq } = proto.po

  console.log('=== 收藏/取消收藏学习资料请求 ===')
  console.log('资料ID:', studyId, '操作:', isStar ? '收藏' : '取消收藏')

  const starReq = StarStudyReq.create({
    studyId: studyId,
    isCancel: !isStar  // ✅ 修复：字段名改为 isCancel，并且逻辑取反
  })

  await request(MsgType.TMT_StarStudyReq, starReq)

  console.log('✅ 操作成功')
}

/**
 * 获取我的收藏列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getMyStarList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListUserStarStudyReq, ListUserStarStudyRsp } = proto.po

  const listReq = ListUserStarStudyReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListUserStarStudyReq, listReq)
  const listRsp = ListUserStarStudyRsp.decode(response.msg)

  console.log('========================================')
  console.log('⭐ 我的收藏列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('收藏数量:', listRsp.infos?.length || 0)
  console.log('完整ListUserStarStudyRsp对象:', listRsp)
  console.log('========================================')
  console.log(' ')

  return listRsp.infos || []
}
