import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/token'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/users',
    },
    {
      path: '/login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/users',
      component: () => import('../views/UserList.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to) => {
  const token = getToken()

  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  if (to.path === '/login' && token) {
    return '/users'
  }
})

export default router
