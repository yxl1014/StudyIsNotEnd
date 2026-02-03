<template>
  <div class="study-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学习资料管理</span>
          <el-button type="primary" :icon="Plus" @click="showUploadDialog = true">
            上传资料
          </el-button>
        </div>
      </template>

      <!-- 资料列表 -->
      <el-table :data="studies" v-loading="loading" stripe>
        <el-table-column prop="studyTitle" label="标题" min-width="200" />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.studyType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isOpen" type="success" size="small">公开</el-tag>
            <el-tag v-else type="info" size="small">私密</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阅读次数" width="100">
          <template #default="{ row }">
            {{ row.readCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="editStudy(row)">
              编辑
            </el-button>
            <el-button text type="danger" @click="deleteStudy(row)">
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
          @current-change="loadStudies"
        />
      </div>
    </el-card>

    <!-- 上传/编辑资料对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      :title="editingStudy ? '编辑资料' : '上传资料'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="studyForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="资料标题" prop="studyTitle">
          <el-input v-model="studyForm.studyTitle" placeholder="请输入资料标题" />
        </el-form-item>

        <el-form-item label="资料分类" prop="studyType">
          <el-select v-model="studyForm.studyType" placeholder="请选择分类">
            <el-option label="政策解读" value="政策解读" />
            <el-option label="农技推广" value="农技推广" />
            <el-option label="健康养生" value="健康养生" />
            <el-option label="法律常识" value="法律常识" />
          </el-select>
        </el-form-item>

        <el-form-item label="资料简介" prop="studyIntro">
          <el-input
            v-model="studyForm.studyIntro"
            type="textarea"
            :rows="3"
            placeholder="请输入资料简介"
          />
        </el-form-item>

        <el-form-item label="资料内容" prop="studyContent">
          <el-input
            v-model="studyForm.studyContent"
            type="textarea"
            :rows="8"
            placeholder="请输入资料内容"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="上传文件">
          <el-upload
            action="#"
            :auto-upload="false"
            :limit="1"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.mp4"
          >
            <el-button :icon="Upload">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持PDF、Word、PPT、视频等格式，单个文件不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="是否公开">
          <el-switch v-model="studyForm.isOpen" />
        </el-form-item>

        <el-form-item label="首页推荐">
          <el-switch v-model="studyForm.isTop" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingStudy ? '保存' : '上传' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { getStudyList, createStudy, updateStudy } from '@/api/study.mock.js'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const studies = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showUploadDialog = ref(false)
const editingStudy = ref(null)
const formRef = ref()

const studyForm = reactive({
  studyTitle: '',
  studyType: '政策解读',
  studyIntro: '',
  studyContent: '',
  isOpen: true,
  isTop: false
})

const rules = {
  studyTitle: [
    { required: true, message: '请输入资料标题', trigger: 'blur' }
  ],
  studyType: [
    { required: true, message: '请选择资料分类', trigger: 'change' }
  ],
  studyContent: [
    { required: true, message: '请输入资料内容', trigger: 'blur' },
    { min: 20, message: '资料内容至少20个字', trigger: 'blur' }
  ]
}

const editStudy = (study) => {
  editingStudy.value = study
  Object.assign(studyForm, {
    studyTitle: study.studyTitle,
    studyType: study.studyType,
    studyIntro: study.studyIntro || '',
    studyContent: study.studyContent,
    isOpen: study.isOpen,
    isTop: study.isTop
  })
  showUploadDialog.value = true
}

const deleteStudy = async (study) => {
  try {
    await ElMessageBox.confirm('确定要删除这个学习资料吗？', '提示', {
      type: 'warning'
    })

    await updateStudy(study, true)
    ElMessage.success('删除成功')
    loadStudies()
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

    const studyInfo = {
      studyTitle: studyForm.studyTitle,
      studyType: studyForm.studyType,
      studyIntro: studyForm.studyIntro,
      studyContent: studyForm.studyContent,
      uploaderTel: userStore.userTel,
      uploaderName: userStore.userName,
      isOpen: studyForm.isOpen,
      isTop: studyForm.isTop,
      createTime: Date.now(),
      readCount: 0
    }

    if (editingStudy.value) {
      studyInfo.studyId = editingStudy.value.studyId
      await updateStudy(studyInfo)
      ElMessage.success('更新成功')
    } else {
      await createStudy(studyInfo)
      ElMessage.success('上传成功')
    }

    showUploadDialog.value = false
    resetForm()
    loadStudies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  editingStudy.value = null
  Object.assign(studyForm, {
    studyTitle: '',
    studyType: '政策解读',
    studyIntro: '',
    studyContent: '',
    isOpen: true,
    isTop: false
  })
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
.study-manage {
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
