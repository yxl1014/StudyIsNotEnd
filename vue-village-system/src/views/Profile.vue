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
            <el-button text type="primary" @click="showEditDialog = true">
              编辑
            </el-button>
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

    <!-- 编辑信息对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑个人信息" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.userName" />
        </el-form-item>
        <el-form-item label="修改密码">
          <el-input
            v-model="editForm.userPwd"
            type="password"
            placeholder="不修改请留空"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/user.js'
import { formatTime } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()
const showEditDialog = ref(false)
const updating = ref(false)

const editForm = reactive({
  userName: userStore.userInfo?.userName || '',
  userPwd: ''
})

const goBack = () => {
  router.back()
}

const handleUpdate = async () => {
  try {
    updating.value = true

    const updateData = {
      userTel: userStore.userInfo.userTel,
      userName: editForm.userName
    }

    if (editForm.userPwd) {
      updateData.userPwd = editForm.userPwd
    }

    await updateUserInfo(updateData)

    // 更新本地用户信息
    userStore.setUserInfo({
      ...userStore.userInfo,
      userName: editForm.userName
    })

    ElMessage.success('更新成功')
    showEditDialog.value = false
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    updating.value = false
  }
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
