<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser } from '../api/user'
import { clearCurrentUser, saveCurrentUser } from '../utils/currentUser'
import { clearToken } from '../utils/token'

const router = useRouter()
const currentUser = ref(null)

const welcomeName = computed(() => {
  return currentUser.value?.name || currentUser.value?.username || ''
})

async function loadCurrentUser() {
  try {
    const response = await getCurrentUser()
    currentUser.value = response.data
    saveCurrentUser(response.data)
  } catch (error) {
    if (error.response?.status !== 401) {
      console.error('获取当前用户失败：', error)
    }
  }
}

function handleLogout() {
  clearToken()
  clearCurrentUser()
  router.replace('/login')
}

onMounted(loadCurrentUser)
</script>

<template>
  <main class="home-page">
    <el-card class="home-card">
      <div class="home-header">
        <h1>团队任务管理系统</h1>
        <el-button @click="handleLogout">退出登录</el-button>
      </div>

      <p class="welcome-text">欢迎{{ welcomeName ? `，${welcomeName}` : '' }}！</p>
      <el-alert title="任务功能将在后续开发" type="info" show-icon :closable="false" />
    </el-card>
  </main>
</template>

<style scoped>
.home-page {
  width: min(800px, calc(100% - 40px));
  margin: 0 auto;
  padding: 40px 0;
  text-align: left;
}

.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

h1 {
  margin: 0;
  font-size: 28px;
}

.welcome-text {
  margin: 32px 0 20px;
  font-size: 18px;
}
</style>
