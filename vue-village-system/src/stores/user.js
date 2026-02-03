import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref('')
  const userInfo = ref(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  const isAdmin = computed(() => {
    return userInfo.value?.userPower === 'TUP_CGM' // 村干部
  })

  const userName = computed(() => userInfo.value?.userName || '')
  const userTel = computed(() => userInfo.value?.userTel || 0)

  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const login = (loginToken, loginUserInfo) => {
    setToken(loginToken)
    setUserInfo(loginUserInfo)
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const loadFromStorage = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')

    if (savedToken) {
      token.value = savedToken
    }

    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    userName,
    userTel,
    setToken,
    setUserInfo,
    login,
    logout,
    loadFromStorage
  }
})
