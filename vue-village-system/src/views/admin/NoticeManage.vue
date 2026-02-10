<template>
  <div class="notice-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" :icon="Plus" @click="showCreateDialog = true">
            发布公告
          </el-button>
        </div>
      </template>

      <!-- 公告列表 -->
      <el-table :data="notices" v-loading="loading" stripe>
        <el-table-column prop="noticeTitle" label="标题" min-width="200" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getNoticeTypeText(row.noticeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="writerName" label="发布人" width="100" />
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.noticeCreateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="editNotice(row)">
              编辑
            </el-button>
            <el-button text type="danger" @click="deleteNotice(row)">
              删除
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
          @current-change="loadNotices"
        />
      </div>
    </el-card>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingNotice ? '编辑公告' : '发布公告'"
      width="750px"
    >
      <el-form
        ref="formRef"
        :model="noticeForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="noticeTitle">
          <el-input v-model="noticeForm.noticeTitle" placeholder="请输入公告标题" />
        </el-form-item>

        <el-form-item label="公告类型" prop="noticeType">
          <el-select v-model="noticeForm.noticeType" placeholder="请选择公告类型">
            <el-option label="通知类" :value="0" />
            <el-option label="招聘类" :value="1" />
            <el-option label="活动类" :value="2" />
            <el-option label="政策宣传类" :value="3" />
            <el-option label="公开政务类" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="公告内容" prop="noticeContext">
          <el-input
            v-model="noticeForm.noticeContext"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            v-model:file-list="attachmentList"
            action="#"
            :auto-upload="false"
            :limit="1"
            accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
          >
            <el-button :icon="Upload">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持PDF、Word、图片格式，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="是否置顶">
          <el-switch v-model="noticeForm.isTop" />
        </el-form-item>

        <el-form-item label="需要确认阅读">
          <el-switch v-model="noticeForm.isAcceptRead" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingNotice ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { getNoticeList, createNotice, updateNotice } from '@/api/notice.js'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const notices = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showCreateDialog = ref(false)
const editingNotice = ref(null)
const formRef = ref()
const attachmentList = ref([])

// 公告类型映射
const noticeTypeMap = {
  0: '通知类',
  1: '招聘类',
  2: '活动类',
  3: '政策宣传类',
  4: '公开政务类'
}

// 获取公告类型文本
const getNoticeTypeText = (type) => {
  return noticeTypeMap[type] || '通知类'
}

const noticeForm = reactive({
  noticeTitle: '',
  noticeType: 0, // 默认为通知类 (TNT_TZ = 0)
  noticeContext: '',
  isTop: false,
  isAcceptRead: false
})

const rules = {
  noticeTitle: [
    { required: true, message: '请输入公告标题', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  noticeContext: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, message: '公告内容至少10个字', trigger: 'blur' }
  ]
}

const editNotice = (notice) => {
  editingNotice.value = notice
  Object.assign(noticeForm, {
    noticeTitle: notice.noticeTitle,
    noticeType: notice.noticeType || 0, // 使用数字类型
    noticeContext: notice.noticeContext,
    isTop: notice.isTop,
    isAcceptRead: notice.isAcceptRead
  })
  // 如果有附件，恢复附件列表
  attachmentList.value = notice.attachments || []
  showCreateDialog.value = true
}

const deleteNotice = async (notice) => {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗？', '提示', {
      type: 'warning'
    })

    await updateNotice(notice, true)
    ElMessage.success('删除成功')
    loadNotices()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    const noticeInfo = {
      noticeTitle: noticeForm.noticeTitle,
      noticeType: noticeForm.noticeType,
      noticeContext: noticeForm.noticeContext,
      writerTel: userStore.userTel,
      writerName: userStore.userName,
      isTop: noticeForm.isTop,
      isAcceptRead: noticeForm.isAcceptRead
    }

    // 处理附件：将文件转换为二进制数据
    if (attachmentList.value.length > 0 && attachmentList.value[0].raw) {
      try {
        const file = attachmentList.value[0].raw
        console.log('正在读取附件文件:', file.name, '大小:', file.size, 'bytes')

        // 读取文件为 ArrayBuffer
        const arrayBuffer = await file.arrayBuffer()
        // 转换为 Uint8Array
        const uint8Array = new Uint8Array(arrayBuffer)

        noticeInfo.noticeAtt = uint8Array
        console.log('附件已转换为二进制数据，长度:', uint8Array.length, 'bytes')
      } catch (error) {
        console.error('读取附件失败:', error)
        ElMessage.error('读取附件失败')
        submitting.value = false
        return
      }
    }

    if (editingNotice.value) {
      // 编辑模式：保留原来的 ID 和创建时间
      noticeInfo.noticeId = editingNotice.value.noticeId
      noticeInfo.noticeCreateTime = editingNotice.value.noticeCreateTime
      // 如果没有上传新附件，保留原来的附件
      if (!noticeInfo.noticeAtt && editingNotice.value.noticeAtt) {
        noticeInfo.noticeAtt = editingNotice.value.noticeAtt
      }
      await updateNotice(noticeInfo)
      ElMessage.success('更新成功')
    } else {
      // 创建模式：设置新的创建时间
      noticeInfo.noticeCreateTime = Date.now()
      await createNotice(noticeInfo)
      ElMessage.success('发布成功')
    }

    showCreateDialog.value = false
    resetForm()
    loadNotices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  editingNotice.value = null
  attachmentList.value = []
  Object.assign(noticeForm, {
    noticeTitle: '',
    noticeType: 0, // 默认为通知类
    noticeContext: '',
    isTop: false,
    isAcceptRead: false
  })
}

// 文件超出限制
const handleExceed = () => {
  ElMessage.warning('只能上传1个附件')
}

// 移除文件
const handleRemove = (file, fileList) => {
  console.log('移除文件:', file.name)
}

const loadNotices = async () => {
  try {
    loading.value = true
    const list = await getNoticeList(page.value, size.value)
    notices.value = list
    total.value = list.length
  } catch (error) {
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-manage {
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

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  line-height: 1.5;
}
</style>
