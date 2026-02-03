/**
 * 学习资料相关API - Mock版本
 */

/**
 * 获取学习资料列表 - Mock版本
 */
export async function getStudyList(page = 1, size = 10) {
  await new Promise(resolve => setTimeout(resolve, 300))

  const mockStudies = [
    {
      studyId: 1,
      studyTitle: '2024年中央一号文件解读',
      studyContent: '2024年中央一号文件聚焦建设农业强国，提出了一系列重要政策措施。本文件重点关注粮食安全、乡村产业发展、农民增收等方面...',
      studyType: '政策解读',
      studyIntro: '深入解读2024年中央一号文件精神',
      uploaderTel: 13800138001,
      uploaderName: '李村长',
      isOpen: true,
      isTop: true,
      readCount: 156,
      createTime: Date.now() - 86400000 * 10,
      isStar: false
    },
    {
      studyId: 2,
      studyTitle: '春季蔬菜种植技术要点',
      studyContent: '春季是蔬菜种植的关键时期，本文介绍了春季常见蔬菜的种植技术要点，包括选种、育苗、田间管理、病虫害防治等内容...',
      studyType: '农技推广',
      studyIntro: '春季蔬菜种植实用技术指南',
      uploaderTel: 13800138001,
      uploaderName: '李村长',
      isOpen: true,
      isTop: false,
      readCount: 89,
      createTime: Date.now() - 86400000 * 15,
      isStar: false
    },
    {
      studyId: 3,
      studyTitle: '老年人健康养生知识',
      studyContent: '随着年龄增长，老年人的身体机能逐渐下降，科学的养生方法对保持健康至关重要。本文介绍了老年人日常养生的注意事项...',
      studyType: '健康养生',
      studyIntro: '老年人日常保健实用知识',
      uploaderTel: 13800138001,
      uploaderName: '李村长',
      isOpen: true,
      isTop: false,
      readCount: 234,
      createTime: Date.now() - 86400000 * 20,
      isStar: true
    },
    {
      studyId: 4,
      studyTitle: '农村土地承包法律知识',
      studyContent: '农村土地承包是关系农民切身利益的重要问题。本文介绍了农村土地承包的相关法律规定，包括承包期限、权利义务等...',
      studyType: '法律常识',
      studyIntro: '农村土地承包相关法律解读',
      uploaderTel: 13800138001,
      uploaderName: '李村长',
      isOpen: true,
      isTop: false,
      readCount: 67,
      createTime: Date.now() - 86400000 * 25,
      isStar: false
    }
  ]

  return mockStudies
}

/**
 * 创建学习资料 - Mock版本
 */
export async function createStudy(studyInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('创建学习资料:', studyInfo)
  return { success: true }
}

/**
 * 更新学习资料 - Mock版本
 */
export async function updateStudy(studyInfo, isDel = false) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('更新学习资料:', studyInfo, '是否删除:', isDel)
  return { success: true }
}

/**
 * 收藏/取消收藏学习资料 - Mock版本
 */
export async function toggleStarStudy(studyId, isStar) {
  await new Promise(resolve => setTimeout(resolve, 300))

  console.log('收藏操作:', studyId, isStar)
  return { success: true }
}

/**
 * 获取我的收藏列表 - Mock版本
 */
export async function getMyStarList(page = 1, size = 10) {
  await new Promise(resolve => setTimeout(resolve, 300))

  return [
    {
      studyId: 3,
      studyTitle: '老年人健康养生知识',
      studyType: '健康养生',
      starTime: Date.now() - 86400000 * 5
    }
  ]
}
