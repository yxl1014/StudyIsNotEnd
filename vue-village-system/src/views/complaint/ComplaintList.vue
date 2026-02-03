<template>
  <div class="complaint-list-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>民情反馈</h2>
      <el-button type="primary" @click="createComplaint" :icon="Plus">
        提交投诉
      </el-button>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="handleFilterChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="待处理">待处理</el-radio-button>
        <el-radio-button label="处理中">处理中</el-radio-button>
        <el-radio-button label="已完成">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 投诉列表 -->
    <div class="complaint-content">
      <el-card v-loading="loading">
        <div v-if="filteredComplaints.length === 0" class="empty-state">
          <el-empty description="暂无投诉记录" />
        </div>
        <div v-else class="complaint-items">
          <div
            v-for="complaint in filteredComplaints"
            :key="complaint.questionId"
            class="complaint-item"
            @click="viewComplaint(complaint)"
          >
            <div class="complaint-header">
              <div class="complaint-title">
                <el-tag :type="getStatusType(complaint.nodeType)" size="small">
                  {{ getStatusText(complaint.nodeType) }}
                </el-tag>
                <span>{{ complaint.questionCtx?.substring(0, 50) }}...</span>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
            <div class="complaint-meta">
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>{{ formatRelativeTime(complaint.createTime) }}</span>
              </div>
              <div v-if="complaint.choiceUser" class="meta-item">
                <el-icon><User /></el-icon>
                <span>处理人：{{ complaint.choiceUserName }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 分页 -->
      <div v-if="total > size" class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadComplaints"
        />
      </div>
    </div>

    <!-- 投诉详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="投诉详情"
      width="600px"
    >
      <div v-if="currentComplaint" class="complaint-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="投诉编号">
            {{ currentComplaint.questionId }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentComplaint.nodeType)">
              {{ getStatusText(currentComplaint.nodeType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatTime(currentComplaint.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="投诉内容">
            <div class="content-text">{{ currentComplaint.questionCtx }}</div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentComplaint.choiceUser" label="处理人">
            {{ currentComplaint.choiceUserName }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentComplaint.replyContent" label="处理结果">
            <div class="content-text">{{ currentComplaint.replyContent }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 满意度评价 -->
        <div v-if="currentComplaint.nodeType === 2" class="satisfaction-section">
          <el-divider />
          <div class="satisfaction-title">满意度评价</div>

          <!-- 已评价 -->
          <div v-if="currentComplaint.satisfactionRating" class="rated">
            <div class="rating-display">
              <el-rate
                v-model="currentComplaint.satisfactionRating"
                disabled
                show-score
                text-color="#ff9900"
              />
              <span class="rating-text">{{ getRatingText(currentComplaint.satisfactionRating) }}</span>
            </div>
            <div class="rating-time">
              评价时间：{{ formatTime(currentComplaint.ratingTime) }}
            </div>
          </div>

          <!-- 未评价 -->
          <div v-else class="not-rated">
            <div class="rating-input">
              <el-rate
                v-model="tempRating"
                :texts="['非常不满意', '不满意', '一般', '满意', '非常满意']"
                show-text
                :colors="['#F56C6C', '#E6A23C', '#409EFF', '#67C23A', '#67C23A']"
              />
            </div>
            <el-button
              type="primary"
              size="small"
              @click="submitRating"
              :disabled="tempRating === 0"
              :loading="submittingRating"
            >
              提交评价
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  ArrowRight,
  Plus,
  Clock,
  User
} from '@element-plus/icons-vue'
import { getComplaintList } from '@/api/complaint.mock.js'
import { formatTime, formatRelativeTime } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const complaints = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')
const showDetailDialog = ref(false)
const currentComplaint = ref(null)
const tempRating = ref(0)
const submittingRating = ref(false)

const filteredComplaints = computed(() => {
  if (!statusFilter.value) {
    return complaints.value
  }
  return complaints.value.filter(c => getStatusText(c.nodeType) === statusFilter.value)
})

const goBack = () => {
  router.back()
}

const createComplaint = () => {
  router.push('/complaint/create')
}

const viewComplaint = (complaint) => {
  currentComplaint.value = complaint
  tempRating.value = 0
  showDetailDialog.value = true
}

const getRatingText = (rating) => {
  const textMap = {
    1: '非常不满意',
    2: '不满意',
    3: '一般',
    4: '满意',
    5: '非常满意'
  }
  return textMap[rating] || ''
}

const submitRating = async () => {
  if (tempRating.value === 0) {
    ElMessage.warning('请选择评分')
    return
  }

  try {
    submittingRating.value = true

    // 调用评价接口（Mock）
    await new Promise(resolve => setTimeout(resolve, 500))

    // 更新当前投诉的评分
    currentComplaint.value.satisfactionRating = tempRating.value
    currentComplaint.value.ratingTime = Date.now()

    // 更新列表中的数据
    const index = complaints.value.findIndex(c => c.questionId === currentComplaint.value.questionId)
    if (index !== -1) {
      complaints.value[index].satisfactionRating = tempRating.value
      complaints.value[index].ratingTime = Date.now()
    }

    ElMessage.success('评价成功，感谢您的反馈！')
    tempRating.value = 0
  } catch (error) {
    ElMessage.error('评价失败')
  } finally {
    submittingRating.value = false
  }
}

const handleFilterChange = () => {
  page.value = 1
  loadComplaints()
}

const getStatusType = (nodeType) => {
  const typeMap = {
    0: 'warning',  // 待处理
    1: 'primary',  // 处理中
    2: 'success'   // 已完成
  }
  return typeMap[nodeType] || 'info'
}

const getStatusText = (nodeType) => {
  const textMap = {
    0: '待处理',
    1: '处理中',
    2: '已完成'
  }
  return textMap[nodeType] || '未知'
}

const loadComplaints = async () => {
  try {
    loading.value = true
    const list = await getComplaintList(page.value, size.value)
    complaints.value = list
    total.value = list.length
  } catch (error) {
    ElMessage.error('加载投诉列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadComplaints()
})
</script>

<style scoped>
.complaint-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.header {
  max-width: 800px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.filter-bar {
  max-width: 800px;
  margin: 0 auto 20px;
}

.complaint-content {
  max-width: 800px;
  margin: 0 auto;
}

.complaint-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.complaint-item {
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.complaint-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
}

.complaint-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.complaint-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #333;
  flex: 1;
}

.arrow-icon {
  color: #999;
  transition: transform 0.2s;
}

.complaint-item:hover .arrow-icon {
  transform: translateX(4px);
  color: #409EFF;
}

.complaint-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-state {
  padding: 60px 0;
}

.complaint-detail {
  padding: 10px 0;
}

.content-text {
  line-height: 1.6;
  white-space: pre-wrap;
}

.satisfaction-section {
  margin-top: 20px;
}

.satisfaction-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.rated {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.rating-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

.rating-time {
  font-size: 12px;
  color: #999;
}

.not-rated {
  padding: 16px;
  background: #fff9e6;
  border-radius: 8px;
  border: 1px dashed #e6a23c;
}

.rating-input {
  margin-bottom: 16px;
}
</style>
