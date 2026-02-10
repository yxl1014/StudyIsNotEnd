<template>
  <div class="study-list-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>学习园地</h2>
      <el-button @click="goToFavorite" :icon="Star">我的收藏</el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索学习资料..."
        :prefix-icon="Search"
        clearable
        @change="handleSearch"
        style="max-width: 300px"
      />
      <el-select
        v-model="categoryFilter"
        placeholder="选择分类"
        clearable
        @change="handleFilterChange"
        style="width: 150px"
      >
        <el-option label="全部" :value="null" />
        <el-option label="政策解读" :value="0" />
        <el-option label="农技推广" :value="1" />
        <el-option label="健康养生" :value="2" />
        <el-option label="法律常识" :value="3" />
      </el-select>
    </div>

    <!-- 学习资料列表 -->
    <div class="study-content">
      <el-row :gutter="20" v-loading="loading">
        <el-col
          :xs="24"
          :sm="12"
          :md="8"
          v-for="study in filteredStudies"
          :key="study.studyId"
        >
          <el-card class="study-card" @click="viewStudy(study.studyId)">
            <div class="study-cover">
              <el-icon :size="60" color="#409EFF"><Reading /></el-icon>
            </div>
            <div class="study-info">
              <div class="study-title">{{ study.studyTitle }}</div>
              <div class="study-desc">{{ study.studyContent?.substring(0, 60) }}...</div>
              <div class="study-meta">
                <el-tag size="small">{{ getStudyTypeText(study.studyType) }}</el-tag>
                <div class="meta-right">
                  <el-icon><View /></el-icon>
                  <span>{{ study.readCount || 0 }}</span>
                </div>
              </div>
              <div class="study-footer">
                <span class="study-time">{{ formatRelativeTime(study.studyCreateTime) }}</span>
                <el-button
                  text
                  :icon="study.isStar ? StarFilled : Star"
                  @click.stop="toggleStar(study)"
                >
                  {{ study.isStar ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="filteredStudies.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无学习资料" />
      </div>

      <!-- 分页 -->
      <div v-if="total > size" class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadStudies"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Star,
  StarFilled,
  Search,
  Reading,
  View
} from '@element-plus/icons-vue'
import { getStudyList, toggleStarStudy } from '@/api/study.js'
import { formatRelativeTime } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const studies = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const searchKeyword = ref('')
const categoryFilter = ref(null) // null 表示全部

// 学习资料类型映射
const studyTypeMap = {
  0: '政策解读',
  1: '农技推广',
  2: '健康养生',
  3: '法律常识'
}

// 获取学习资料类型文本
const getStudyTypeText = (type) => {
  return studyTypeMap[type] || '未知类型'
}

const filteredStudies = computed(() => {
  let result = studies.value

  if (searchKeyword.value) {
    result = result.filter(s =>
      s.studyTitle.includes(searchKeyword.value) ||
      s.studyContent.includes(searchKeyword.value)
    )
  }

  if (categoryFilter.value !== null) {
    result = result.filter(s => s.studyType === categoryFilter.value)
  }

  return result
})

const goBack = () => {
  router.back()
}

const goToFavorite = () => {
  router.push('/study/favorite')
}

const viewStudy = (studyId) => {
  router.push(`/study/${studyId}`)
}

const handleSearch = () => {
  page.value = 1
}

const handleFilterChange = () => {
  page.value = 1
}

const toggleStar = async (study) => {
  try {
    const newStarStatus = !study.isStar
    await toggleStarStudy(study.studyId, newStarStatus)
    study.isStar = newStarStatus
    ElMessage.success(newStarStatus ? '收藏成功' : '取消收藏')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const loadStudies = async () => {
  try {
    loading.value = true
    const list = await getStudyList(page.value, size.value)
    studies.value = list
    total.value = list.length
  } catch (error) {
    ElMessage.error('加载学习资料失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStudies()
})
</script>

<style scoped>
.study-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.header {
  max-width: 1200px;
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
  max-width: 1200px;
  margin: 0 auto 20px;
  display: flex;
  gap: 16px;
}

.study-content {
  max-width: 1200px;
  margin: 0 auto;
}

.study-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
  height: 100%;
}

.study-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.study-cover {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 150px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  margin-bottom: 16px;
}

.study-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.study-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.study-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  height: 40px;
  overflow: hidden;
}

.study-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-right {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.study-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.study-time {
  font-size: 13px;
  color: #999;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.empty-state {
  padding: 60px 0;
}
</style>
