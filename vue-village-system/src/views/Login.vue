<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>村务管理系统</h1>
        <p>Village Management System</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="userTel">
          <el-input
            v-model.number="form.userTel"
            placeholder="请输入手机号"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="userPwd">
          <el-input
            v-model="form.userPwd"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="login-footer">
            <span>还没有账号？</span>
            <el-button text type="primary" @click="goToRegister">
              立即注册
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
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user.js'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  userTel: '',
  userPwd: ''
})

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
  userPwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    // 调用登录API
    const result = await login(form.userTel, form.userPwd)

    // 保存用户信息和Token
    userStore.login(result.token, result.userInfo)

    ElMessage.success('登录成功')

    // 根据权限跳转
    if (result.userInfo.userPower === 'TUP_CGM') {
      router.push('/admin')
    } else {
      router.push('/home')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 600;
}

.login-header p {
  font-size: 14px;
  color: #999;
  font-weight: 300;
}

.login-form {
  margin-top: 20px;
}

.login-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.login-footer span {
  margin-right: 8px;
}
</style>
