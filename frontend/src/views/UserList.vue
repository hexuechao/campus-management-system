<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { createUser, deleteUser, getUserList, updateUser } from '../api/user'
import { clearCurrentUser } from '../utils/currentUser'
import { clearToken } from '../utils/token'

const router = useRouter()
const users = ref([])
const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')
const activeKeyword = ref('')
const dialogVisible = ref(false)
const dialogMode = ref('create')
const submitting = ref(false)
const deletingId = ref(null)

function createEmptyForm() {
  return {
    id: null,
    username: '',
    password: '',
    name: '',
    status: 1,
  }
}

const userForm = ref(createEmptyForm())

async function loadUsers() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getUserList(activeKeyword.value)
    users.value = response.data
  } catch (error) {
    console.error('获取用户列表失败：', error)

    if (error.response?.status === 403) {
      errorMessage.value = '权限不足，无法访问用户管理'
    } else if (error.response?.status !== 401) {
      errorMessage.value = '用户列表加载失败，请确认后端服务已经启动。'
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  activeKeyword.value = keyword.value.trim()
  loadUsers()
}

function handleReset() {
  keyword.value = ''
  activeKeyword.value = ''
  loadUsers()
}

function handleLogout() {
  clearToken()
  clearCurrentUser()
  router.replace('/login')
}

function formatCreateTime(createTime) {
  return createTime ? createTime.replace('T', ' ') : '-'
}

function openCreateDialog() {
  dialogMode.value = 'create'
  userForm.value = createEmptyForm()
  dialogVisible.value = true
}

function openEditDialog(user) {
  dialogMode.value = 'edit'
  userForm.value = {
    id: user.id,
    username: user.username,
    password: '',
    name: user.name ?? '',
    status: user.status,
  }
  dialogVisible.value = true
}

async function submitUserForm() {
  if (!userForm.value.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (dialogMode.value === 'create' && !userForm.value.password) {
    ElMessage.warning('请输入密码')
    return
  }

  submitting.value = true

  try {
    let response

    if (dialogMode.value === 'create') {
      response = await createUser({
        username: userForm.value.username.trim(),
        password: userForm.value.password,
        name: userForm.value.name.trim(),
        status: userForm.value.status,
      })
    } else {
      response = await updateUser(userForm.value.id, {
        username: userForm.value.username.trim(),
        name: userForm.value.name.trim(),
        status: userForm.value.status,
      })
    }

    if (response.data !== true) {
      throw new Error('后端没有成功处理请求')
    }

    ElMessage.success(dialogMode.value === 'create' ? '新增成功' : '修改成功')
    dialogVisible.value = false
    await loadUsers()
  } catch (error) {
    console.error('保存用户失败：', error)
    ElMessage.error(dialogMode.value === 'create' ? '新增失败' : '修改失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${user.username} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  deletingId.value = user.id

  try {
    const response = await deleteUser(user.id)

    if (response.data !== true) {
      throw new Error('后端没有成功删除用户')
    }

    ElMessage.success('删除成功')
    await loadUsers()
  } catch (error) {
    console.error('删除用户失败：', error)
    ElMessage.error('删除失败')
  } finally {
    deletingId.value = null
  }
}

onMounted(loadUsers)
</script>

<template>
  <main class="user-list-page">
    <div class="page-header">
      <h1>用户管理</h1>
      <div class="header-actions">
        <el-button @click="router.push('/tasks')">任务管理</el-button>
        <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
        <el-button @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="请输入用户名或姓名"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      show-icon
      :closable="false"
    />

    <el-table v-loading="loading" :data="users" border>
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column label="角色" width="120">
        <template #default="scope">
          {{
            scope.row.role === 'ADMIN'
              ? '管理员'
              : scope.row.role === 'USER'
                ? '普通用户'
                : '-'
          }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="190">
        <template #default="scope">
          {{ formatCreateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button
            type="danger"
            link
            :loading="deletingId === scope.row.id"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item v-if="dialogMode === 'create'" label="密码">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入学习用假密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitUserForm">
          确定
        </el-button>
      </template>
    </el-dialog>
  </main>
</template>

<style scoped>
.user-list-page {
  width: min(1000px, calc(100% - 40px));
  margin: 0 auto;
  padding: 40px 0;
  text-align: left;
}

h1 {
  margin: 0;
  font-size: 28px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar .el-input {
  width: 280px;
}

.el-alert {
  margin-bottom: 20px;
}
</style>
