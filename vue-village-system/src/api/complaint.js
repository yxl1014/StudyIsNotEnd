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
  const { MsgType, CreateQuestionReq, QuestionInfo } = proto.po

  console.log('=== 创建投诉请求 ===')
  console.log('投诉信息:', questionInfo)

  const questionInfoProto = QuestionInfo.create(questionInfo)

  const createReq = CreateQuestionReq.create({
    questionInfo: questionInfoProto
  })

  await request(MsgType.TMT_CreateQuestionReq, createReq)

  console.log('========================================')
  console.log('✅ 投诉创建成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 更新投诉
 * @param {Object} questionInfo - 投诉信息
 * @returns {Promise<void>}
 */
export async function updateComplaint(questionInfo) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateQuestionReq, QuestionInfo } = proto.po

  console.log('=== 更新投诉请求 ===')
  console.log('投诉信息:', questionInfo)

  const questionInfoProto = QuestionInfo.create(questionInfo)

  const updateReq = UpdateQuestionReq.create({
    questionInfo: questionInfoProto
  })

  await request(MsgType.TMT_UpdateQuestionReq, updateReq)

  console.log('========================================')
  console.log('✅ 投诉更新成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 获取投诉列表
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Array>}
 */
export async function getComplaintList(page = 1, size = 10) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListQuestionReq, ListQuestionRsp } = proto.po

  console.log('=== 获取投诉列表请求 ===')
  console.log('页码:', page, '每页数量:', size)

  const listReq = ListQuestionReq.create({
    page: page,
    size: size
  })

  const response = await request(MsgType.TMT_ListQuestionReq, listReq)
  const listRsp = ListQuestionRsp.decode(response.msg)

  console.log('========================================')
  console.log('📝 投诉列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('投诉数量:', listRsp.infos?.length || 0)
  if (listRsp.infos && listRsp.infos.length > 0) {
    console.log('投诉列表:')
    listRsp.infos.forEach((question, index) => {
      console.log(`  [${index + 1}] ID:${question.questionId} 类型:${question.questionType} 状态:${question.nodeType}`)
    })
  }
  console.log('完整ListQuestionRsp对象:', listRsp)
  console.log('ListQuestionRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
  console.log('========================================')
  console.log(' ')

  return listRsp.infos || []
}

/**
 * 提交满意度评价
 * @param {number} questionId - 投诉ID
 * @param {number} rating - 评分（1-5星）
 * @returns {Promise<void>}
 */
export async function submitSatisfactionRating(questionId, rating) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, UpdateQuestionReq, QuestionInfo } = proto.po

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
