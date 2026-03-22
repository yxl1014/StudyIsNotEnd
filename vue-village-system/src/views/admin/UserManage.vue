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
            <el-tag v-if="row.userPower === 1" type="success">
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
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewUser(row)">
              查看
            </el-button>
            <el-button text type="primary" @click="editUser(row)">
              编辑
            </el-button>
            <el-button
              text
              :type="row.flagType === 0 ? 'danger' : 'success'"
              @click="toggleUserStatus(row)"
            >
              {{ row.flagType === 0 ? '冻结' : '解冻' }}
            </el-button>
            <el-button text type="danger" @click="deleteUser(row)">
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
          <el-tag v-if="currentUser.userPower === 1" type="success">
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

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑用户" width="500px">
      <el-form
        v-if="editingUser"
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="手机号">
          <el-input v-model="editingUser.userTel" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="editForm.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="角色" prop="userPower">
          <el-radio-group v-model="editForm.userPower">
            <el-radio :label="0">村民</el-radio>
            <el-radio :label="1">村干部</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="重置密码">
          <el-input
            v-model="editForm.userPwd"
            type="password"
            placeholder="不修改请留空"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { updateUserInfo, getUserList } from '@/api/user.js'
import { formatTime } from '@/utils/format'

const loading = ref(false)
const updating = ref(false)
const users = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showDetailDialog = ref(false)
const showEditDialog = ref(false)
const currentUser = ref(null)
const editingUser = ref(null)
const editFormRef = ref()

const editForm = ref({
  userName: '',
  userPower: 0,
  userPwd: ''
})

const editRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  userPower: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

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

const editUser = (user) => {
  editingUser.value = user
  editForm.value = {
    userName: user.userName,
    userPower: user.userPower,
    userPwd: ''
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  try {
    await editFormRef.value.validate()
    updating.value = true

    const updateData = {
      userTel: editingUser.value.userTel,
      userName: editForm.value.userName,
      userPower: editForm.value.userPower,
      flagType: editingUser.value.flagType,
      userCreateTime: editingUser.value.userCreateTime
    }

    // 如果填写了密码，则更新密码
    if (editForm.value.userPwd) {
      updateData.userPwd = editForm.value.userPwd
    }

    await updateUserInfo(updateData)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '更新失败')
    }
  } finally {
    updating.value = false
  }
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
      userName: user.userName,
      userPower: user.userPower,
      flagType: newFlagType,
      userCreateTime: user.userCreateTime
    })

    user.flagType = newFlagType
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.userName}" 吗？此操作不可恢复！`,
      '警告',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }
    )

    await updateUserInfo({
      userTel: user.userTel,
      userName: user.userName,
      userPower: user.userPower,
      flagType: user.flagType,
      userCreateTime: user.userCreateTime
    }, true) // isDel = true

    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const loadUsers = async () => {
  try {
    loading.value = true
    console.log('开始加载用户列表...')
    const list = await getUserList(page.value, size.value)
    console.log('用户列表加载成功，数量:', list.length)
    console.log('用户列表数据:', list)
    users.value = list
    total.value = list.length
  } catch (error) {
    console.error('加载用户列表失败，错误详情:', error)
    console.error('错误堆栈:', error.stack)
    ElMessage.error('加载用户列表失败: ' + (error.message || '未知错误'))
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
