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
          <div v-if="notice.attachments && notice.attachments.length > 0" class="attachments">
            <h3>附件</h3>
            <div class="attachment-list">
              <div
                v-for="(file, index) in notice.attachments"
                :key="index"
                class="attachment-item"
              >
                <el-icon><Document /></el-icon>
                <span>{{ file.name }}</span>
                <el-button text type="primary" @click="downloadFile(file)">
                  下载
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
  CircleCheck
} from '@element-plus/icons-vue'
import { getNoticeList, markNoticeRead } from '@/api/notice.js'
import { formatTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const confirming = ref(false)
const notice = ref(null)
const hasRead = ref(false)

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
