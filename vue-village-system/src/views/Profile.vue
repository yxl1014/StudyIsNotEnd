<template>
  <div class="profile-container">
    <div class="profile-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>个人中心</h2>
      <div></div>
    </div>

    <div class="profile-content">
      <el-card class="user-card">
        <div class="user-avatar">
          <el-avatar :size="80">{{ userStore.userName.charAt(0) }}</el-avatar>
        </div>
        <div class="user-name">{{ userStore.userName }}</div>
        <div class="user-role">
          <el-tag v-if="userStore.isAdmin" type="success">村干部</el-tag>
          <el-tag v-else>村民</el-tag>
        </div>
      </el-card>

      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="手机号">
            {{ userStore.userInfo?.userTel }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ userStore.userInfo?.userName }}
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatTime(userStore.userInfo?.userCreateTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag v-if="userStore.userInfo?.flagType === 0" type="success">
              正常
            </el-tag>
            <el-tag v-else type="danger">已冻结</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.profile-header {
  max-width: 800px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.profile-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-card {
  text-align: center;
  padding: 20px;
}

.user-avatar {
  margin-bottom: 16px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.user-role {
  margin-top: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
