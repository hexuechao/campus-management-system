import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearCurrentUser } from '../utils/currentUser'
import { clearToken, getToken } from '../utils/token'

const request = axios.create()
let redirectingToLogin = false

request.interceptors.request.use((config) => {
  const token = getToken()

  const publicUrls = ['/api/login', '/api/register']

  if (token && !publicUrls.includes(config.url)) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

request.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      clearToken()
      clearCurrentUser()

      if (router.currentRoute.value.path !== '/login' && !redirectingToLogin) {
        redirectingToLogin = true

        try {
          await router.replace('/login')
        } finally {
          redirectingToLogin = false
        }
      }
    }

    if (error.response?.status === 403) {
      ElMessage.error('权限不足，无法执行此操作')
    }

    return Promise.reject(error)
  },
)

export default request
