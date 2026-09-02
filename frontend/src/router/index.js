import { createRouter, createWebHistory } from 'vue-router'
import {
  clearCurrentUser,
  getSavedCurrentUser,
  saveCurrentUser,
} from '../utils/currentUser'
import { clearToken, getToken } from '../utils/token'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/register',
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/users',
      component: () => import('../views/UserList.vue'),
      meta: { requiresAuth: true, requiredRole: 'ADMIN' },
    },
  ],
})

router.beforeEach(async (to) => {
  const token = getToken()

  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  if (!token) {
    clearCurrentUser()
    return true
  }

  let currentUser = getSavedCurrentUser()

  if (!currentUser?.role) {
    try {
      const { getCurrentUser } = await import('../api/user')
      const response = await getCurrentUser()
      currentUser = response.data
      saveCurrentUser(currentUser)
    } catch {
      clearToken()
      clearCurrentUser()
      return '/login'
    }
  }

  const userHome = currentUser.role === 'ADMIN' ? '/users' : '/home'

  if (to.path === '/login' || to.path === '/register') {
    return userHome
  }

  if (to.meta.requiredRole && currentUser.role !== to.meta.requiredRole) {
    return '/home'
  }

  return true
})

export default router
