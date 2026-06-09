import { req } from './projects.js'

export async function getDashboard() {
  return req('/api/dashboard')
}
