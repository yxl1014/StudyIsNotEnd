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
<!--        <el-table-column prop="questionWriterName" label="提交人" width="100" />-->
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.nodeType === 0"
              text
              type="warning"
              @click="assignComplaint(row)"
            >
              分发
            </el-button>
            <el-button
              text
              type="primary"
              @click="handleComplaint(row)"
            >
              处理
            </el-button>
            <el-button text type="info" @click="viewDetail(row)">
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

    <!-- 分发投诉对话框 -->
    <el-dialog v-model="showAssignDialog" title="分发投诉" width="600px">
      <div v-if="currentComplaint" class="complaint-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="投诉编号">
            {{ currentComplaint.questionId }}
          </el-descriptions-item>
          <el-descriptions-item label="投诉内容">
            <div class="content-text">{{ currentComplaint.questionCtx }}</div>
          </el-descriptions-item>
<!--          <el-descriptions-item label="提交人">-->
<!--            {{ currentComplaint.questionWriterName }}-->
<!--          </el-descriptions-item>-->
        </el-descriptions>

        <el-divider />

        <el-form :model="assignForm" label-width="120px">
          <el-form-item label="分发给">
            <el-select
              v-model="assignForm.choiceUser"
              placeholder="请选择处理人"
              style="width: 100%"
              filterable
            >
              <el-option
                v-for="user in adminUsers"
                :key="user.userTel"
                :label="`${user.userName} (${user.userTel})`"
                :value="user.userTel"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="assignForm.remark"
              type="textarea"
              :rows="3"
              placeholder="可以添加备注信息..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assigning">
          确认分发
        </el-button>
      </template>
    </el-dialog>

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
          <el-descriptions-item label="投诉图片" v-if="currentComplaint.questPhoto && currentComplaint.questPhoto.length > 0">
            <div class="image-preview">
              <el-image
                :src="getImageUrl(currentComplaint.questPhoto)"
                :preview-src-list="[getImageUrl(currentComplaint.questPhoto)]"
                fit="cover"
                style="width: 200px; height: 200px; border-radius: 4px;"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>图片加载失败</span>
                  </div>
                </template>
              </el-image>
            </div>
          </el-descriptions-item>
<!--          <el-descriptions-item label="提交人">-->
<!--            {{ currentComplaint.questionWriterName }}-->
<!--          </el-descriptions-item>-->
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
          <el-descriptions-item label="投诉图片" v-if="currentComplaint.questPhoto && currentComplaint.questPhoto.length > 0">
            <div class="image-preview">
              <el-image
                :src="getImageUrl(currentComplaint.questPhoto)"
                :preview-src-list="[getImageUrl(currentComplaint.questPhoto)]"
                fit="cover"
                style="width: 200px; height: 200px; border-radius: 4px;"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>图片加载失败</span>
                  </div>
                </template>
              </el-image>
            </div>
          </el-descriptions-item>
<!--          <el-descriptions-item label="提交人">-->
<!--            {{ currentComplaint.questionWriterName }}-->
<!--          </el-descriptions-item>-->
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
import { Picture } from '@element-plus/icons-vue'
import { getComplaintList, updateComplaint } from '@/api/complaint.js'
import { getUserList } from '@/api/user.js'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const assigning = ref(false)
const complaints = ref([])
const adminUsers = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')
const showHandleDialog = ref(false)
const showAssignDialog = ref(false)
const showDetailDialog = ref(false)
const currentComplaint = ref(null)

const handleForm = reactive({
  nodeType: 1,
  replyContent: ''
})

const assignForm = reactive({
  choiceUser: null,
  remark: ''
})

const filteredComplaints = computed(() => {
  // 不需要前端过滤，后端已经根据 statusFilter 返回对应的数据
  return complaints.value
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
  loadComplaints() // 重新加载数据
}

const handleComplaint = (complaint) => {
  currentComplaint.value = complaint
  handleForm.nodeType = 1
  handleForm.replyContent = ''
  showHandleDialog.value = true
}

const assignComplaint = (complaint) => {
  currentComplaint.value = complaint
  assignForm.choiceUser = null
  assignForm.remark = ''
  showAssignDialog.value = true
}

const submitAssign = async () => {
  try {
    if (!assignForm.choiceUser) {
      ElMessage.warning('请选择处理人')
      return
    }

    assigning.value = true

    // 只传递必要的字段：id 和 user
    const questionInfo = {
      questionId: currentComplaint.value.questionId,
      choiceUser: assignForm.choiceUser  // 被分配人的电话
    }

    console.log('分发投诉，传递参数:', questionInfo)

    await updateComplaint(questionInfo)
    ElMessage.success('分发成功')
    showAssignDialog.value = false
    loadComplaints()
  } catch (error) {
    console.error('分发失败:', error)
    ElMessage.error('分发失败: ' + (error.message || '未知错误'))
  } finally {
    assigning.value = false
  }
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

    // 只传递必要的字段：id 和 type
    const questionInfo = {
      questionId: currentComplaint.value.questionId,
      nodeType: handleForm.nodeType  // 1=处理中, 2=已完成
    }

    console.log('处理投诉，传递参数:', questionInfo)

    await updateComplaint(questionInfo)
    ElMessage.success('处理成功')
    showHandleDialog.value = false
    loadComplaints()
  } catch (error) {
    console.error('处理失败:', error)
    ElMessage.error('处理失败: ' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 将二进制数据转换为图片 URL
const getImageUrl = (uint8Array) => {
  if (!uint8Array || uint8Array.length === 0) return ''

  const blob = new Blob([uint8Array], { type: 'image/jpeg' })
  return URL.createObjectURL(blob)
}

const loadComplaints = async () => {
  try {
    loading.value = true
    console.log('=== 投诉管理：加载投诉列表 ===')

    // 将 statusFilter 转换为数字或 null
    const nodeType = statusFilter.value === '' ? null : parseInt(statusFilter.value)

    // 使用 ListQuestionReq - 自动根据身份返回对应的投诉
    const list = await getComplaintList(page.value, size.value, nodeType)
    complaints.value = list
    total.value = list.length
    console.log('✅ 投诉列表加载成功，数量:', list.length)
  } catch (error) {
    console.error('❌ 加载投诉列表失败:', error)
    console.error('错误详情:', error.message)
    ElMessage.error('加载投诉列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const loadAdminUsers = async () => {
  try {
    console.log('=== 加载村干部列表 ===')
    const users = await getUserList(1, 1000)
    // 过滤出村干部（userPower = 1）
    adminUsers.value = users.filter(u => u.userPower === 1)
    console.log('✅ 村干部列表加载成功，数量:', adminUsers.value.length)
  } catch (error) {
    console.error('❌ 加载村干部列表失败:', error)
  }
}

onMounted(() => {
  loadComplaints()
  loadAdminUsers()
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
