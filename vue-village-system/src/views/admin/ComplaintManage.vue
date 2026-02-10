<template>
  <div class="complaint-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉管理</span>
          <el-radio-group v-model="statusFilter" @change="handleFilterChange">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">待处理</el-radio-button>
            <el-radio-button label="1">处理中</el-radio-button>
            <el-radio-button label="2">已完成</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 投诉列表 -->
      <el-table :data="filteredComplaints" v-loading="loading" stripe>
        <el-table-column prop="questionId" label="编号" width="80" />
        <el-table-column label="投诉内容" min-width="200">
          <template #default="{ row }">
            {{ row.questionCtx?.substring(0, 50) }}...
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.nodeType)" size="small">
              {{ getStatusText(row.nodeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionWriterName" label="提交人" width="100" />
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.questionTime) }}
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="100">
          <template #default="{ row }">
            {{ row.choiceUserName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.nodeType === 0"
              text
              type="primary"
              @click="handleComplaint(row)"
            >
              处理
            </el-button>
            <el-button text type="primary" @click="viewDetail(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadComplaints"
        />
      </div>
    </el-card>

    <!-- 处理投诉对话框 -->
    <el-dialog v-model="showHandleDialog" title="处理投诉" width="600px">
      <div v-if="currentComplaint" class="complaint-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="投诉编号">
            {{ currentComplaint.questionId }}
          </el-descriptions-item>
          <el-descriptions-item label="投诉内容">
            <div class="content-text">{{ currentComplaint.questionCtx }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="提交人">
            {{ currentComplaint.questionWriterName }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatTime(currentComplaint.questionTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-form :model="handleForm" label-width="100px">
          <el-form-item label="处理状态">
            <el-radio-group v-model="handleForm.nodeType">
              <el-radio :label="1">处理中</el-radio>
              <el-radio :label="2">已完成</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="处理说明">
            <el-input
              v-model="handleForm.replyContent"
              type="textarea"
              :rows="4"
              placeholder="请填写处理说明..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitting">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="投诉详情" width="600px">
      <div v-if="currentComplaint">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="投诉编号">
            {{ currentComplaint.questionId }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentComplaint.nodeType)">
              {{ getStatusText(currentComplaint.nodeType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="投诉内容">
            <div class="content-text">{{ currentComplaint.questionCtx }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="提交人">
            {{ currentComplaint.questionWriterName }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ formatTime(currentComplaint.questionTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentComplaint.choiceUser" label="处理人">
            {{ currentComplaint.choiceUserName }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentComplaint.replyContent" label="处理结果">
            <div class="content-text">{{ currentComplaint.replyContent }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getComplaintList, updateComplaint } from '@/api/complaint.js'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const complaints = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')
const showHandleDialog = ref(false)
const showDetailDialog = ref(false)
const currentComplaint = ref(null)

const handleForm = reactive({
  nodeType: 1,
  replyContent: ''
})

const filteredComplaints = computed(() => {
  if (!statusFilter.value) {
    return complaints.value
  }
  return complaints.value.filter(c => String(c.nodeType) === statusFilter.value)
})

const getStatusType = (nodeType) => {
  const typeMap = { 0: 'warning', 1: 'primary', 2: 'success' }
  return typeMap[nodeType] || 'info'
}

const getStatusText = (nodeType) => {
  const textMap = { 0: '待处理', 1: '处理中', 2: '已完成' }
  return textMap[nodeType] || '未知'
}

const handleFilterChange = () => {
  page.value = 1
}

const handleComplaint = (complaint) => {
  currentComplaint.value = complaint
  handleForm.nodeType = 1
  handleForm.replyContent = ''
  showHandleDialog.value = true
}

const viewDetail = (complaint) => {
  currentComplaint.value = complaint
  showDetailDialog.value = true
}

const submitHandle = async () => {
  try {
    if (!handleForm.replyContent) {
      ElMessage.warning('请填写处理说明')
      return
    }

    submitting.value = true

    const questionInfo = {
      questionId: currentComplaint.value.questionId,
      nodeType: handleForm.nodeType,
      choiceUser: userStore.userTel,
      choiceUserName: userStore.userName,
      replyContent: handleForm.replyContent
    }

    await updateComplaint(questionInfo)
    ElMessage.success('处理成功')
    showHandleDialog.value = false
    loadComplaints()
  } catch (error) {
    ElMessage.error('处理失败')
  } finally {
    submitting.value = false
  }
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
.complaint-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.complaint-info {
  margin-top: 10px;
}

.content-text {
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
