<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <el-icon :size="24"><House /></el-icon>
        <span>村务管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/complaints">
          <el-icon><ChatDotRound /></el-icon>
          <span>投诉管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/study">
          <el-icon><Reading /></el-icon>
          <span>学习管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/people">
          <el-icon><Tickets /></el-icon>
          <span>信息管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbTitle">{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button text @click="goToHome">
            <el-icon><HomeFilled /></el-icon>
            返回前台
          </el-button>
          <el-dropdown @command="handleCommand">
            <div class="user-dropdown">
              <el-avatar :size="32">{{ userStore.userName.charAt(0) }}</el-avatar>
              <span>{{ userStore.userName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  DataAnalysis,
  User,
  Bell,
  ChatDotRound,
  Reading,
  Tickets,
  HomeFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const breadcrumbTitle = computed(() => {
  const titleMap = {
    '/admin/dashboard': '工作台',
    '/admin/users': '用户管理',
    '/admin/notices': '公告管理',
    '/admin/complaints': '投诉管理',
    '/admin/study': '学习管理',
    '/admin/people': '信息管理'
  }
  return titleMap[route.path] || ''
})

const goToHome = () => {
  router.push('/home')
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  background: #001529;
  color: white;
  overflow-y: auto;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  height: 60px;
  font-size: 18px;
  font-weight: 600;
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-menu {
  border: none;
  background: transparent;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
