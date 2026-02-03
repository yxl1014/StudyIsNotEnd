<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名或手机号"
            :prefix-icon="Search"
            clearable
            style="width: 300px"
            @change="handleSearch"
          />
        </div>
      </template>

      <!-- 用户列表 -->
      <el-table :data="filteredUsers" v-loading="loading" stripe>
        <el-table-column prop="userTel" label="手机号" width="130" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.userPower === 'TUP_CGM'" type="success">
              村干部
            </el-tag>
            <el-tag v-else>村民</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.flagType === 0" type="success">正常</el-tag>
            <el-tag v-else type="danger">已冻结</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.userCreateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewUser(row)">
              查看
            </el-button>
            <el-button
              text
              :type="row.flagType === 0 ? 'danger' : 'success'"
              @click="toggleUserStatus(row)"
            >
              {{ row.flagType === 0 ? '冻结' : '解冻' }}
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
          layout="total, sizes, prev, pager, next"
          @current-change="loadUsers"
          @size-change="loadUsers"
        />
      </div>
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="用户详情" width="500px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="手机号">
          {{ currentUser.userTel }}
        </el-descriptions-item>
        <el-descriptions-item label="用户名">
          {{ currentUser.userName }}
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="currentUser.userPower === 'TUP_CGM'" type="success">
            村干部
          </el-tag>
          <el-tag v-else>村民</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentUser.flagType === 0" type="success">
            正常
          </el-tag>
          <el-tag v-else type="danger">已冻结</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatTime(currentUser.userCreateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { updateUserInfo } from '@/api/user.mock.js'
import { formatTime } from '@/utils/format'

const loading = ref(false)
const users = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showDetailDialog = ref(false)
const currentUser = ref(null)

const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }
  return users.value.filter(u =>
    u.userName.includes(searchKeyword.value) ||
    String(u.userTel).includes(searchKeyword.value)
  )
})

const handleSearch = () => {
  page.value = 1
}

const viewUser = (user) => {
  currentUser.value = user
  showDetailDialog.value = true
}

const toggleUserStatus = async (user) => {
  const action = user.flagType === 0 ? '冻结' : '解冻'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      type: 'warning'
    })

    const newFlagType = user.flagType === 0 ? 1 : 0

    await updateUserInfo({
      userTel: user.userTel,
      flagType: newFlagType
    })

    user.flagType = newFlagType
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const loadUsers = async () => {
  try {
    loading.value = true
    // 实际应该调用获取用户列表的API
    // 这里模拟数据
    users.value = [
      {
        userTel: 13800138000,
        userName: '张三',
        userPower: 'TUP_CM',
        flagType: 0,
        userCreateTime: Date.now() - 86400000 * 30
      },
      {
        userTel: 13800138001,
        userName: '李四',
        userPower: 'TUP_CM',
        flagType: 0,
        userCreateTime: Date.now() - 86400000 * 20
      },
      {
        userTel: 13800138002,
        userName: '王五',
        userPower: 'TUP_CGM',
        flagType: 0,
        userCreateTime: Date.now() - 86400000 * 10
      }
    ]
    total.value = users.value.length
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage {
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
</style>
