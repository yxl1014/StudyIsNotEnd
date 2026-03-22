/**
 * 公告相关API
 */

import { request } from './request'

/**
 * 获取公告列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getNoticeList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListNoticeReq, ListNoticeRsp } = proto.po

  console.log('=== 获取公告列表请求 ===')
  console.log('页码:', page, '每页数量:', size)

  const listReq = ListNoticeReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListNoticeReq, listReq)
  const listRsp = ListNoticeRsp.decode(response.msg)

  console.log('========================================')
  console.log('📋 公告列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('公告数量:', listRsp.infos?.length || 0)
  if (listRsp.infos && listRsp.infos.length > 0) {
    console.log('公告列表:')
    listRsp.infos.forEach((notice, index) => {
      console.log(`  [${index + 1}] ID:${notice.noticeId} 标题:${notice.noticeTitle} 类型:${notice.noticeType} 置顶:${notice.isTop}`)
    })
  }
  console.log('完整ListNoticeRsp对象:', listRsp)
  console.log('ListNoticeRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
  console.log('========================================')
  console.log(' ')

  return listRsp.infos || []
}

/**
 * 创建公告（村干部）
 * @param {Object} noticeInfo - 公告信息
 * @returns {Promise<void>}
 */
export async function createNotice(noticeInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreateNoticeReq, NoticeInfo } = proto.po

  console.log('=== 创建公告请求 ===')
  console.log('公告信息:', noticeInfo)

  const noticeInfoProto = NoticeInfo.create(noticeInfo)

  const createReq = CreateNoticeReq.create({
    noticeInfo: noticeInfoProto
  })

  await request(MsgType.TMT_CreateNoticeReq, createReq)

  console.log('========================================')
  console.log('✅ 公告创建成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 更新公告（村干部）
 * @param {Object} noticeInfo - 公告信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateNotice(noticeInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateNoticeReq, NoticeInfo } = proto.po

  console.log('=== 更新公告请求 ===')
  console.log('公告信息:', noticeInfo)
  console.log('是否删除:', isDel)

  const noticeInfoProto = NoticeInfo.create(noticeInfo)

  const updateReq = UpdateNoticeReq.create({
    noticeInfo: noticeInfoProto,
    isDel: isDel
  })

  await request(MsgType.TMT_UpdateNoticeReq, updateReq)

  console.log('========================================')
  console.log('✅ 公告更新成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 标记公告已读
 * @param {number} noticeId - 公告ID
 * @returns {Promise<void>}
 */
export async function markNoticeRead(noticeId) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, SetNoticeReadReq } = proto.po

  const readReq = SetNoticeReadReq.create({
    noticeId: noticeId
  })

  await request(MsgType.TMT_SetNoticeReadReq, readReq)
}

/**
 * 获取当前用户已读公告列表
 * @returns {Promise<Array>} 返回已读公告ID列表
 */
export async function getNoticeReadList() {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListNoticeReadReq, ListNoticeReadRsp } = proto.po

  const listReq = ListNoticeReadReq.create({})

  const response = await request(MsgType.TMT_ListNoticeReadReq, listReq)
  const listRsp = ListNoticeReadRsp.decode(response.msg)

  console.log('已读公告列表:', listRsp.noticeList)

  // 返回已读公告ID列表
  return listRsp.noticeList || []
}
