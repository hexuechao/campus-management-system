import request from './request'

export function getAllTasks() {
  return request.get('/api/tasks')
}

export function getMyTasks() {
  return request.get('/api/tasks/my')
}

export function createTask(data) {
  return request.post('/api/tasks', data)
}

export function updateTaskStatus(id, data) {
  return request.patch(`/api/tasks/${id}/status`, data)
}
