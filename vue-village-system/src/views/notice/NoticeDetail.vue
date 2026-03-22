<template>
  <div class="notice-detail-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>公告详情</h2>
      <div></div>
    </div>

    <!-- 公告内容 -->
    <div class="notice-content">
      <el-card v-loading="loading">
        <div v-if="notice" class="notice-detail">
          <!-- 标题 -->
          <div class="notice-title">
            <el-tag v-if="notice.isTop" type="danger" effect="dark">置顶</el-tag>
            <h1>{{ notice.noticeTitle }}</h1>
          </div>

          <!-- 元信息 -->
          <div class="notice-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>发布人：{{ notice.writerName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>发布时间：{{ formatTime(notice.noticeCreateTime) }}</span>
            </div>
          </div>

          <el-divider />

          <!-- 正文 -->
          <div class="notice-body">
            <div class="content-text">{{ notice.noticeContext }}</div>
          </div>

          <!-- 附件 -->
          <div v-if="attachmentInfo" class="attachments">
            <el-divider />
            <h3>附件</h3>
            <div class="attachment-display">
              <!-- 图片预览 -->
              <div v-if="attachmentInfo.type === 'image'" class="image-attachment">
                <el-image
                  :src="attachmentInfo.url"
                  :preview-src-list="[attachmentInfo.url]"
                  fit="contain"
                  style="max-width: 100%; max-height: 400px; border-radius: 8px;"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>图片加载失败</span>
                    </div>
                  </template>
                </el-image>
              </div>
              <!-- 文件信息 -->
              <div v-else class="file-attachment">
                <div class="file-info">
                  <el-icon size="48" color="#409EFF"><Document /></el-icon>
                  <div class="file-details">
                    <p class="file-name">{{ attachmentInfo.fileName }}</p>
                    <p class="file-size">{{ formatFileSize(attachmentInfo.size) }}</p>
                  </div>
                </div>
              </div>
              <!-- 下载按钮 -->
              <div class="attachment-actions">
                <el-button type="primary" @click="downloadAttachment">
                  <el-icon><Download /></el-icon>
                  下载附件
                </el-button>
              </div>
            </div>
          </div>

          <!-- 已读确认 -->
          <div v-if="notice.isAcceptRead && !hasRead" class="read-confirm">
            <el-button type="primary" @click="confirmRead" :loading="confirming">
              确认已读
            </el-button>
          </div>

          <div v-if="hasRead" class="read-status">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span>您已确认阅读此公告</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  User,
  Clock,
  Document,
  CircleCheck,
  Picture,
  Download
} from '@element-plus/icons-vue'
import { getNoticeList, markNoticeRead, getNoticeReadList } from '@/api/notice.js'
import { formatTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const confirming = ref(false)
const notice = ref(null)
const hasRead = ref(false)
const attachmentInfo = ref(null) // 附件信息

const noticeId = parseInt(route.params.id)

const goBack = () => {
  router.back()
}

const loadNotice = async () => {
  try {
    loading.value = true
    // 实际应该有单独的获取详情接口
    const list = await getNoticeList(1, 100)
    notice.value = list.find(n => n.noticeId === noticeId)

    if (!notice.value) {
      ElMessage.error('公告不存在')
      goBack()
      return
    }

    // 处理附件
    if (notice.value.noticeAtt && notice.value.noticeAtt.length > 0) {
      const decoded = decodeFileWithName(notice.value.noticeAtt)
      const blob = new Blob([decoded.fileData], { type: decoded.fileInfo.mimeType })
      const url = URL.createObjectURL(blob)

      attachmentInfo.value = {
        blob: blob,
        url: url,
        size: decoded.fileData.length,
        type: decoded.fileInfo.type,
        extension: decoded.fileInfo.extension,
        fileName: decoded.fileName,
        mimeType: decoded.fileInfo.mimeType
      }

      console.log('附件信息 - 文件名:', decoded.fileName, '类型:', decoded.fileInfo.type)
    }

    // 检查是否已读
    if (notice.value.isAcceptRead) {
      try {
        const readList = await getNoticeReadList()
        // readList 是已读公告ID列表，检查当前公告ID是否在其中
        hasRead.value = readList.includes(noticeId)
        console.log('公告阅读状态:', hasRead.value ? '已读' : '未读', '已读公告数:', readList.length)
      } catch (error) {
        console.error('获取阅读状态失败:', error)
      }
    }
  } catch (error) {
    ElMessage.error('加载公告失败')
  } finally {
    loading.value = false
  }
}

const confirmRead = async () => {
  try {
    confirming.value = true
    await markNoticeRead(noticeId)
    hasRead.value = true
    ElMessage.success('已确认阅读')
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    confirming.value = false
  }
}

// 从二进制数据中解码文件名
const decodeFileWithName = (data) => {
  try {
    if (data.length < 4) {
      throw new Error('数据太短')
    }

    const lengthView = new DataView(data.buffer, data.byteOffset, 4)
    const fileNameLength = lengthView.getUint32(0, true)

    if (fileNameLength < 0 || fileNameLength > 255 || fileNameLength + 4 > data.length) {
      console.log('检测到旧格式数据，使用文件头检测')
      const fileInfo = detectFileType(data)
      return {
        fileName: `附件.${fileInfo.extension}`,
        fileData: data,
        fileInfo: fileInfo
      }
    }

    const fileNameBytes = data.slice(4, 4 + fileNameLength)
    const fileName = new TextDecoder().decode(fileNameBytes)
    const fileData = data.slice(4 + fileNameLength)
    const fileInfo = detectFileType(fileData)

    return {
      fileName: fileName,
      fileData: fileData,
      fileInfo: fileInfo
    }
  } catch (error) {
    console.error('解码文件失败，使用默认处理:', error)
    const fileInfo = detectFileType(data)
    return {
      fileName: `附件.${fileInfo.extension}`,
      fileData: data,
      fileInfo: fileInfo
    }
  }
}

// 检测文件类型
const detectFileType = (uint8Array) => {
  if (!uint8Array || uint8Array.length < 4) return { type: 'unknown', extension: 'bin', mimeType: 'application/octet-stream' }

  const header = Array.from(uint8Array.slice(0, 8))

  if (header[0] === 0xFF && header[1] === 0xD8 && header[2] === 0xFF) {
    return { type: 'image', extension: 'jpg', mimeType: 'image/jpeg' }
  }

  if (header[0] === 0x89 && header[1] === 0x50 && header[2] === 0x4E && header[3] === 0x47) {
    return { type: 'image', extension: 'png', mimeType: 'image/png' }
  }

  if (header[0] === 0x47 && header[1] === 0x49 && header[2] === 0x46) {
    return { type: 'image', extension: 'gif', mimeType: 'image/gif' }
  }

  if (header[0] === 0x25 && header[1] === 0x50 && header[2] === 0x44 && header[3] === 0x46) {
    return { type: 'pdf', extension: 'pdf', mimeType: 'application/pdf' }
  }

  if (header[0] === 0x50 && header[1] === 0x4B && header[2] === 0x03 && header[3] === 0x04) {
    return { type: 'word', extension: 'docx', mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }
  }

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
  if (!attachmentInfo.value) return

  const a = document.createElement('a')
  a.href = attachmentInfo.value.url
  a.download = attachmentInfo.value.fileName
  a.click()

  ElMessage.success(`正在下载 ${attachmentInfo.value.fileName}`)
}

const downloadFile = (file) => {
  // 实现文件下载逻辑
  ElMessage.info('下载功能开发中')
}

onMounted(() => {
  loadNotice()
})
</script>

<style scoped>
.notice-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.header {
  max-width: 900px;
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

.notice-content {
  max-width: 900px;
  margin: 0 auto;
}

.notice-detail {
  padding: 20px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.notice-title h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.notice-body {
  margin: 30px 0;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.attachments {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.attachments h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: white;
  border-radius: 6px;
}

.attachment-item span {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.read-confirm {
  margin-top: 30px;
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.read-status {
  margin-top: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 8px;
  color: #67C23A;
  font-size: 14px;
}
</style>
