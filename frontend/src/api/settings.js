import { req } from './projects.js'

const CALENDARS_BASE = '/api/v1/business-calendars'
const HOLIDAYS_BASE = '/api/v1/holidays'

export async function listCalendars() {
  return req(CALENDARS_BASE)
}

export async function createCalendar(payload) {
  return req(CALENDARS_BASE, { method: 'POST', body: payload })
}

export async function updateCalendar(id, payload) {
  return req(`${CALENDARS_BASE}/${id}`, { method: 'PUT', body: payload })
}

export async function deleteCalendar(id) {
  return req(`${CALENDARS_BASE}/${id}`, { method: 'DELETE' })
}

export async function listHolidays() {
  return req(HOLIDAYS_BASE)
}

export async function createHoliday(payload) {
  return req(HOLIDAYS_BASE, { method: 'POST', body: payload })
}

export async function updateHoliday(id, payload) {
  return req(`${HOLIDAYS_BASE}/${id}`, { method: 'PUT', body: payload })
}

export async function deleteHoliday(id) {
  return req(`${HOLIDAYS_BASE}/${id}`, { method: 'DELETE' })
}
