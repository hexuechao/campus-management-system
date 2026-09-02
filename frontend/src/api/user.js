import request from './request'

export function getUserList(keyword) {
  const params = keyword ? { keyword } : {}
  return request.get('/api/users', { params })
}

export function createUser(user) {
  return request.post('/api/users', user)
}

export function updateUser(id, user) {
  return request.put(`/api/users/${id}`, user)
}

export function deleteUser(id) {
  return request.delete(`/api/users/${id}`)
}

export function getCurrentUser() {
  return request.get('/api/users/me')
}
