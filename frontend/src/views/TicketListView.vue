<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  listByProject,
  getSummary
} from '../api/tickets.js'

export default {
  setup() {
    const router = useRouter()
    const activeFilter = ref('all')
    const tickets = ref([])
    const summary = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const currentPage = ref(0)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const filterKey = ref('')

    const filterStatusMap = {
      all: undefined,
      registered: 'REGISTERED',
      received: 'RECEIVED',
      processing: 'PROCESSING',
      delayed: 'DELAYED',
      'comp-req': 'COMPLETION_REQUESTED',
      completed: 'COMPLETED'
    }

    const priorityMap = {
      'LOW': 'LOW',
      'MEDIUM': 'MEDIUM',
      'HIGH': 'HIGH',
      'URGENT': 'URGENT'
    }

    const statusMap = {
      'REGISTERED': 'registered',
      'RECEIVED': 'received',
      'PROCESSING': 'processing',
      'DELAYED': 'delayed',
      'COMPLETION_REQUESTED': 'comp-req',
      'APPROVED': 'approved',
      'COMPLETED': 'completed'
    }

    const statusLabelMap = {
      'REGISTERED': '등록됨',
      'RECEIVED': '접수됨',
      'PROCESSING': '처리 중',
      'DELAYED': '지체됨',
      'COMPLETION_REQUESTED': '완료 요청',
      'APPROVED': '승인됨',
      'COMPLETED': '완료'
    }

    const priorityLabelMap = {
      'LOW': 'LOW',
      'MEDIUM': 'MEDIUM',
      'HIGH': 'HIGH',
      'URGENT': 'URGENT'
    }

    const filters = computed(() => {
      const counts = summary.value?.byStatusCounts || {}
      return [
        { key: 'all', label: '전체', count: totalElements.value, dot: 'var(--gray-400)' },
        { key: 'registered', label: '등록됨', count: counts.REGISTERED || 0, dot: 'var(--gray-400)' },
        { key: 'received', label: '접수됨', count: counts.RECEIVED || 0, dot: 'var(--blue)' },
        { key: 'processing', label: '처리 중', count: counts.PROCESSING || 0, dot: 'var(--amber)' },
        { key: 'delayed', label: '지체됨', count: counts.DELAYED || 0, dot: 'var(--red)' },
        { key: 'comp-req', label: '완료 요청', count: counts.COMPLETION_REQUESTED || 0, dot: 'var(--purple)' },
        { key: 'completed', label: '완료', count: counts.COMPLETED || 0, dot: 'var(--green)' },
      ]
    })

    const displayTickets = computed(() => {
      return tickets.value.map(t => {
        const status = statusMap[t.status] || t.status
        const statusLabel = statusLabelMap[t.status] || t.status
        const priorityLabel = priorityLabelMap[t.priority] || t.priority
        const urgency = t.priority === 'URGENT'
        const parts = (t.assignee || '미정').split(' ')
        const init = parts[parts.length - 1] ? parts[parts.length - 1][0] : '미'
        const deadline = t.deadline ? t.deadline.split('T')[0].replace(/-/g, '/') : ''
        const regDate = t.registeredAt ? t.registeredAt.slice(5, 10).replace('-', '/') : ''

        return {
          id: t.id,
          title: t.title,
          status: status,
          statusLabel: statusLabel,
          priority: t.priority.toLowerCase?.() || t.priority,
          priorityLabel: priorityLabel,
          company: t.customerCompanyName || '-',
          assignee: t.assigneeName || '미정',
          assigneeInit: init,
          deadline: deadline,
          urgent: urgency,
          regDate: regDate
        }
      })
    })

    const subtitle = computed(() => {
      return `총 ${totalElements.value}개 티켓`
    })

    async function loadTickets() {
      try {
        loading.value = true
        error.value = null
        const status = filterStatusMap[activeFilter.value]
        const response = await listByProject({
          page: currentPage.value,
          size: 20,
          status: status,
          keyword: filterKey.value || undefined
        })
        tickets.value = response.content || []
        totalPages.value = response.totalPages || 0
        totalElements.value = response.totalElements || 0
      }
      catch (e) {
        error.value = true
        console.error('Failed to load tickets', e)
      }
      finally {
        loading.value = false
      }
    }

    async function loadSummary() {
      try {
        const result = await getSummary()
        summary.value = result
      }
      catch (e) {
        console.error('Failed to load summary', e)
      }
    }

    function selectFilter(key) {
      activeFilter.value = key
      currentPage.value = 0
      loadTickets()
    }

    function navigateToTicket(id) {
      router.push(`/tickets/${id}`)
    }

    function goToPage(page) {
      currentPage.value = page
      loadTickets()
    }

    onMounted(async () => {
      await loadSummary()
      await loadTickets()
    })

    return {
      activeFilter,
      tickets: displayTickets,
      filters,
      loading,
      error,
      subtitle,
      selectFilter,
      navigateToTicket,
      currentPage,
      totalPages,
      totalElements,
      goToPage
    }
  }
}
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">티켓 목록</div>
        <div class="page-subtitle">{{ subtitle }}</div>
      </div>
      <button class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="16" height="16" stroke-width="2" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        새 티켓 생성
      </button>
    </div>

    <!-- Filter Chips -->
    <div class="filter-bar">
      <button
        v-for="filter in filters"
        :key="filter.key"
        class="filter-chip"
        :class="{ 'filter-chip--active': activeFilter === filter.key }"
        @click="selectFilter(filter.key)"
      >
        <span v-if="filter.key !== 'all'" class="badge-dot" :style="{ background: filter.dot, width: '6px', height: '6px', display: 'inline-block' }"></span>
        {{ filter.label }}
        <span style="color:var(--gray-400)">{{ filter.count }}</span>
      </button>
      <div style="margin-left:auto;display:flex;gap:8px">
        <button class="btn btn-secondary btn-sm">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="13" height="13" stroke-width="2" stroke-linecap="round"><line x1="4" y1="6" x2="20" y2="6"/><line x1="4" y1="12" x2="20" y2="12"/><line x1="4" y1="18" x2="20" y2="18"/></svg>
          필터
        </button>
        <button class="btn btn-secondary btn-sm">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="13" height="13" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9"/></svg>
          정렬
        </button>
      </div>
    </div>

    <!-- Table Card -->
    <div class="card">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>티켓 목록을 불러오는 중입니다</span>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <span>데이터를 불러오지 못했습니다</span>
        <button class="btn btn-secondary btn-sm" @click="loadTickets">다시 시도</button>
      </div>

      <!-- Table -->
      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th style="width:30px"><input type="checkbox" style="accent-color:var(--teal)"></th>
              <th>번호</th>
              <th>제목</th>
              <th>상태</th>
              <th>우선순위</th>
              <th>고객사</th>
              <th>담당자</th>
              <th>마감일</th>
              <th>등록일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="tickets.length === 0">
              <td colspan="9" style="text-align:center;padding:40px;color:var(--gray-400)">
                티켓이 없습니다
              </td>
            </tr>
            <tr v-for="ticket in tickets" :key="ticket.id" @click="navigateToTicket(ticket.id)" style="cursor:pointer">
              <td><input type="checkbox" style="accent-color:var(--teal)"></td>
              <td style="font-family:var(--mono);color:var(--teal);font-weight:700">#{{ ticket.id }}</td>
              <td><span class="truncate" style="display:block">{{ ticket.title }}</span></td>
              <td><span class="badge" :class="ticket.status"><span class="badge-dot"></span>{{ ticket.statusLabel }}</span></td>
              <td><span class="priority-badge" :class="ticket.priority">{{ ticket.priorityLabel }}</span></td>
              <td>{{ ticket.company }}</td>
              <td><span class="flex-center gap-2"><span class="avatar-sm" :style="{ background: ticket.assignee === '미정' ? 'var(--gray-300)' : '' }">{{ ticket.assigneeInit }}</span> {{ ticket.assignee }}</span></td>
              <td :style="ticket.urgent ? 'color:var(--red);font-weight:600' : ''">{{ ticket.deadline }}</td>
              <td class="text-muted">{{ ticket.regDate }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination" v-if="totalPages > 1">
      <button class="pagination-btn" :disabled="currentPage === 0" @click="goToPage(currentPage - 1)">‹</button>
      <button
        v-for="page in totalPages"
        :key="page"
        class="pagination-btn"
        :class="{ 'active': currentPage === page }"
        @click="goToPage(page)"
      >
        {{ page + 1 }}
      </button>
      <button class="pagination-btn next" :disabled="currentPage >= totalPages - 1" @click="goToPage(currentPage + 1)">›</button>
    </div>
  </div>
</template>

<style scoped>
.avatar-sm {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--blue);
  color: white;
  font-size: 10px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  flex-shrink: 0;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: var(--gray-500);
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--gray-200);
  border-top-color: var(--teal);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: var(--red);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 16px;
}

.pagination-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--gray-200);
  background: var(--white);
  border-radius: 6px;
  font-size: 13px;
  color: var(--gray-600);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--anim);
}

.pagination-btn:hover:not(:disabled) {
  border-color: var(--teal);
  color: var(--teal);
}

.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-btn.active {
  background: var(--teal);
  color: white;
  border-color: var(--teal);
}

.pagination-btn.next {
  font-weight: 600;
}
</style>
