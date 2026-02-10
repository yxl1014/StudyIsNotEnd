<template>
  <div class="complaint-create-container">
    <!-- 顶部导航 -->
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>提交投诉</h2>
      <div></div>
    </div>

    <!-- 表单内容 -->
    <div class="form-content">
      <el-card>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="投诉类别" prop="category">
            <el-select v-model="form.category" placeholder="请选择投诉类别">
              <el-option label="环境卫生" :value="0" />
              <el-option label="邻里纠纷" :value="1" />
              <el-option label="基础设施损坏" :value="2" />
              <el-option label="干部作风" :value="3" />
            </el-select>
          </el-form-item>

          <el-form-item label="投诉内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="8"
              placeholder="请详细描述您要反映的问题..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="联系方式" prop="contact">
            <el-input
              v-model="form.contact"
              placeholder="请输入您的联系方式（选填）"
            />
          </el-form-item>

          <el-form-item label="上传图片">
            <el-upload
              v-model:file-list="fileList"
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              accept="image/*"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">
                  上传1张图片，不超过5MB
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              提交投诉
            </el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 温馨提示 -->
      <el-card class="tips-card">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>温馨提示</span>
          </div>
        </template>
        <ul class="tips-list">
          <li>请如实填写投诉内容，我们会在7个工作日内响应</li>
          <li>您可以上传1张相关图片作为证据</li>
          <li>提交后会生成唯一投诉编号，可凭编号查询处理进度</li>
          <li>处理完成后会及时通知您</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, InfoFilled } from '@element-plus/icons-vue'
import { createComplaint } from '@/api/complaint.js'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const submitting = ref(false)
const fileList = ref([])

const form = reactive({
  category: 0, // 默认为环境卫生 (TQT_WSHJ = 0)
  content: '',
  contact: ''
})

const rules = {
  category: [
    { required: true, message: '请选择投诉类别', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入投诉内容', trigger: 'blur' },
    { min: 10, message: '投诉内容至少10个字', trigger: 'blur' }
  ]
}

const goBack = () => {
  router.back()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    // 构建投诉信息
    const complaintInfo = {
      questionType: form.category, // 使用数字类型
      questionCtx: form.content,
      questionWriterTel: userStore.userTel,
      questionWriterName: userStore.userName,
      questionTime: Date.now()
    }

    if (form.contact) {
      complaintInfo.contactInfo = form.contact
    }

    // 处理图片附件：将图片转换为二进制数据
    if (fileList.value.length > 0 && fileList.value[0].raw) {
      try {
        const file = fileList.value[0].raw
        console.log('正在读取图片文件:', file.name, '大小:', file.size, 'bytes')

        // 读取文件为 ArrayBuffer
        const arrayBuffer = await file.arrayBuffer()
        // 转换为 Uint8Array
        const uint8Array = new Uint8Array(arrayBuffer)

        complaintInfo.questPhoto = uint8Array
        console.log('图片已转换为二进制数据，长度:', uint8Array.length, 'bytes')
      } catch (error) {
        console.error('读取图片失败:', error)
        ElMessage.error('读取图片失败')
        submitting.value = false
        return
      }
    }

    console.log('提交投诉信息:', complaintInfo)

    // 提交投诉
    await createComplaint(complaintInfo)

    ElMessage.success('投诉提交成功')

    // 跳转到投诉列表
    setTimeout(() => {
      router.push('/complaint')
    }, 1500)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.complaint-create-container {
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

.form-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.tips-card {
  background: #fff9e6;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e6a23c;
}

.tips-list {
  padding-left: 20px;
  line-height: 2;
  color: #666;
}

.tips-list li {
  list-style: disc;
}
</style>
