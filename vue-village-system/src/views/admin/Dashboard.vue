<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              <el-icon :size="32"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.noticeCount }}</div>
              <div class="stat-label">发布公告</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f4f4f5; color: #909399">
              <el-icon :size="32"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.complaintCount }}</div>
              <div class="stat-label">待处理投诉</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff; color: #67c23a">
              <el-icon :size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studyCount }}</div>
              <div class="stat-label">学习资料</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="action-buttons">
            <el-button type="primary" :icon="Plus" @click="goTo('/admin/notices')">
              发布公告
            </el-button>
            <el-button type="success" :icon="Upload" @click="goTo('/admin/study')">
              上传资料
            </el-button>
            <el-button type="warning" :icon="Document" @click="goTo('/admin/complaints')">
              处理投诉
            </el-button>
            <el-button type="info" :icon="User" @click="goTo('/admin/users')">
              用户管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新投诉 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待处理投诉</span>
              <el-button text type="primary" @click="goTo('/admin/complaints')">
                查看全部
              </el-button>
            </div>
          </template>
          <div v-if="recentComplaints.length === 0" class="empty-state">
            <el-empty description="暂无待处理投诉" :image-size="80" />
          </div>
          <div v-else class="list-items">
            <div
              v-for="item in recentComplaints"
              :key="item.questionId"
              class="list-item"
            >
              <div class="item-content">
                <div class="item-title">{{ item.questionCtx?.substring(0, 30) }}...</div>
                <div class="item-meta">{{ formatRelativeTime(item.createTime) }}</div>
              </div>
              <el-tag type="warning" size="small">待处理</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最新公告 -->
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
              <el-button text type="primary" @click="goTo('/admin/notices')">
                查看全部
              </el-button>
            </div>
          </template>
          <div v-if="recentNotices.length === 0" class="empty-state">
            <el-empty description="暂无公告" :image-size="80" />
          </div>
          <div v-else class="list-items">
            <div
              v-for="item in recentNotices"
              :key="item.noticeId"
              class="list-item"
            >
              <div class="item-content">
                <div class="item-title">{{ item.noticeTitle }}</div>
                <div class="item-meta">{{ formatRelativeTime(item.createTime) }}</div>
              </div>
              <el-tag v-if="item.isTop" type="danger" size="small">置顶</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  Bell,
  ChatDotRound,
  Reading,
  Plus,
  Upload,
  Document
} from '@element-plus/icons-vue'
import { getNoticeList } from '@/api/notice'
import { getComplaintList } from '@/api/complaint'
import { formatRelativeTime } from '@/utils/format'

const router = useRouter()

const stats = ref({
  userCount: 0,
  noticeCount: 0,
  complaintCount: 0,
  studyCount: 0
})

const recentComplaints = ref([])
const recentNotices = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = async () => {
  try {
    // 加载最新公告
    const notices = await getNoticeList(1, 5)
    recentNotices.value = notices
    stats.value.noticeCount = notices.length

    // 加载待处理投诉
    const complaints = await getComplaintList(1, 5)
    recentComplaints.value = complaints.filter(c => c.nodeType === 0)
    stats.value.complaintCount = recentComplaints.value.length

    // 其他统计数据可以从后端获取
    stats.value.userCount = 128
    stats.value.studyCount = 45
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-row {
  margin-bottom: 0;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.list-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.2s;
}

.list-item:hover {
  background: #f5f7fa;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
}

.item-meta {
  font-size: 12px;
  color: #999;
}

.empty-state {
  padding: 20px 0;
}
</style>
