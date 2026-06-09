<script setup>
import { ref, onMounted } from 'vue'
import {
  listCalendars, createCalendar, updateCalendar, deleteCalendar,
  listHolidays, createHoliday, updateHoliday, deleteHoliday,
} from '../api/settings.js'

const activeSection = ref('sla')
const sections = [
  { key: 'sla', label: 'SLA 설정' },
  { key: 'notification', label: '알림 설정' },
  { key: 'ratelimit', label: 'Rate Limit' },
  { key: 'cache', label: '캐시 정책' },
  { key: 'calendar', label: 'Business Calendar' },
  { key: 'holiday', label: '공휴일 관리' },
  { key: 'auditlog', label: '감사 로그' },
]

// --- SLA settings (placeholder) ---
const slaSettings = ref({
  high: 2,
  medium: 5,
  low: 14,
  warning: 1,
})
function handleSave() {}
function handleReset() {
  slaSettings.value = { high: 2, medium: 5, low: 14, warning: 1 }
}

// --- Business Calendar CRUD ---
const calendars = ref([])
const calendarForm = ref({
  calendarName: '',
  colorCode: '#0f766e',
  startTime: '09:00',
  endTime: '18:00',
  breakStart: '12:00',
  breakEnd: '13:00',
  workingDays: 'MON,TUE,WED,THU,FRI',
  isDefault: false,
  status: 'ACTIVE',
})
const editingCalendarId = ref(null)
const calendarMsg = ref('')

async function loadCalendars() {
  try {
    calendars.value = await listCalendars()
  } catch (e) {
    calendarMsg.value = '캘린더 로드 실패: ' + e.message
  }
}

function resetCalendarForm() {
  calendarForm.value = {
    calendarName: '',
    colorCode: '#0f766e',
    startTime: '09:00',
    endTime: '18:00',
    breakStart: '12:00',
    breakEnd: '13:00',
    workingDays: 'MON,TUE,WED,THU,FRI',
    isDefault: false,
    status: 'ACTIVE',
  }
  editingCalendarId.value = null
}

async function saveCalendar() {
  try {
    const fn = editingCalendarId.value ? updateCalendar : createCalendar
    const payload = { ...calendarForm.value }
    // Convert string boolean to actual boolean for isDefault
    if (typeof payload.isDefault === 'string') {
      payload.isDefault = payload.isDefault === 'true'
    }
    await fn(editingCalendarId.value, payload)
    calendarMsg.value = editingCalendarId.value ? '캘린더를 업데이트했습니다.' : '새 캘린더를 추가했습니다.'
    resetCalendarForm()
    await loadCalendars()
    setTimeout(() => { calendarMsg.value = '' }, 2000)
  } catch (e) {
    calendarMsg.value = '저장 실패: ' + e.message
  }
}

function editCalendar(c) {
  calendarForm.value = { ...c }
  editingCalendarId.value = c.id
}

async function removeCalendar(id) {
  if (!confirm('이 캘린더를 삭제하시겠습니까?')) return
  try {
    await deleteCalendar(id)
    calendarMsg.value = '캘린더를 삭제했습니다.'
    await loadCalendars()
    if (editingCalendarId.value === id) resetCalendarForm()
    setTimeout(() => { calendarMsg.value = '' }, 2000)
  } catch (e) {
    calendarMsg.value = '삭제 실패: ' + e.message
  }
}

// --- Holiday CRUD ---
const holidays = ref([])
const holidayForm = ref({
  holidayDate: '',
  holidayName: '',
  holidayType: 'REGULAR',
})
const editingHolidayId = ref(null)
let holidayMsg = ''

async function loadHolidays() {
  try {
    holidays.value = await listHolidays()
  } catch (e) {
    holidayMsg = '공휴일 로드 실패: ' + e.message
  }
}

function resetHolidayForm() {
  holidayForm.value = { holidayDate: '', holidayName: '', holidayType: 'REGULAR' }
  editingHolidayId.value = null
}

async function saveHoliday() {
  try {
    const fn = editingHolidayId.value ? updateHoliday : createHoliday
    await fn(editingHolidayId.value, holidayForm.value)
    holidayMsg = editingHolidayId.value ? '공휴일을 업데이트했습니다.' : '새 공휴일을 추가했습니다.'
    resetHolidayForm()
    await loadHolidays()
    setTimeout(() => { holidayMsg = '' }, 2000)
  } catch (e) {
    holidayMsg = '저장 실패: ' + e.message
  }
}

function editHoliday(h) {
  holidayForm.value = { ...h }
  editingHolidayId.value = h.id
}

async function removeHoliday(id) {
  if (!confirm('이 공휴일을 삭제하시겠습니까?')) return
  try {
    await deleteHoliday(id)
    holidayMsg = '공휴일을 삭제했습니다.'
    await loadHolidays()
    if (editingHolidayId.value === id) resetHolidayForm()
    setTimeout(() => { holidayMsg = '' }, 2000)
  } catch (e) {
    holidayMsg = '삭제 실패: ' + e.message
  }
}

onMounted(() => { loadCalendars(); loadHolidays() })
</script>

<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">시스템 설정</div>
      <div class="page-subtitle">SLA, Rate Limit, 알림, 캐시 정책 등 전체 시스템 구성</div>
    </div>

    <!-- Two Column Layout -->
    <div style="display:grid;grid-template-columns:220px 1fr;gap:20px">
      <!-- Settings Sidebar -->
      <div class="card" style="height:fit-content">
        <div style="padding:8px 0">
          <div style="padding:8px 16px;font-size:11px;font-weight:700;color:var(--gray-400);text-transform:uppercase;letter-spacing:0.06em">설정 항목</div>
          <div
            v-for="section in sections"
            :key="section.key"
            style="padding:9px 16px;font-size:13px;cursor:pointer;transition:all var(--anim)"
            @click="activeSection = section.key"
            :class="activeSection === section.key ? 'is-active-section' : 'is-inactive-section'"
            :style="activeSection === section.key
              ? 'color:var(--teal);font-weight:600;background:var(--mint-light);border-right:2px solid var(--teal)'
              : 'color:var(--gray-600)'"
          >{{ section.label }}</div>
        </div>
      </div>

      <!-- Content Area -->
      <div>
        <!-- SLA 설정 -->
        <div v-if="activeSection === 'sla'" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:4px">SLA 설정</div>
          <div style="font-size:12px;color:var(--gray-500);margin-bottom:20px">우선순위별 SLA 기한 및 경고 시점을 설정합니다</div>
          <div class="form-grid" style="margin-bottom:20px">
            <div class="form-group">
              <label class="form-label">HIGH 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.high">
            </div>
            <div class="form-group">
              <label class="form-label">MEDIUM 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.medium">
            </div>
            <div class="form-group">
              <label class="form-label">LOW 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.low">
            </div>
            <div class="form-group">
              <label class="form-label">SLA 경고 시점 (D-)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.warning">
            </div>
          </div>
          <div style="display:flex;gap:8px">
            <button class="btn btn-primary" @click="handleSave">저장</button>
            <button class="btn btn-secondary" @click="handleReset">초기화</button>
          </div>
        </div>

        <!-- 나머지 섹션 (placeholder) -->
        <div v-else-if="['notification', 'ratelimit', 'cache', 'auditlog'].includes(activeSection)" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:8px">{{ sections.find(s => s.key === activeSection)?.label }}</div>
          <div style="font-size:12px;color:var(--gray-500)">이 기능은 준비 중입니다.</div>
        </div>

        <!-- Business Calendar -->
        <div v-if="activeSection === 'calendar'" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:4px">Business Calendar</div>
          <div style="font-size:12px;color:var(--gray-500);margin-bottom:20px">업체별 업무 시간 캘린더를 관리합니다</div>

          <div v-if="calendarMsg" style="padding:10px;background:#ecfdf5;border-radius:8px;margin-bottom:16px;font-size:13px;color:#065f46">{{ calendarMsg }}</div>

          <!-- Calendar Form -->
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:20px;background:var(--gray-0);padding:16px;border-radius:10px">
            <div class="form-group"><label class="form-label">캘린더 이름</label><input class="form-input" v-model="calendarForm.calendarName" placeholder="예: 본사"></div>
            <div class="form-group">
              <label class="form-label">색상</label>
              <input type="color" v-model="calendarForm.colorCode" style="width:100%;height:38px;border:1px solid var(--border);border-radius:6px;cursor:pointer">
            </div>
            <div class="form-group"><label class="form-label">시작 시간</label><input class="form-input" type="time" v-model="calendarForm.startTime"></div>
            <div class="form-group"><label class="form-label">종료 시간</label><input class="form-input" type="time" v-model="calendarForm.endTime"></div>
            <div class="form-group"><label class="form-label">점심 시작</label><input class="form-input" type="time" v-model="calendarForm.breakStart"></div>
            <div class="form-group"><label class="form-label">점심 완료</label><input class="form-input" type="time" v-model="calendarForm.breakEnd"></div>
            <div class="form-group"><label class="form-label">업무요일</label><input class="form-input" v-model="calendarForm.workingDays" placeholder="MON,TUE,WED,THU,FRI"></div>
            <div class="form-group"><label class="form-label">기본 캘린더</label><input type="checkbox" v-model="calendarForm.isDefault" style="margin-top:8px"></div>
          </div>
          <div style="display:flex;gap:8px;margin-bottom:24px">
            <button class="btn btn-primary" @click="saveCalendar">{{ editingCalendarId ? '수정' : '추가' }}</button>
            <button class="btn btn-secondary" v-if="editingCalendarId" @click="resetCalendarForm">취소</button>
          </div>

          <!-- Calendar List -->
          <table>
            <thead>
              <tr><th>이름</th><th>시간</th><th>요일</th><th>기본</th><th>상태</th><th style="width:80px"></th></tr>
            </thead>
            <tbody>
              <tr v-for="c in calendars" :key="c.id">
                <td style="font-weight:600">{{ c.calendarName }}
                  <span v-if="c.isDefault" style="font-size:11px;color:var(--blue);font-weight:500"> ★ 기본</span>
                </td>
                <td>{{ c.startTime }} - {{ c.endTime }}</td>
                <td>{{ c.workingDays }}</td>
                <td>{{ c.isDefault ? '예' : '아니요' }}</td>
                <td><span class="badge" :class="c.status === 'ACTIVE' ? 'badge_registered' : 'badge_completed'">{{ c.status }}</span></td>
                <td>
                  <button class="btn-link" @click="editCalendar(c)">수정</button>
                  <button class="btn-link" style="color:var(--red)" @click="removeCalendar(c.id)">삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="calendars.length === 0" style="padding:24px;text-align:center;color:var(--gray-500);font-size:13px">등록된 캘린더가 없습니다.</div>
        </div>

        <!-- Holiday Management -->
        <div v-if="activeSection === 'holiday'" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:4px">공휴일 관리</div>
          <div style="font-size:12px;color:var(--gray-500);margin-bottom:20px">업체별 공휴일 및 대체공휴일을 관리합니다</div>

          <div v-if="holidayMsg" style="padding:10px;background:#ecfdf5;border-radius:8px;margin-bottom:16px;font-size:13px;color:#065f46">{{ holidayMsg }}</div>

          <!-- Holiday Form -->
          <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px;margin-bottom:20px;background:var(--gray-0);padding:16px;border-radius:10px">
            <div class="form-group"><label class="form-label">날짜</label><input class="form-input" type="date" v-model="holidayForm.holidayDate"></div>
            <div class="form-group"><label class="form-label">공휴일 이름</label><input class="form-input" v-model="holidayForm.holidayName" placeholder="예: 설날"></div>
            <div class="form-group">
              <label class="form-label">유형</label>
              <select class="form-input" v-model="holidayForm.holidayType">
                <option value="REGULAR">일반공휴일</option>
                <option value="SUBSTITUTE">대체공휴일</option>
                <option value="COMPANY">회사 지정</option>
              </select>
            </div>
          </div>
          <div style="display:flex;gap:8px;margin-bottom:24px">
            <button class="btn btn-primary" @click="saveHoliday">{{ editingHolidayId ? '수정' : '추가' }}</button>
            <button class="btn btn-secondary" v-if="editingHolidayId" @click="resetHolidayForm">취소</button>
          </div>

          <!-- Holiday List -->
          <table>
            <thead>
              <tr><th>날짜</th><th>이름</th><th>유형</th><th style="width:80px"></th></tr>
            </thead>
            <tbody>
              <tr v-for="h in holidays" :key="h.id">
                <td style="font-family:var(--mono)">{{ h.holidayDate }}</td>
                <td>{{ h.holidayName }}</td>
                <td><span class="badge" :class="h.holidayType === 'REGULAR' ? 'badge_registered' : 'badge_processing'">{{ h.holidayType }}</span></td>
                <td>
                  <button class="btn-link" @click="editHoliday(h)">수정</button>
                  <button class="btn-link" style="color:var(--red)" @click="removeHoliday(h.id)">삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="holidays.length === 0" style="padding:24px;text-align:center;color:var(--gray-500);font-size:13px">등록된 공휴일이 없습니다.</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.is-active-section {
  color: var(--teal);
  font-weight: 600;
  background: var(--mint-light);
  border-right: 2px solid var(--teal);
}

.is-inactive-section {
  color: var(--gray-600);
}

.is-inactive-section:hover {
  color: var(--gray-900);
  background: var(--gray-50);
}

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.form-label { display: block; font-size: 12px; font-weight: 600; color: var(--gray-700); margin-bottom: 4px; }
.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 13px;
  transition: border-color var(--anim);
}
.form-input:focus { outline: none; border-color: var(--teal); }
.btn {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all var(--anim);
}
.btn-primary { background: var(--blue); color: #fff; }
.btn-primary:hover { background: var(--blue-dark, #1d4ed8); }
.btn-secondary { background: var(--gray-100); color: var(--gray-700); }
.btn-link {
  background: none; border: none; cursor: pointer;
  font-size: 12px; color: var(--blue); padding: 4px 8px;
}
.table-wrap table { width: 100%; border-collapse: collapse; }
.table-wrap th, .table-wrap td {
  padding: 10px 12px; text-align: left; font-size: 13px;
  border-bottom: 1px solid var(--border);
}
.table-wrap th { color: var(--gray-6); font-weight: 500; background: var(--gray-0); font-size: 12px; }
.badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-dot { width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
.badge_registered { background: #e8f5e9; }
.badge_received { background: #e3f2fd; }
.badge_processing { background: #fff8e1; }
.badge_completed { background: #e0e0e0; }
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 32px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; margin-bottom: 4px; }
.page-subtitle { color: var(--gray-6); font-size: 14px; }
</style>
