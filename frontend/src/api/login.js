import request from './request'

export function login(loginForm) {
  return request.post('/api/login', loginForm)
}
