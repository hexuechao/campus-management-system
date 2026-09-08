<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { createTask, getAllTasks } from '../api/task'
import { getUserList } from '../api/user'
import { clearCurrentUser } from '../utils/currentUser'
import { clearToken } from '../utils/token'

const router = useRouter()
const tasks = ref([])
const assignableUsers = ref([])
const usernameById = ref({})
const loading = ref(false)
const errorMessage = ref('')
const dialogVisible = ref(false)
const submitting = ref(false)
const taskFormRef = ref(null)

const taskForm = reactive({
  title: '',
  description: '',
  assigneeUsername: '',
  priority: 'MEDIUM',
  deadline: '',
})

const taskRules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  assigneeUsername: [{ required: true, message: '请选择负责人', trigger: 'change' }],
}

const statusText = { TODO: '待处理', IN_PROGRESS: '进行中', DONE: '已完成' }
const priorityText = { LOW: '低', MEDIUM: '中', HIGH: '高' }

function getErrorMessage(error, fallback) {
  return error.response?.data?.message || fallback
}

function formatDateTime(value) {
  return value ? value.replace('T', ' ') : '-'
}

function resetTaskForm() {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.assigneeUsername = ''
  taskForm.priority = 'MEDIUM'
  taskForm.deadline = ''
  taskFormRef.value?.clearValidate()
}

async function loadData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [taskResponse, userResponse] = await Promise.all([getAllTasks(), getUserList()])
    tasks.value = taskResponse.data.data || []

    const users = userResponse.data || []
    assignableUsers.value = users.filter((user) => user.role === 'USER' && user.status === 1)
    usernameById.value = Object.fromEntries(users.map((user) => [user.id, user.username]))
  } catch (error) {
    if (error.response?.status !== 401 && error.response?.status !== 403) {
      errorMessage.value = getErrorMessage(error, '任务列表加载失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  resetTaskForm()
  dialogVisible.value = true
}

async function submitTask() {
  const valid = await taskFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true

  try {
    const response = await createTask({
      title: taskForm.title.trim(),
      description: taskForm.description.trim(),
      assigneeUsername: taskForm.assigneeUsername,
      priority: taskForm.priority,
      deadline: taskForm.deadline || null,
    })

    if (response.data.code !== 200) {
      ElMessage.error(response.data.message || '任务创建失败')
      return
    }

    ElMessage.success(response.data.message || '任务创建成功')
    dialogVisible.value = false
    await loadData()
  } catch (error) {
    if (error.response?.status !== 401 && error.response?.status !== 403) {
      ElMessage.error(getErrorMessage(error, '任务创建失败'))
    }
  } finally {
    submitting.value = false
  }
}

function handleLogout() {
  clearToken()
  clearCurrentUser()
  router.replace('/login')
}

onMounted(loadData)
</script>

<template>
  <main class="task-list-page">
    <div class="page-header">
      <h1>任务管理</h1>
      <div class="header-actions">
        <el-button @click="router.push('/users')">用户管理</el-button>
        <el-button type="primary" @click="openCreateDialog">创建任务</el-button>
        <el-button @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon :closable="false" />

    <el-table v-loading="loading" :data="tasks" border empty-text="暂无任务">
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
      <el-table-column label="负责人" min-width="130">
        <template #default="scope">
          {{ usernameById[scope.row.assigneeId] || `用户 #${scope.row.assigneeId}` }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="scope">{{ statusText[scope.row.status] || scope.row.status }}</template>
      </el-table-column>
      <el-table-column label="优先级" width="100">
        <template #default="scope">{{ priorityText[scope.row.priority] || scope.row.priority }}</template>
      </el-table-column>
      <el-table-column label="截止时间" min-width="175">
        <template #default="scope">{{ formatDateTime(scope.row.deadline) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="175">
        <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="创建任务" width="560px">
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="90px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="负责人" prop="assigneeUsername">
          <el-select v-model="taskForm.assigneeUsername" placeholder="请选择普通用户" filterable>
            <el-option
              v-for="user in assignableUsers"
              :key="user.id"
              :label="user.name ? `${user.name}（${user.username}）` : user.username"
              :value="user.username"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="taskForm.priority">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="请选择截止时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitTask">确定</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<style scoped>
.task-list-page {
  width: min(1100px, calc(100% - 40px));
  margin: 0 auto;
  padding: 40px 0;
  text-align: left;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

h1 { margin: 0; font-size: 28px; }
.header-actions { display: flex; gap: 12px; }
.el-alert { margin-bottom: 20px; }
.el-select { width: 100%; }
</style>
