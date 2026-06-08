<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchProjects, createProject, deleteProject } from '@/api/projects'

const router = useRouter()
const projects = ref([])
const loading = ref(true)
const error = ref(null)
const showCreateModal = ref(false)
const form = ref({
  name: '',
  ownerId: '',
  companyId: '',
  customerCompanyId: '',
  startDate: '',
  endDate: '',
  contractDate: '',
  status: 'ACTIVE',
  description: ''
})
const creating = ref(false)

const stats = computed(() => {
  const list = projects.value
  return {
    active: list.filter(p => p.status === 'ACTIVE').length,
    paused: list.filter(p => p.status === 'ON_HOLD').length,
    completed: list.filter(p => p.status === 'COMPLETED').length
  }
})

function dateRange(p) {
  if (!p.startDate && !p.endDate) return '-'
  const s = p.startDate ? formatDate(p.startDate) : '-'
  const e = p.endDate ? formatDate(p.endDate) : '-'
  return `${s} ~ ${e}`
}
function formatDate(d) {
  if (!d) return ''
  return d.replace(/-/g, '/')
}

const statusColors = { ACTIVE: '#0d9488', ON_HOLD: '#f59e0b', COMPLETED: '#10b981', CANCELLED: '#6b7280' }
function projectGradient(p) {
  const c = statusColors[p.status] || '#6b7280'
  return `linear-gradient(135deg, ${c} 0%, ${c}cc 100%)`
}
function projectLetter(p) {
  return p.name?.charAt(0).toUpperCase() || '?'
}
function statusBadgeClass(status) {
  return {
    'active-b': status === 'ACTIVE',
    'inactive-b': status !== 'ACTIVE'
  }
}

async function loadProjects() {
  try {
    loading.value = true
    projects.value = await fetchProjects()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  try {
    creating.value = true
    const payload = { ...form.value }
    if (payload.ownerId) payload.ownerId = Number(payload.ownerId)
    if (payload.companyId) payload.companyId = Number(payload.companyId)
    if (payload.customerCompanyId) {
      payload.customerCompanyId = Number(payload.customerCompanyId)
    } else {
      payload.customerCompanyId = null
    }
    await createProject(payload)
    showCreateModal.value = false
    form.value = {
      name: '', ownerId: '', companyId: '', customerCompanyId: '',
      startDate: '', endDate: '', contractDate: '', status: 'ACTIVE', description: ''
    }
    await loadProjects()
  } catch (e) {
    alert(`프로젝트 등록 실패: ${e.message}`)
  } finally {
    creating.value = false
  }
}

function handleDelete(p) {
  if (!confirm(`"${p.name}" 프로젝트를 삭제하시겠습니까?`)) return
  deleteProject(p.id)
    .then(loadProjects)
    .catch(e => alert(`삭제 실패: ${e.message}`))
}

function handleDetail(p) {
  router.push({ name: 'TicketList', query: { project: p.name } })
}

onMounted(loadProjects)
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">프로젝트 관리</div>
        <div class="page-subtitle">
          활성 {{ stats.active }} · 보류 {{ stats.paused }} · 완료 {{ stats.completed }}
        </div>
      </div>
      <div style="display:flex;gap:8px">
        <button class="btn btn-primary" @click="showCreateModal = true">+ 프로젝트 등록</button>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" style="text-align:center;padding:60px;color:var(--gray-400)">
      로딩 중...
    </div>
    <div v-else-if="error" style="text-align:center;padding:60px;color:var(--red)">
      {{ error }}
    </div>
    <template v-else>
      <!-- Project Cards Grid -->
      <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:14px">
        <div
          v-for="p in projects" :key="p.id"
          class="card"
          style="cursor:pointer;transition:all var(--anim)"
          @mouseenter="e => { e.currentTarget.style.boxShadow = 'var(--shadow-lg)'; e.currentTarget.style.transform = 'translateY(-2px)'; }"
          @mouseleave="e => { e.currentTarget.style.boxShadow = ''; e.currentTarget.style.transform = ''; }"
        >
          <div style="padding:16px 20px;border-bottom:1px solid var(--gray-100)">
            <div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:8px">
              <div
                style="width:36px;height:36px;border-radius:9px;display:flex;align-items:center;justify-content:center;color:white;font-size:13px;font-weight:800"
                :style="{ background: projectGradient(p) }"
              >{{ projectLetter(p) }}</div>
              <span class="badge" :class="statusBadgeClass(p.status)" style="font-size:10px">
                <span class="badge-dot"></span>{{ p.statusLabel || p.status }}
              </span>
            </div>
            <div style="font-size:14px;font-weight:700;color:var(--gray-900);margin-bottom:3px">{{ p.name }}</div>
            <div style="font-size:12px;color:var(--gray-500)">{{ p.companyName || '-' }}</div>
          </div>
          <div style="padding:12px 20px;display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px;border-bottom:1px solid var(--gray-100)">
            <div style="text-align:center">
              <div style="font-size:18px;font-weight:700;color:var(--gray-900)">-</div>
              <div style="font-size:10px;color:var(--gray-500)">활성 티켓</div>
            </div>
            <div style="text-align:center">
              <div style="font-size:18px;font-weight:700;color:var(--gray-900)">{{ p.ownerName || '-' }}</div>
              <div style="font-size:10px;color:var(--gray-500)">책임자</div>
            </div>
            <div style="text-align:center">
              <div style="font-size:18px;font-weight:700;color:var(--gray-900)">-</div>
              <div style="font-size:10px;color:var(--gray-500)">SLA 위험</div>
            </div>
          </div>
          <div style="padding:10px 20px;display:flex;justify-content:space-between;align-items:center">
            <span style="font-size:11px;color:var(--gray-400)">{{ dateRange(p) }}</span>
            <div style="display:flex;gap:8px">
              <span class="text-sm" style="color:var(--teal);font-weight:500" @click.stop="handleDetail(p)">상세 보기 →</span>
              <span class="text-sm" style="color:var(--red);cursor:pointer" @click.stop="handleDelete(p)">&#x2715;</span>
            </div>
          </div>
        </div>

        <!-- New Project Placeholder -->
        <div
          class="card"
          style="cursor:pointer;transition:all var(--anim);display:flex;align-items:center;justify-content:center;min-height:200px"
          @mouseenter="e => { e.currentTarget.style.borderColor = 'var(--teal)'; }"
          @mouseleave="e => { e.currentTarget.style.borderColor = ''; }"
          @click="showCreateModal = true"
        >
          <div style="text-align:center;color:var(--gray-400)">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="32" height="32" stroke-width="1.5" stroke-linecap="round" style="margin:0 auto 8px;display:block">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            <div style="font-size:13px;font-weight:600">새 프로젝트 등록</div>
          </div>
        </div>
      </div>
    </template>

    <!-- Create Modal -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3 style="margin:0;font-size:15px;font-weight:700">새 프로젝트 등록</h3>
          <button class="btn-icon" @click="showCreateModal = false">&times;</button>
        </div>
        <form @submit.prevent="handleCreate">
          <div class="form-group">
            <label>프로젝트명 <span style="color:var(--red)">*</span></label>
            <input v-model="form.name" required class="input" placeholder="예: POSM Web App" />
          </div>
          <div class="form-group">
            <label>책임자 ID <span style="color:var(--red)">*</span></label>
            <input v-model="form.ownerId" type="number" required class="input" placeholder="사용자 ID" />
          </div>
          <div class="form-group">
            <label>회사 ID <span style="color:var(--red)">*</span></label>
            <input v-model="form.companyId" type="number" required class="input" placeholder="회사 ID" />
          </div>
          <div class="form-group">
            <label>고객사 ID</label>
            <input v-model="form.customerCompanyId" type="number" class="input" placeholder="선택사항" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>시작일</label>
              <input v-model="form.startDate" type="date" class="input" />
            </div>
            <div class="form-group">
              <label>종료일</label>
              <input v-model="form.endDate" type="date" class="input" />
            </div>
            <div class="form-group">
              <label>계약일</label>
              <input v-model="form.contractDate" type="date" class="input" />
            </div>
          </div>
          <div class="form-group">
            <label>상태</label>
            <select v-model="form.status" class="input">
              <option value="ACTIVE">활성</option>
              <option value="ON_HOLD">보류</option>
              <option value="COMPLETED">완료</option>
              <option value="CANCELLED">취소</option>
            </select>
          </div>
          <div class="form-group">
            <label>설명</label>
            <textarea v-model="form.description" rows="3" class="input" style="resize:vertical"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="showCreateModal = false">취소</button>
            <button type="submit" class="btn btn-primary" :disabled="creating">
              {{ creating ? '기록 중...' : '등록' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card:hover:not(.placeholder) {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal {
  background: white;
  border-radius: 12px;
  width: 480px;
  max-width: 90vw;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: var(--shadow-xl);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--gray-100);
}

.modal-header .btn-icon {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--gray-400);
  padding: 0;
  line-height: 1;
}

.modal form {
  padding: 20px;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: var(--gray-600);
  margin-bottom: 4px;
}

.form-group .input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--gray-200);
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  transition: border 0.15s;
  box-sizing: border-box;
}

.form-group .input:focus {
  border-color: var(--teal);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
}

.form-row .form-group {
  margin-bottom: 14px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}

.badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 9999px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.badge-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
}

.active-b { background: #e6f7f0; color: #0d9488; }
.inactive-b { background: #f3f4f6; color: #6b7280; }
</style>
