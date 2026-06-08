const BASE = '/api/tickets'
import { req } from './projects.js'

export async function listByProject(params = {}) {
  const { page = 0, size = 20, status, keyword } = params
  const query = new URLSearchParams({ page, size })
  if (status) query.set('status', status)
  if (keyword) query.set('keyword', keyword)
  return req(`${BASE}?${query.toString()}`)
}

export async function getSummary() {
  return req(`${BASE}/summary`)
}

export async function getById(id) {
  return req(`${BASE}/${id}`)
}

export async function create(payload) {
  return req(BASE, { method: 'POST', body: payload })
}

export async function update(id, payload) {
  return req(`${BASE}/${id}`, { method: 'PUT', body: payload })
}

export async function transition(id, newStatus) {
  return req(`${BASE}/${id}/transition?newStatus=${encodeURIComponent(newStatus)}`, { method: 'POST' })
}

export async function listComments(id, { page = 0, pageSize = 50 } = {}) {
  return req(`${BASE}/${id}/comments?page=${page}&pageSize=${pageSize}`)
}

export async function createComment(id, payload) {
  return req(`${BASE}/${id}/comments`, { method: 'POST', body: payload })
}

export async function listProcessingPlans(id, { page = 0, pageSize = 50 } = {}) {
  return req(`${BASE}/${id}/processingPlans?page=${page}&pageSize=${pageSize}`)
}

export async function createProcessingPlan(id, payload) {
  return req(`${BASE}/${id}/processingPlans`, { method: 'POST', body: payload })
}
