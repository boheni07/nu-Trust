const BASE = '/api/projects'

export function req(url, opts) {
  const init = Object.assign({ method: 'GET', headers: {} }, opts || {})
  if (opts && opts.body != null) {
    init.headers['Content-Type'] = 'application/json'
    init.body = JSON.stringify(opts.body)
  }
  return fetch(url, init).then(r => {
    if (!r.ok) return r.text().then(t => { throw new Error(t || r.status) })
    return r.json()
  })
}

export async function fetchProjects() { return req(BASE) }
export async function fetchProject(id) { return req(`${BASE}/${id}`) }
export async function fetchProjectDetail(id) { return req(`${BASE}/${id}/detail`) }
export async function createProject(payload) { return req(BASE, { method: 'POST', body: payload }) }
export async function updateProject(id, payload) { return req(`${BASE}/${id}`, { method: 'PUT', body: payload }) }

export async function updateProjectStatus(id) {
  return req(`${BASE}/${id}/status`, { method: 'PATCH' })
}

export async function deleteProject(id) { return req(`${BASE}/${id}`, { method: 'DELETE' }) }
export async function assignManager(projectId, managerId, role) {
  return req(`${BASE}/${projectId}/managers`, { method: 'POST', body: { managerId, role } })
}
export async function removeManager(projectId, managerId) {
  return req(`${BASE}/${projectId}/managers/${managerId}`, { method: 'DELETE' })
}
