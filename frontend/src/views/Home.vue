<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyTasks, updateTaskStatus } from '../api/task'
import { getCurrentUser } from '../api/user'
import { clearCurrentUser, saveCurrentUser } from '../utils/currentUser'
import { clearToken } from '../utils/token'

const router = useRouter()
const currentUser = ref(null)
const tasks = ref([])
const loading = ref(false)
const updatingId = ref(null)
const errorMessage = ref('')

const welcomeName = computed(() => {
  return currentUser.value?.name || currentUser.value?.username || ''
})

const statusText = { TODO: '待处理', IN_PROGRESS: '进行中', DONE: '已完成' }
const priorityText = { LOW: '低', MEDIUM: '中', HIGH: '高' }

function getErrorMessage(error, fallback) {
  return error.response?.data?.message || fallback
}

function formatDateTime(value) {
  return value ? value.replace('T', ' ') : '-'
}

function statusTagType(status) {
  if (status === 'DONE') return 'success'
  if (status === 'IN_PROGRESS') return 'warning'
  return 'info'
}

function priorityTagType(priority) {
  if (priority === 'HIGH') return 'danger'
  if (priority === 'MEDIUM') return 'warning'
  return 'info'
}

async function loadPage() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [currentUserResponse, taskResponse] = await Promise.all([
      getCurrentUser(),
      getMyTasks(),
    ])

    currentUser.value = currentUserResponse.data
    saveCurrentUser(currentUserResponse.data)
    tasks.value = taskResponse.data.data || []
  } catch (error) {
    if (error.response?.status !== 401 && error.response?.status !== 403) {
      errorMessage.value = getErrorMessage(error, '我的任务加载失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

async function changeTaskStatus(task, taskStatus) {
  updatingId.value = task.id

  try {
    const response = await updateTaskStatus(task.id, { taskStatus })

    if (response.data.code !== 200) {
      ElMessage.error(response.data.message || '任务状态修改失败')
      return
    }

    ElMessage.success(response.data.message || '任务状态修改成功')
    await loadPage()
  } catch (error) {
    if (error.response?.status !== 401 && error.response?.status !== 403) {
      ElMessage.error(getErrorMessage(error, '任务状态修改失败'))
    }
  } finally {
    updatingId.value = null
  }
}

function handleLogout() {
  clearToken()
  clearCurrentUser()
  router.replace('/login')
}

onMounted(loadPage)
</script>

<template>
  <main class="home-page">
    <div class="page-header">
      <div>
        <h1>我的任务</h1>
        <p class="welcome-text">欢迎{{ welcomeName ? `，${welcomeName}` : '' }}！</p>
      </div>
      <el-button @click="handleLogout">退出登录</el-button>
    </div>

    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      show-icon
      :closable="false"
    />

    <div v-loading="loading" class="task-list">
      <el-empty v-if="!loading && tasks.length === 0" description="暂无任务" />

      <el-card v-for="task in tasks" :key="task.id" class="task-card" shadow="hover">
        <div class="task-heading">
          <h2>{{ task.title }}</h2>
          <div class="task-tags">
            <el-tag :type="priorityTagType(task.priority)">
              {{ priorityText[task.priority] || task.priority }}优先级
            </el-tag>
            <el-tag :type="statusTagType(task.status)">
              {{ statusText[task.status] || task.status }}
            </el-tag>
          </div>
        </div>

        <p class="description">{{ task.description || '暂无描述' }}</p>
        <p class="deadline">截止时间：{{ formatDateTime(task.deadline) }}</p>

        <div class="task-action">
          <el-button
            v-if="task.status === 'TODO'"
            type="primary"
            :loading="updatingId === task.id"
            @click="changeTaskStatus(task, 'IN_PROGRESS')"
          >
            开始任务
          </el-button>
          <el-button
            v-else-if="task.status === 'IN_PROGRESS'"
            type="success"
            :loading="updatingId === task.id"
            @click="changeTaskStatus(task, 'DONE')"
          >
            完成任务
          </el-button>
          <span v-else class="finished-text">已完成</span>
        </div>
      </el-card>
    </div>
  </main>
</template>

<style scoped>
.home-page {
  width: min(900px, calc(100% - 40px));
  margin: 0 auto;
  padding: 40px 0;
  text-align: left;
}

.page-header,
.task-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

h1, h2, .welcome-text { margin: 0; }
h1 { font-size: 28px; }
h2 { font-size: 20px; }
.welcome-text { margin-top: 8px; }
.el-alert { margin-top: 20px; }
.task-list { min-height: 180px; margin-top: 24px; }
.task-card + .task-card { margin-top: 16px; }
.task-tags { display: flex; gap: 8px; }
.description { margin-top: 18px; color: #606266; white-space: pre-wrap; }
.deadline { margin-top: 14px; color: #909399; font-size: 14px; }
.task-action { margin-top: 18px; text-align: right; }
.finished-text { color: #67c23a; font-weight: 600; }
</style>
