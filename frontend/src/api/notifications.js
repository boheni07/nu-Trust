import { req } from './projects.js'

const BASE = '/api/v1/notifications'

/** GET my notifications (paginated) */
export async function getMyNotifications({ page = 0, pageSize = 20 } = {}) {
  return req(
    `${BASE}/my?page=${page}&pageSize=${pageSize}`
  )
}

/** Mark a notification as read */
export async function markAsRead(notificationId) {
  return req(`${BASE}/${notificationId}/read`, { method: 'PUT' })
}

/** Mark all notifications as read */
export async function markAllAsRead() {
  return req(`${BASE}/read-all`, { method: 'PUT' })
}
