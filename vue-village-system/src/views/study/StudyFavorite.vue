<template>
  <div class="favorite-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>我的收藏</h2>
      <div></div>
    </div>

    <!-- 收藏列表 -->
    <div class="favorite-content">
      <el-card v-loading="loading">
        <div v-if="favorites.length === 0" class="empty-state">
          <el-empty description="暂无收藏">
            <el-button type="primary" @click="goToStudyList">
              去浏览学习资料
            </el-button>
          </el-empty>
        </div>
        <div v-else class="favorite-list">
          <div
            v-for="item in favorites"
            :key="item.studyId"
            class="favorite-item"
          >
            <div class="item-content" @click="viewStudy(item.studyId)">
              <div class="item-icon">
                <el-icon :size="40" color="#409EFF"><Reading /></el-icon>
              </div>
              <div class="item-info">
                <div class="item-title">{{ item.studyTitle }}</div>
                <div class="item-meta">
                  <el-tag size="small">{{ item.studyType }}</el-tag>
                  <span class="item-time">{{ formatRelativeTime(item.starTime) }}</span>
                </div>
              </div>
            </div>
            <el-button
              text
              type="danger"
              :icon="Delete"
              @click="removeFavorite(item)"
            >
              取消收藏
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Reading, Delete } from '@element-plus/icons-vue'
import { getMyStarList, toggleStarStudy } from '@/api/study.mock.js'
import { formatRelativeTime } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const favorites = ref([])

const goBack = () => {
  router.back()
}

const goToStudyList = () => {
  router.push('/study')
}

const viewStudy = (studyId) => {
  router.push(`/study/${studyId}`)
}

const removeFavorite = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      type: 'warning'
    })

    await toggleStarStudy(item.studyId, false)
    favorites.value = favorites.value.filter(f => f.studyId !== item.studyId)
    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const loadFavorites = async () => {
  try {
    loading.value = true
    const list = await getMyStarList(1, 100)
    favorites.value = list
  } catch (error) {
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorite-container {
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

.favorite-content {
  max-width: 800px;
  margin: 0 auto;
}

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  transition: background 0.2s;
}

.favorite-item:hover {
  background: #f5f7fa;
}

.item-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  cursor: pointer;
}

.item-icon {
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-time {
  font-size: 13px;
  color: #999;
}

.empty-state {
  padding: 60px 0;
}
</style>
