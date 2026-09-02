const CURRENT_USER_KEY = 'team_task_management_current_user'

export function getSavedCurrentUser() {
  const currentUserJson = localStorage.getItem(CURRENT_USER_KEY)

  if (!currentUserJson) {
    return null
  }

  try {
    return JSON.parse(currentUserJson)
  } catch {
    localStorage.removeItem(CURRENT_USER_KEY)
    return null
  }
}

export function saveCurrentUser(currentUser) {
  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(currentUser))
}

export function clearCurrentUser() {
  localStorage.removeItem(CURRENT_USER_KEY)
}
