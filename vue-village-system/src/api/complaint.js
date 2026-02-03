/**
 * 投诉相关API
 */

import { request } from './request'

/**
 * 创建投诉
 * @param {Object} questionInfo - 投诉信息
 * @returns {Promise<void>}
 */
export async function createComplaint(questionInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, CreateQuestionReq, QuestionInfo } = proto

  const questionInfoProto = QuestionInfo.create(questionInfo)

  const createReq = CreateQuestionReq.create({
    questionInfo: questionInfoProto
  })

  await request(MsgType.TMT_CreateQuestionReq, createReq)
}

/**
 * 更新投诉
 * @param {Object} questionInfo - 投诉信息
 * @returns {Promise<void>}
 */
export async function updateComplaint(questionInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateQuestionReq, QuestionInfo } = proto

  const questionInfoProto = QuestionInfo.create(questionInfo)

  const updateReq = UpdateQuestionReq.create({
    questionInfo: questionInfoProto
  })

  await request(MsgType.TMT_UpdateQuestionReq, updateReq)
}

/**
 * 获取投诉列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getComplaintList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListQuestionReq, ListQuestionRsp } = proto

  const listReq = ListQuestionReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListQuestionReq, listReq)
  const listRsp = ListQuestionRsp.decode(response.msg)

  return listRsp.questionsList || []
}

/**
 * 提交满意度评价
 * @param {number} questionId - 投诉ID
 * @param {number} rating - 评分（1-5星）
 * @returns {Promise<void>}
 */
export async function submitSatisfactionRating(questionId, rating) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateQuestionReq, QuestionInfo } = proto

  const questionInfo = QuestionInfo.create({
    questionId: questionId,
    satisfactionRating: rating,
    ratingTime: Date.now()
  })

  const updateReq = UpdateQuestionReq.create({
    questionInfo: questionInfo
  })

  await request(MsgType.TMT_UpdateQuestionReq, updateReq)
}
