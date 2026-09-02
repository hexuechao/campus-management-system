import axios from 'axios'
import router from '../router'
import { clearToken, getToken } from '../utils/token'

const request = axios.create()
let redirectingToLogin = false

request.interceptors.request.use((config) => {
  const token = getToken()

  if (token && config.url !== '/api/login') {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

request.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      clearToken()

      if (router.currentRoute.value.path !== '/login' && !redirectingToLogin) {
        redirectingToLogin = true

        try {
          await router.replace('/login')
        } finally {
          redirectingToLogin = false
        }
      }
    }

    return Promise.reject(error)
  },
)

export default request
