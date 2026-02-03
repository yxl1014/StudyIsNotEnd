<template>
  <div class="people-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>家庭信息管理</span>
          <div class="header-actions">
            <el-button :icon="Upload" @click="showImportDialog = true">
              批量导入
            </el-button>
            <el-button type="primary" :icon="Plus" @click="showAddDialog = true">
              添加信息
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名或身份证号"
          :prefix-icon="Search"
          clearable
          style="width: 300px"
          @change="handleSearch"
        />
        <el-button type="primary" @click="exportData" :icon="Download">
          导出数据
        </el-button>
      </div>

      <!-- 家庭信息列表 -->
      <el-table :data="filteredPeople" v-loading="loading" stripe>
        <el-table-column prop="peopleName" label="姓名" width="120" />
        <el-table-column prop="peopleCardId" label="身份证号" width="180" />
        <el-table-column prop="peopleHouseId" label="房屋编号" width="120" />
        <el-table-column label="备注" min-width="200">
          <template #default="{ row }">
            {{ row.peopleCtx || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="录入时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="editPeople(row)">
              编辑
            </el-button>
            <el-button text type="danger" @click="deletePeople(row)">
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
          @current-change="loadPeople"
        />
      </div>
    </el-card>

    <!-- 添加/编辑信息对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingPeople ? '编辑信息' : '添加信息'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="peopleForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="peopleName">
          <el-input v-model="peopleForm.peopleName" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="身份证号" prop="peopleCardId">
          <el-input
            v-model="peopleForm.peopleCardId"
            placeholder="请输入身份证号"
            maxlength="18"
          />
        </el-form-item>

        <el-form-item label="房屋编号" prop="peopleHouseId">
          <el-input v-model="peopleForm.peopleHouseId" placeholder="请输入房屋编号" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="peopleForm.peopleCtx"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingPeople ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showImportDialog" title="批量导入" width="500px">
      <div class="import-content">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <p>1. 请下载Excel模板，按照模板格式填写数据</p>
          <p>2. 支持.xlsx和.xls格式</p>
          <p>3. 单次最多导入1000条数据</p>
        </el-alert>

        <el-button type="primary" @click="downloadTemplate" style="margin-bottom: 20px">
          下载Excel模板
        </el-button>

        <el-upload
          drag
          action="#"
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          @change="handleFileChange"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
        </el-upload>
      </div>

      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleImport" :loading="importing">
          开始导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Upload,
  Download,
  Search,
  UploadFilled
} from '@element-plus/icons-vue'
import { getPeopleList, createPeople, updatePeople } from '@/api/people.mock.js'
import { formatTime } from '@/utils/format'

const loading = ref(false)
const submitting = ref(false)
const importing = ref(false)
const people = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showAddDialog = ref(false)
const showImportDialog = ref(false)
const editingPeople = ref(null)
const formRef = ref()

const peopleForm = reactive({
  peopleName: '',
  peopleCardId: '',
  peopleHouseId: '',
  peopleCtx: ''
})

const rules = {
  peopleName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  peopleCardId: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { len: 18, message: '身份证号必须是18位', trigger: 'blur' }
  ],
  peopleHouseId: [
    { required: true, message: '请输入房屋编号', trigger: 'blur' }
  ]
}

const filteredPeople = computed(() => {
  if (!searchKeyword.value) {
    return people.value
  }
  return people.value.filter(p =>
    p.peopleName.includes(searchKeyword.value) ||
    p.peopleCardId.includes(searchKeyword.value)
  )
})

const handleSearch = () => {
  page.value = 1
}

const editPeople = (person) => {
  editingPeople.value = person
  Object.assign(peopleForm, {
    peopleName: person.peopleName,
    peopleCardId: person.peopleCardId,
    peopleHouseId: person.peopleHouseId,
    peopleCtx: person.peopleCtx || ''
  })
  showAddDialog.value = true
}

const deletePeople = async (person) => {
  try {
    await ElMessageBox.confirm('确定要删除这条信息吗？', '提示', {
      type: 'warning'
    })

    await updatePeople(person, true)
    ElMessage.success('删除成功')
    loadPeople()
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

    const peopleInfo = {
      peopleName: peopleForm.peopleName,
      peopleCardId: peopleForm.peopleCardId,
      peopleHouseId: peopleForm.peopleHouseId,
      peopleCtx: peopleForm.peopleCtx,
      createTime: Date.now()
    }

    if (editingPeople.value) {
      await updatePeople(peopleInfo)
      ElMessage.success('更新成功')
    } else {
      await createPeople(peopleInfo)
      ElMessage.success('添加成功')
    }

    showAddDialog.value = false
    resetForm()
    loadPeople()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  editingPeople.value = null
  Object.assign(peopleForm, {
    peopleName: '',
    peopleCardId: '',
    peopleHouseId: '',
    peopleCtx: ''
  })
}

const downloadTemplate = () => {
  ElMessage.info('模板下载功能开发中')
}

const handleFileChange = (file) => {
  console.log('选择文件:', file)
}

const handleImport = async () => {
  try {
    importing.value = true
    // 实现Excel导入逻辑
    ElMessage.success('导入成功')
    showImportDialog.value = false
    loadPeople()
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
  }
}

const exportData = () => {
  ElMessage.info('导出功能开发中')
}

const loadPeople = async () => {
  try {
    loading.value = true
    const list = await getPeopleList(page.value, size.value)
    people.value = list
    total.value = list.length
  } catch (error) {
    ElMessage.error('加载家庭信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPeople()
})
</script>

<style scoped>
.people-manage {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.import-content p {
  margin: 8px 0;
  line-height: 1.6;
}
</style>
