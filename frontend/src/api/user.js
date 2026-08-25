import axios from 'axios'

export function getUserList() {
  return axios.get('/api/users')
}

export function createUser(user) {
  return axios.post('/api/users', user)
}

export function updateUser(id, user) {
  return axios.put(`/api/users/${id}`, user)
}

export function deleteUser(id) {
  return axios.delete(`/api/users/${id}`)
}
