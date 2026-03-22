<template>
  <div class="notice-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" :icon="Plus" @click="createNewNotice">
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
      @close="handleDialogClose"
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
          <!-- 显示已有附件 -->
          <div v-if="existingAttachment" class="existing-attachment">
            <div class="attachment-preview">
              <!-- 图片预览 -->
              <img
                v-if="existingAttachment.type === 'image'"
                :src="existingAttachment.url"
                alt="附件预览"
                style="max-width: 200px; max-height: 200px; border-radius: 4px; cursor: pointer;"
                @click="previewImage"
              />
              <!-- 文件信息 -->
              <div v-else class="file-info">
                <el-icon size="48" color="#409EFF"><Document /></el-icon>
                <div class="file-details">
                  <p class="file-name">{{ existingAttachment.fileName }}</p>
                  <p class="file-size">{{ formatFileSize(existingAttachment.size) }}</p>
                </div>
              </div>
            </div>
            <div class="attachment-actions">
              <el-button size="small" type="primary" @click="downloadAttachment">
                <el-icon><Download /></el-icon>
                下载附件
              </el-button>
              <el-button size="small" type="danger" @click="removeExistingAttachment">
                <el-icon><Delete /></el-icon>
                删除附件
              </el-button>
            </div>
          </div>

          <!-- 上传新附件 -->
          <el-upload
            v-else
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
import { Plus, Upload, Document, Download, Delete } from '@element-plus/icons-vue'
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
const existingAttachment = ref(null) // 已有的附件信息

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

// 创建新公告
const createNewNotice = () => {
  resetForm() // 先重置表单
  showCreateDialog.value = true
  console.log('打开发布公告弹窗，表单已重置')
}

const editNotice = (notice) => {
  editingNotice.value = notice
  Object.assign(noticeForm, {
    noticeTitle: notice.noticeTitle,
    noticeType: notice.noticeType || 0,
    noticeContext: notice.noticeContext,
    isTop: notice.isTop,
    isAcceptRead: notice.isAcceptRead
  })

  // 处理附件：如果有附件，显示预览
  attachmentList.value = []
  existingAttachment.value = null

  if (notice.noticeAtt && notice.noticeAtt.length > 0) {
    console.log('检测到已有附件，大小:', notice.noticeAtt.length, 'bytes')

    // 解码文件名和数据
    const decoded = decodeFileWithName(notice.noticeAtt)
    const blob = new Blob([decoded.fileData], { type: decoded.fileInfo.mimeType })
    const url = URL.createObjectURL(blob)

    existingAttachment.value = {
      blob: blob,
      url: url,
      size: decoded.fileData.length,
      type: decoded.fileInfo.type,
      extension: decoded.fileInfo.extension,
      fileName: decoded.fileName,
      data: notice.noticeAtt // 保存原始二进制数据（含文件名编码）
    }

    console.log('附件信息 - 文件名:', decoded.fileName, '类型:', decoded.fileInfo.type, '大小:', decoded.fileData.length)
  }

  showCreateDialog.value = true
}

// 将文件名编码到二进制数据中
const encodeFileWithName = (fileData, fileName) => {
  // 编码文件名为 UTF-8
  const fileNameBytes = new TextEncoder().encode(fileName)
  const fileNameLength = fileNameBytes.length

  // 限制文件名长度（最大255字节）
  if (fileNameLength > 255) {
    ElMessage.warning('文件名过长，将被截断')
    const truncatedName = fileName.substring(0, 100) // 截断到100个字符
    return encodeFileWithName(fileData, truncatedName)
  }

  // 创建新的数组：[4字节长度] + [文件名] + [文件数据]
  const result = new Uint8Array(4 + fileNameLength + fileData.length)

  // 写入文件名长度（4字节，小端序）
  const lengthView = new DataView(result.buffer, 0, 4)
  lengthView.setUint32(0, fileNameLength, true)

  // 写入文件名
  result.set(fileNameBytes, 4)

  // 写入文件数据
  result.set(fileData, 4 + fileNameLength)

  console.log('文件编码完成 - 文件名:', fileName, '文件名长度:', fileNameLength, '总长度:', result.length)

  return result
}

// 从二进制数据中解码文件名
const decodeFileWithName = (data) => {
  try {
    // 检查数据长度
    if (data.length < 4) {
      throw new Error('数据太短')
    }

    // 读取文件名长度
    const lengthView = new DataView(data.buffer, data.byteOffset, 4)
    const fileNameLength = lengthView.getUint32(0, true)

    // 验证文件名长度是否合理（0-255字节）
    if (fileNameLength < 0 || fileNameLength > 255 || fileNameLength + 4 > data.length) {
      // 可能是旧数据（没有文件名编码），使用文件头检测
      console.log('检测到旧格式数据，使用文件头检测')
      const fileInfo = detectFileType(data)
      return {
        fileName: `附件.${fileInfo.extension}`,
        fileData: data,
        fileInfo: fileInfo
      }
    }

    // 读取文件名
    const fileNameBytes = data.slice(4, 4 + fileNameLength)
    const fileName = new TextDecoder().decode(fileNameBytes)

    // 读取文件数据
    const fileData = data.slice(4 + fileNameLength)

    // 检测文件类型
    const fileInfo = detectFileType(fileData)

    console.log('文件解码完成 - 文件名:', fileName, '数据长度:', fileData.length)

    return {
      fileName: fileName,
      fileData: fileData,
      fileInfo: fileInfo
    }
  } catch (error) {
    console.error('解码文件失败，使用默认处理:', error)
    // 出错时使用文件头检测
    const fileInfo = detectFileType(data)
    return {
      fileName: `附件.${fileInfo.extension}`,
      fileData: data,
      fileInfo: fileInfo
    }
  }
}

// 检测文件类型（通过文件头）
const detectFileType = (uint8Array) => {
  if (!uint8Array || uint8Array.length < 4) return { type: 'unknown', extension: 'bin', mimeType: 'application/octet-stream' }

  const header = Array.from(uint8Array.slice(0, 8))

  // JPEG: FF D8 FF
  if (header[0] === 0xFF && header[1] === 0xD8 && header[2] === 0xFF) {
    return { type: 'image', extension: 'jpg', mimeType: 'image/jpeg' }
  }

  // PNG: 89 50 4E 47
  if (header[0] === 0x89 && header[1] === 0x50 && header[2] === 0x4E && header[3] === 0x47) {
    return { type: 'image', extension: 'png', mimeType: 'image/png' }
  }

  // GIF: 47 49 46
  if (header[0] === 0x47 && header[1] === 0x49 && header[2] === 0x46) {
    return { type: 'image', extension: 'gif', mimeType: 'image/gif' }
  }

  // PDF: 25 50 44 46 (%PDF)
  if (header[0] === 0x25 && header[1] === 0x50 && header[2] === 0x44 && header[3] === 0x46) {
    return { type: 'pdf', extension: 'pdf', mimeType: 'application/pdf' }
  }

  // Word (.docx): 50 4B 03 04 (ZIP format, need to check further)
  if (header[0] === 0x50 && header[1] === 0x4B && header[2] === 0x03 && header[3] === 0x04) {
    return { type: 'word', extension: 'docx', mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }
  }

  // Word (.doc): D0 CF 11 E0
  if (header[0] === 0xD0 && header[1] === 0xCF && header[2] === 0x11 && header[3] === 0xE0) {
    return { type: 'word', extension: 'doc', mimeType: 'application/msword' }
  }

  return { type: 'unknown', extension: 'bin', mimeType: 'application/octet-stream' }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 下载附件
const downloadAttachment = () => {
  if (!existingAttachment.value) return

  const a = document.createElement('a')
  a.href = existingAttachment.value.url
  a.download = existingAttachment.value.fileName
  a.click()

  ElMessage.success(`正在下载 ${existingAttachment.value.fileName}`)
}

// 删除已有附件
const removeExistingAttachment = () => {
  existingAttachment.value = null
  ElMessage.success('附件已移除，保存后生效')
}

// 预览图片
const previewImage = () => {
  if (!existingAttachment.value || existingAttachment.value.type !== 'image') return

  // 使用 Element Plus 的图片预览
  const viewer = document.createElement('div')
  viewer.innerHTML = `
    <div style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.8); z-index: 9999; display: flex; align-items: center; justify-content: center;" onclick="this.remove()">
      <img src="${existingAttachment.value.url}" style="max-width: 90%; max-height: 90%; cursor: pointer;" />
    </div>
  `
  document.body.appendChild(viewer)
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

    // 处理附件：优先使用新上传的文件
    if (attachmentList.value.length > 0 && attachmentList.value[0].raw) {
      // 用户上传了新文件
      try {
        const file = attachmentList.value[0].raw
        console.log('正在读取新附件文件:', file.name, '大小:', file.size, 'bytes')

        const arrayBuffer = await file.arrayBuffer()
        const fileData = new Uint8Array(arrayBuffer)

        // 编码文件名到二进制数据中
        const encodedData = encodeFileWithName(fileData, file.name)

        noticeInfo.noticeAtt = encodedData
        console.log('新附件已转换为二进制数据（含文件名），总长度:', encodedData.length, 'bytes')
      } catch (error) {
        console.error('读取附件失败:', error)
        ElMessage.error('读取附件失败')
        submitting.value = false
        return
      }
    } else if (existingAttachment.value) {
      // 保留原有附件
      noticeInfo.noticeAtt = existingAttachment.value.data
      console.log('保留原有附件，长度:', existingAttachment.value.data.length, 'bytes')
    }
    // 如果两者都没有，则不设置 noticeAtt（删除附件）

    if (editingNotice.value) {
      // 编辑模式：保留原来的 ID 和创建时间
      noticeInfo.noticeId = editingNotice.value.noticeId
      noticeInfo.noticeCreateTime = editingNotice.value.noticeCreateTime

      // 如果用户删除了附件，发送空的 Uint8Array
      if (!noticeInfo.noticeAtt && editingNotice.value.noticeAtt) {
        noticeInfo.noticeAtt = new Uint8Array(0)
        console.log('附件已删除，发送空数据')
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
  existingAttachment.value = null
  Object.assign(noticeForm, {
    noticeTitle: '',
    noticeType: 0, // 默认为通知类
    noticeContext: '',
    isTop: false,
    isAcceptRead: false
  })

  // 重置表单验证
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 处理弹窗关闭事件
const handleDialogClose = () => {
  resetForm()
  console.log('弹窗已关闭，表单已重置')
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

.existing-attachment {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  background: #f5f7fa;
}

.attachment-preview {
  margin-bottom: 12px;
  text-align: center;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.file-details {
  flex: 1;
}

.file-name {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  word-break: break-all;
}

.file-size {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.attachment-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 12px;
}
</style>
