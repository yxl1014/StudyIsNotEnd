<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>用户注册</h1>
        <p>Register Account</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="register-form"
        label-width="80px"
      >
        <el-form-item label="手机号" prop="userTel">
          <el-input
            v-model.number="form.userTel"
            placeholder="请输入11位手机号"
            clearable
          />
        </el-form-item>

        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="form.userName"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="userPwd">
          <el-input
            v-model="form.userPwd"
            type="password"
            placeholder="请输入密码（至少6位）"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input
            v-model="form.confirmPwd"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="register-footer">
            <span>已有账号？</span>
            <el-button text type="primary" @click="goToLogin">
              立即登录
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user.js'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  userTel: '',
  userName: '',
  userPwd: '',
  confirmPwd: ''
})

const validateConfirmPwd = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.userPwd) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  userTel: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { type: 'number', message: '手机号必须是数字', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && String(value).length !== 11) {
          callback(new Error('手机号必须是11位'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20个字符', trigger: 'blur' }
  ],
  userPwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    // 调用注册API
    await register({
      userTel: form.userTel,
      userName: form.userName,
      userPwd: form.userPwd
    })

    ElMessage.success('注册成功，请登录')

    // 跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 600;
}

.register-header p {
  font-size: 14px;
  color: #999;
  font-weight: 300;
}

.register-form {
  margin-top: 20px;
}

.register-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.register-footer span {
  margin-right: 8px;
}
</style>
