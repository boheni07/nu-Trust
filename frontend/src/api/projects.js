const BASE = '/api/projects'

function req(method, url, body) {
  const opts = { method, headers: {} }
  if (body) {
    opts.headers['Content-Type'] = 'application/json'
    opts.body = JSON.stringify(body)
  }
  return fetch(url, opts).then(r => {
    if (!r.ok) return r.text().then(t => { throw new Error(t || r.status) })
    return r.json()
  })
}

export async function fetchProjects() { return req('GET', BASE) }
export async function fetchProject(id) { return req('GET', `${BASE}/${id}`) }
export async function fetchProjectDetail(id) { return req('GET', `${BASE}/${id}/detail`) }
export async function createProject(payload) { return req('POST', BASE, payload) }
export async function updateProject(id, payload) { return req('PUT', `${BASE}/${id}`, payload) }

export async function updateProjectStatus(id, status) {
  return req('PATCH', `${BASE}/${id}/status`, null)
}

export async function deleteProject(id) { return req('DELETE', `${BASE}/${id}`) }
export async function assignManager(projectId, managerId, role) {
  return req('POST', `${BASE}/${projectId}/managers`, { managerId, role })
}
export async function removeManager(projectId, managerId) {
  return req('DELETE', `${BASE}/${projectId}/managers/${managerId}`)
}
