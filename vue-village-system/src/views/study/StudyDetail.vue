<template>
  <div class="study-detail-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>资料详情</h2>
      <el-button
        :icon="study?.isStar ? StarFilled : Star"
        @click="toggleStar"
      >
        {{ study?.isStar ? '已收藏' : '收藏' }}
      </el-button>
    </div>

    <!-- 资料内容 -->
    <div class="study-content">
      <el-card v-loading="loading">
        <div v-if="study" class="study-detail">
          <!-- 标题 -->
          <div class="study-title">
            <h1>{{ study.studyTitle }}</h1>
            <el-tag>{{ study.studyType }}</el-tag>
          </div>

          <!-- 元信息 -->
          <div class="study-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>上传人：{{ study.uploaderName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>上传时间：{{ formatTime(study.createTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>阅读次数：{{ study.readCount || 0 }}</span>
            </div>
          </div>

          <el-divider />

          <!-- 简介 -->
          <div v-if="study.studyIntro" class="study-intro">
            <h3>资料简介</h3>
            <p>{{ study.studyIntro }}</p>
          </div>

          <!-- 正文 -->
          <div class="study-body">
            <div class="content-text">{{ study.studyContent }}</div>
          </div>

          <!-- 附件下载 -->
          <div v-if="study.fileUrl" class="download-section">
            <el-button type="primary" :icon="Download" @click="downloadFile">
              下载资料
            </el-button>
            <span class="file-info">
              {{ study.fileName }} ({{ formatFileSize(study.fileSize) }})
            </span>
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
  Star,
  StarFilled,
  User,
  Clock,
  View,
  Download
} from '@element-plus/icons-vue'
import { getStudyList, toggleStarStudy } from '@/api/study.mock.js'
import { formatTime, formatFileSize } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const study = ref(null)

const studyId = parseInt(route.params.id)

const goBack = () => {
  router.back()
}

const loadStudy = async () => {
  try {
    loading.value = true
    // 实际应该有单独的获取详情接口
    const list = await getStudyList(1, 100)
    study.value = list.find(s => s.studyId === studyId)

    if (!study.value) {
      ElMessage.error('资料不存在')
      goBack()
    }
  } catch (error) {
    ElMessage.error('加载资料失败')
  } finally {
    loading.value = false
  }
}

const toggleStar = async () => {
  if (!study.value) return

  try {
    const newStarStatus = !study.value.isStar
    await toggleStarStudy(study.value.studyId, newStarStatus)
    study.value.isStar = newStarStatus
    ElMessage.success(newStarStatus ? '收藏成功' : '取消收藏')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const downloadFile = () => {
  // 实现文件下载逻辑
  ElMessage.info('下载功能开发中')
}

onMounted(() => {
  loadStudy()
})
</script>

<style scoped>
.study-detail-container {
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

.study-content {
  max-width: 900px;
  margin: 0 auto;
}

.study-detail {
  padding: 20px;
}

.study-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.study-title h1 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  flex: 1;
}

.study-meta {
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

.study-intro {
  margin: 30px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.study-intro h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.study-intro p {
  font-size: 14px;
  line-height: 1.8;
  color: #666;
}

.study-body {
  margin: 30px 0;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.download-section {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.file-info {
  font-size: 14px;
  color: #666;
}
</style>
