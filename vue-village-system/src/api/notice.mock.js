/**
 * 公告相关API - Mock版本
 */

/**
 * 获取公告列表 - Mock版本
 */
export async function getNoticeList(page = 1, size = 10) {
  await new Promise(resolve => setTimeout(resolve, 300))

  // 模拟公告数据
  const mockNotices = [
    {
      noticeId: 1,
      noticeTitle: '关于开展春季环境卫生整治的通知',
      noticeContext: '为进一步改善村容村貌，提升人居环境质量，经村委会研究决定，将于本月15日至20日开展春季环境卫生集中整治活动。请各位村民积极配合，共同营造干净整洁的生活环境。',
      noticeType: '通知类',
      writerTel: 13800138001,
      writerName: '李村长',
      isTop: true,
      isAcceptRead: true,
      createTime: Date.now() - 86400000 * 1
    },
    {
      noticeId: 2,
      noticeTitle: '村委会招聘保洁员公告',
      noticeContext: '因工作需要，现面向全村招聘保洁员2名。要求：年龄18-60岁，身体健康，工作认真负责。待遇：月薪3000元，包午餐。有意者请到村委会报名。',
      noticeType: '招聘类',
      writerTel: 13800138001,
      writerName: '李村长',
      isTop: false,
      isAcceptRead: false,
      createTime: Date.now() - 86400000 * 3
    },
    {
      noticeId: 3,
      noticeTitle: '端午节文艺活动通知',
      noticeContext: '为丰富村民文化生活，村委会将于端午节当天举办文艺汇演活动。欢迎有才艺的村民踊跃报名参加。报名时间截止到本月底。',
      noticeType: '活动类',
      writerTel: 13800138001,
      writerName: '李村长',
      isTop: false,
      isAcceptRead: false,
      createTime: Date.now() - 86400000 * 5
    },
    {
      noticeId: 4,
      noticeTitle: '农村医保政策解读',
      noticeContext: '2024年度农村医疗保险缴费工作已经开始，缴费标准为每人380元。请各位村民在规定时间内完成缴费，逾期将影响医保待遇享受。',
      noticeType: '政策宣传类',
      writerTel: 13800138001,
      writerName: '李村长',
      isTop: false,
      isAcceptRead: false,
      createTime: Date.now() - 86400000 * 7
    }
  ]

  return mockNotices
}

/**
 * 创建公告 - Mock版本
 */
export async function createNotice(noticeInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('创建公告:', noticeInfo)
  return { success: true }
}

/**
 * 更新公告 - Mock版本
 */
export async function updateNotice(noticeInfo, isDel = false) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('更新公告:', noticeInfo, '是否删除:', isDel)
  return { success: true }
}

/**
 * 标记公告已读 - Mock版本
 */
export async function markNoticeRead(noticeId) {
  await new Promise(resolve => setTimeout(resolve, 300))

  console.log('标记公告已读:', noticeId)
  return { success: true }
}

/**
 * 获取公告已读用户列表 - Mock版本
 */
export async function getNoticeReadList(noticeId) {
  await new Promise(resolve => setTimeout(resolve, 300))

  return [
    {
      userTel: 13800138000,
      userName: '张三',
      readTime: Date.now() - 3600000
    },
    {
      userTel: 13800138002,
      userName: '王五',
      readTime: Date.now() - 7200000
    }
  ]
}
