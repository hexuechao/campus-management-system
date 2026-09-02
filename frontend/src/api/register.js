import request from './request'

export function register(registerForm) {
  return request.post('/api/register', registerForm)
}
