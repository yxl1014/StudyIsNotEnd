<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon :size="24"><House /></el-icon>
          <span>村务管理系统</span>
        </div>
        <div class="user-info">
          <span>欢迎，{{ userStore.userName }}</span>
          <el-dropdown @command="handleCommand">
            <el-avatar :size="36">{{ userStore.userName.charAt(0) }}</el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin">
                  <el-icon><Setting /></el-icon>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 快捷入口 -->
      <div class="quick-access">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8" :md="6" v-for="item in menuItems" :key="item.path">
            <div class="menu-card" @click="navigateTo(item.path)">
              <el-icon :size="40" :color="item.color">
                <component :is="item.icon" />
              </el-icon>
              <div class="menu-title">{{ item.title }}</div>
              <div class="menu-desc">{{ item.desc }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 最新公告 -->
      <div class="notice-section">
        <div class="section-header">
          <h2>最新公告</h2>
          <el-button text type="primary" @click="navigateTo('/notice')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <el-card v-loading="noticeLoading">
          <div v-if="notices.length === 0" class="empty-state">
            <el-empty description="暂无公告" />
          </div>
          <div v-else class="notice-list">
            <div
              v-for="notice in notices"
              :key="notice.noticeId"
              class="notice-item"
              @click="viewNotice(notice.noticeId)"
            >
              <div class="notice-title">
                <el-tag v-if="notice.isTop" type="danger" size="small">置顶</el-tag>
                <span>{{ notice.noticeTitle }}</span>
              </div>
              <div class="notice-meta">
                <span>{{ notice.writerName }}</span>
                <span>{{ formatTime(notice.createTime) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  User,
  SwitchButton,
  Setting,
  Bell,
  ChatDotRound,
  Reading,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getNoticeList } from '@/api/notice.mock.js'
import { formatTime } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()
const notices = ref([])
const noticeLoading = ref(false)

const menuItems = [
  {
    title: '公告栏',
    desc: '查看村务公告',
    icon: 'Bell',
    color: '#409EFF',
    path: '/notice'
  },
  {
    title: '民情反馈',
    desc: '提交投诉建议',
    icon: 'ChatDotRound',
    color: '#67C23A',
    path: '/complaint'
  },
  {
    title: '学习园地',
    desc: '浏览学习资料',
    icon: 'Reading',
    color: '#E6A23C',
    path: '/study'
  },
  {
    title: '个人中心',
    desc: '查看个人信息',
    icon: 'User',
    color: '#F56C6C',
    path: '/profile'
  }
]

const navigateTo = (path) => {
  router.push(path)
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'admin') {
    router.push('/admin')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}

const viewNotice = (noticeId) => {
  router.push(`/notice/${noticeId}`)
}

const loadNotices = async () => {
  try {
    noticeLoading.value = true
    const list = await getNoticeList(1, 5)
    notices.value = list
  } catch (error) {
    console.error('加载公告失败:', error)
  } finally {
    noticeLoading.value = false
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info span {
  color: #666;
  font-size: 14px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.quick-access {
  margin-bottom: 30px;
}

.menu-card {
  background: white;
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.menu-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-top: 12px;
}

.menu-desc {
  font-size: 13px;
  color: #999;
  margin-top: 6px;
}

.notice-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.notice-item:hover {
  background: #f5f7fa;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #999;
}

.empty-state {
  padding: 40px 0;
}
</style>
