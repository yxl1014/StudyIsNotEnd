/**
 * 投诉相关API - Mock版本
 */

/**
 * 创建投诉 - Mock版本
 */
export async function createComplaint(questionInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('创建投诉:', questionInfo)
  return { success: true }
}

/**
 * 更新投诉 - Mock版本
 */
export async function updateComplaint(questionInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('更新投诉:', questionInfo)
  return { success: true }
}

/**
 * 获取投诉列表 - Mock版本
 */
export async function getComplaintList(page = 1, size = 10) {
  await new Promise(resolve => setTimeout(resolve, 300))

  const mockComplaints = [
    {
      questionId: 1,
      questionCtx: '【环境卫生】村口垃圾堆积严重，影响村容村貌，希望尽快清理',
      questionWriterTel: 13800138000,
      questionWriterName: '张三',
      nodeType: 0, // 待处理
      createTime: Date.now() - 3600000,
      choiceUser: null,
      choiceUserName: null,
      replyContent: null,
      satisfactionRating: null,
      ratingTime: null
    },
    {
      questionId: 2,
      questionCtx: '【基础设施损坏】村道路灯损坏多日未修，夜间出行不便',
      questionWriterTel: 13800138002,
      questionWriterName: '王五',
      nodeType: 1, // 处理中
      createTime: Date.now() - 86400000 * 2,
      choiceUser: 13800138001,
      choiceUserName: '李村长',
      replyContent: '已安排电工师傅查看，预计本周内完成维修',
      satisfactionRating: null,
      ratingTime: null
    },
    {
      questionId: 3,
      questionCtx: '【邻里纠纷】邻居装修噪音扰民，希望协调解决',
      questionWriterTel: 13800138003,
      questionWriterName: '赵六',
      nodeType: 2, // 已完成
      createTime: Date.now() - 86400000 * 5,
      choiceUser: 13800138001,
      choiceUserName: '李村长',
      replyContent: '已与双方沟通协调，邻居同意调整装修时间，避开休息时段。问题已解决。',
      satisfactionRating: 5, // 已评价：5星
      ratingTime: Date.now() - 86400000 * 4
    },
    {
      questionId: 4,
      questionCtx: '【基础设施损坏】村委会门口路面坑洼，雨天积水严重',
      questionWriterTel: 13800138000,
      questionWriterName: '张三',
      nodeType: 2, // 已完成
      createTime: Date.now() - 86400000 * 10,
      choiceUser: 13800138001,
      choiceUserName: '李村长',
      replyContent: '已联系施工队进行路面修复，现已完工。',
      satisfactionRating: null, // 未评价
      ratingTime: null
    }
  ]

  return mockComplaints
}

/**
 * 提交满意度评价 - Mock版本
 */
export async function submitSatisfactionRating(questionId, rating) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('提交满意度评价:', { questionId, rating })
  return { success: true }
}
