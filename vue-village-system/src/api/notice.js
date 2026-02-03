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
  const { MsgType, ListNoticeReq, ListNoticeRsp } = proto

  const listReq = ListNoticeReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListNoticeReq, listReq)
  const listRsp = ListNoticeRsp.decode(response.msg)

  return listRsp.noticesList || []
}

/**
 * 创建公告（村干部）
 * @param {Object} noticeInfo - 公告信息
 * @returns {Promise<void>}
 */
export async function createNotice(noticeInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreateNoticeReq, NoticeInfo } = proto

  const noticeInfoProto = NoticeInfo.create(noticeInfo)

  const createReq = CreateNoticeReq.create({
    noticeInfo: noticeInfoProto
  })

  await request(MsgType.TMT_CreateNoticeReq, createReq)
}

/**
 * 更新公告（村干部）
 * @param {Object} noticeInfo - 公告信息
 * @param {boolean} isDel - 是否删除
 * @returns {Promise<void>}
 */
export async function updateNotice(noticeInfo, isDel = false) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateNoticeReq, NoticeInfo } = proto

  const noticeInfoProto = NoticeInfo.create(noticeInfo)

  const updateReq = UpdateNoticeReq.create({
    noticeInfo: noticeInfoProto,
    isDel: isDel
  })

  await request(MsgType.TMT_UpdateNoticeReq, updateReq)
}

/**
 * 标记公告已读
 * @param {number} noticeId - 公告ID
 * @returns {Promise<void>}
 */
export async function markNoticeRead(noticeId) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, SetNoticeReadReq } = proto

  const readReq = SetNoticeReadReq.create({
    noticeId: noticeId
  })

  await request(MsgType.TMT_SetNoticeReadReq, readReq)
}

/**
 * 获取公告已读用户列表
 * @param {number} noticeId - 公告ID
 * @returns {Promise<Array>}
 */
export async function getNoticeReadList(noticeId) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListNoticeReadReq, ListNoticeReadRsp } = proto

  const listReq = ListNoticeReadReq.create({
    noticeId: noticeId
  })

  const response = await request(MsgType.TMT_ListNoticeReadReq, listReq)
  const listRsp = ListNoticeReadRsp.decode(response.msg)

  return listRsp.readsList || []
}
