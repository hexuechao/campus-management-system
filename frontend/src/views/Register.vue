<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '../api/register'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const registerForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
})

function validateConfirmPassword(rule, value, callback) {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }

  if (value !== registerForm.password) {
    callback(new Error('两次密码输入不一致'))
    return
  }

  callback()
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
}

async function submitRegister() {
  const valid = await registerFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await register({
      username: registerForm.username.trim(),
      name: registerForm.name.trim(),
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
    })
    const result = response.data

    if (result.code !== 200) {
      errorMessage.value = result.message || '注册失败'
      return
    }

    ElMessage.success('注册成功，请登录')
    await router.replace('/login')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="register-page">
    <el-card class="register-card">
      <h1>团队任务管理系统</h1>
      <h2>注册账号</h2>

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        show-icon
        :closable="false"
      />

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        @keyup.enter="submitRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>

        <el-button
          class="register-button"
          type="primary"
          :loading="loading"
          @click="submitRegister"
        >
          注册
        </el-button>

        <div class="page-link">
          已有账号？<router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </el-card>
  </main>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f5f7fa;
}

.register-card {
  width: 400px;
}

h1,
h2 {
  text-align: center;
}

h1 {
  margin: 0 0 8px;
  font-size: 28px;
}

h2 {
  margin: 0 0 24px;
  font-size: 20px;
}

.el-alert {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
}

.page-link {
  margin-top: 18px;
  text-align: center;
}
</style>
