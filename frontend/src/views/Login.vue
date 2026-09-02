<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/login'
import { saveToken } from '../utils/token'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const loginForm = reactive({
  username: '',
  password: '',
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submitLogin() {
  const valid = await loginFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await login({
      username: loginForm.username.trim(),
      password: loginForm.password,
    })
    const result = response.data

    if (result.code !== 200 || !result.data) {
      errorMessage.value = result.message || '登录失败'
      return
    }

    saveToken(result.data)
    await router.replace('/users')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <el-card class="login-card">
      <h1>校园管理系统</h1>

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        show-icon
        :closable="false"
      />

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        @keyup.enter="submitLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-button class="login-button" type="primary" :loading="loading" @click="submitLogin">
          登录
        </el-button>
      </el-form>
    </el-card>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f5f7fa;
}

.login-card {
  width: 400px;
}

h1 {
  margin: 0 0 24px;
  text-align: center;
  font-size: 28px;
}

.el-alert {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
}
</style>
