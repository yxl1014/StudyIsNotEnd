<template>
  <div class="notice-list-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>公告栏</h2>
      <div></div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterType" @change="handleFilterChange">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">通知</el-radio-button>
        <el-radio-button :label="1">招聘</el-radio-button>
        <el-radio-button :label="2">活动</el-radio-button>
        <el-radio-button :label="3">政策</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 公告列表 -->
    <div class="notice-content">
      <el-card v-loading="loading">
        <div v-if="filteredNotices.length === 0" class="empty-state">
          <el-empty description="暂无公告" />
        </div>
        <div v-else class="notice-items">
          <div
            v-for="notice in filteredNotices"
            :key="notice.noticeId"
            class="notice-item"
            @click="viewNotice(notice.noticeId)"
          >
            <div class="notice-header">
              <div class="notice-title">
                <el-tag v-if="notice.isTop" type="danger" size="small" effect="dark">
                  置顶
                </el-tag>
                <span>{{ notice.noticeTitle }}</span>
              </div>
              <el-icon class="arrow-icon"><ArrowRight /></el-icon>
            </div>
            <div class="notice-preview">
              {{ notice.noticeContext?.substring(0, 100) }}...
            </div>
            <div class="notice-meta">
              <div class="meta-left">
                <el-icon><User /></el-icon>
                <span>{{ notice.writerName }}</span>
              </div>
              <div class="meta-right">
                <el-icon><Clock /></el-icon>
                <span>{{ formatRelativeTime(notice.noticeCreateTime) }}</span>
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
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadNotices"
          @size-change="loadNotices"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, User, Clock } from '@element-plus/icons-vue'
import { getNoticeList } from '@/api/notice.js'
import { formatRelativeTime } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const notices = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const filterType = ref(null) // null 表示全部

const filteredNotices = computed(() => {
  if (filterType.value === null) {
    return notices.value
  }
  return notices.value.filter(notice => notice.noticeType === filterType.value)
})

const goBack = () => {
  router.back()
}

const viewNotice = (noticeId) => {
  router.push(`/notice/${noticeId}`)
}

const handleFilterChange = () => {
  page.value = 1
  loadNotices()
}

const loadNotices = async () => {
  try {
    loading.value = true
    const list = await getNoticeList(page.value, size.value)
    notices.value = list
    total.value = list.length // 实际应该从后端返回总数
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
.notice-list-container {
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

.notice-content {
  max-width: 800px;
  margin: 0 auto;
}

.notice-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.notice-item:hover {
  background: #f5f7fa;
  border-color: #409EFF;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.arrow-icon {
  color: #999;
  transition: transform 0.2s;
}

.notice-item:hover .arrow-icon {
  transform: translateX(4px);
  color: #409EFF;
}

.notice-preview {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.meta-left,
.meta-right {
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
</style>
