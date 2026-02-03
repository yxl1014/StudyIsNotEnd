/**
 * 家庭信息相关API - Mock版本
 */

/**
 * 获取家庭信息列表 - Mock版本
 */
export async function getPeopleList(page = 1, size = 10) {
  await new Promise(resolve => setTimeout(resolve, 300))

  const mockPeople = [
    {
      peopleCardId: '110101199001011234',
      peopleName: '张三',
      peopleHouseId: 'A001',
      peopleCtx: '户主',
      createTime: Date.now() - 86400000 * 100
    },
    {
      peopleCardId: '110101199205155678',
      peopleName: '李四',
      peopleHouseId: 'A002',
      peopleCtx: '户主',
      createTime: Date.now() - 86400000 * 90
    },
    {
      peopleCardId: '110101198803209012',
      peopleName: '王五',
      peopleHouseId: 'A003',
      peopleCtx: '户主，低保户',
      createTime: Date.now() - 86400000 * 80
    }
  ]

  return mockPeople
}

/**
 * 创建家庭信息 - Mock版本
 */
export async function createPeople(peopleInfo) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('创建家庭信息:', peopleInfo)
  return { success: true }
}

/**
 * 更新家庭信息 - Mock版本
 */
export async function updatePeople(peopleInfo, isDel = false) {
  await new Promise(resolve => setTimeout(resolve, 500))

  console.log('更新家庭信息:', peopleInfo, '是否删除:', isDel)
  return { success: true }
}
