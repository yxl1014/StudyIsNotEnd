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
    question: questionInfoProto  // ✅ 修复：字段名改为 question
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
  const { MsgType, UpdateQuestionReq, QuestionInfo, QuestionNodeType } = proto.po

  console.log('=== 更新投诉请求 ===')
  console.log('投诉信息:', questionInfo)

  // 如果 nodeType 是数字，映射到枚举值
  if (typeof questionInfo.nodeType === 'number') {
    const nodeTypeMap = {
      0: QuestionNodeType.TQNT_PRE,   // 待处理
      1: QuestionNodeType.TQNT_MID,   // 处理中
      2: QuestionNodeType.TQNT_TAIL   // 已处理
    }
    questionInfo.nodeType = nodeTypeMap[questionInfo.nodeType]
    console.log('映射后的状态枚举值:', questionInfo.nodeType)
  }

  const questionInfoProto = QuestionInfo.create(questionInfo)

  const updateReq = UpdateQuestionReq.create({
    question: questionInfoProto  // ✅ 修复：字段名改为 question
  })

  await request(MsgType.TMT_UpdateQuestionReq, updateReq)

  console.log('========================================')
  console.log('✅ 投诉更新成功')
  console.log('========================================')
  console.log(' ')
}

/**
 * 获取投诉列表（村民）
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @param {number} nodeType - 投诉状态（可选：0=待处理，1=处理中，2=已完成，null=全部）
 * @returns {Promise<Array>}
 */
export async function getComplaintList(page = 1, size = 10, nodeType = null) {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListQuestionReq, ListQuestionRsp, QuestionNodeType } = proto.po

  console.log('=== 获取投诉列表请求（村民）===')
  console.log('页码:', page, '每页数量:', size, '状态:', nodeType === null ? '全部' : nodeType)

  const listReq = ListQuestionReq.create({
    page: page,
    size: size
  })

  // 如果指定了状态，映射到枚举值
  if (nodeType !== null) {
    // 映射：0 → TQNT_PRE, 1 → TQNT_MID, 2 → TQNT_TAIL
    const nodeTypeMap = {
      0: QuestionNodeType.TQNT_PRE,   // 待处理
      1: QuestionNodeType.TQNT_MID,   // 处理中
      2: QuestionNodeType.TQNT_TAIL   // 已处理
    }
    listReq.nodeType = nodeTypeMap[nodeType]
    console.log('映射后的枚举值:', listReq.nodeType)
  }

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
 * 获取投诉处理列表（管理员）
 * @returns {Promise<Array>}
 */
export async function getComplaintHandlingList() {
  const proto = await import('@/proto/proto.js')
  const { MsgType, ListQuestionHandlingReq, ListQuestionHandlingRsp } = proto.po

  console.log('=== 获取投诉处理列表请求（管理员）===')

  const listReq = ListQuestionHandlingReq.create({})  // 空请求，不需要参数

  const response = await request(MsgType.TMT_ListQuestionHandlingReq, listReq)
  const listRsp = ListQuestionHandlingRsp.decode(response.msg)

  console.log('========================================')
  console.log('📝 投诉处理列表响应 - 解码后的业务数据')
  console.log('========================================')
  console.log('投诉数量:', listRsp.infos?.length || 0)
  if (listRsp.infos && listRsp.infos.length > 0) {
    console.log('投诉处理列表:')
    listRsp.infos.forEach((question, index) => {
      console.log(`  [${index + 1}] ID:${question.questionId} 类型:${question.questionType} 状态:${question.nodeType}`)
    })
  }
  console.log('完整ListQuestionHandlingRsp对象:', listRsp)
  console.log('ListQuestionHandlingRsp JSON:', JSON.stringify(listRsp.toJSON(), null, 2))
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
