<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeFilter = ref('all')
const filters = [
  { key: 'all', label: '전체', count: 47, dot: 'var(--gray-400)' },
  { key: 'registered', label: '등록됨', count: 8, dot: 'var(--gray-400)' },
  { key: 'received', label: '접수됨', count: 12, dot: 'var(--blue)' },
  { key: 'processing', label: '처리 중', count: 18, dot: 'var(--amber)' },
  { key: 'delayed', label: '지체됨', count: 3, dot: 'var(--red)' },
  { key: 'comp-req', label: '완료 요청', count: 4, dot: 'var(--purple)' },
  { key: 'completed', label: '완료', count: 2, dot: 'var(--green)' },
]

const tickets = [
  { id: 156, title: '로그인 페이지 응답 지연 현상', status: 'received', statusLabel: '접수됨', priority: 'high', priorityLabel: 'HIGH', company: 'ABC 물류', assignee: '김지원', assigneeInit: '김', deadline: '2026/05/30', urgent: true, regDate: '05/28' },
  { id: 155, title: '대시보드 레이아웃 깨짐 버그', status: 'processing', statusLabel: '처리 중', priority: 'medium', priorityLabel: 'MEDIUM', company: 'XYZ 테크', assignee: '박개발', assigneeInit: '박', deadline: '2026/06/02', urgent: false, regDate: '05/27' },
  { id: 154, title: 'Feature X 추가 개발 요청', status: 'registered', statusLabel: '등록됨', priority: 'low', priorityLabel: 'LOW', company: 'DEF 커머스', assignee: '미정', assigneeInit: '미', deadline: '2026/06/10', urgent: false, regDate: '05/26' },
  { id: 153, title: '서버 CPU 사용률 비정상 급등', status: 'delayed', statusLabel: '지체됨', priority: 'high', priorityLabel: 'HIGH', company: 'GHI 솔루션', assignee: '김지원', assigneeInit: '김', deadline: '2026/05/29', urgent: true, regDate: '05/25' },
  { id: 152, title: 'OAuth 토큰 갱신 실패 오류', status: 'comp-req', statusLabel: '완료 요청', priority: 'medium', priorityLabel: 'MEDIUM', company: 'JKL 뱅크', assignee: '최PM', assigneeInit: '최', deadline: '2026/05/31', urgent: false, regDate: '05/24' },
  { id: 151, title: 'DB Connection Pool 고갈 현상', status: 'processing', statusLabel: '처리 중', priority: 'high', priorityLabel: 'HIGH', company: 'ABC 물류', assignee: '박개발', assigneeInit: '박', deadline: '2026/06/01', urgent: true, regDate: '05/23' },
  { id: 150, title: 'UI 컴포넌트 색상 불일치', status: 'completed', statusLabel: '완료', priority: 'low', priorityLabel: 'LOW', company: 'DEF 커머스', assignee: '이디자이너', assigneeInit: '이', deadline: '2026/05/28', urgent: false, regDate: '05/22' },
]

function selectFilter(key) {
  activeFilter.value = key
}

function navigateToTicket(id) {
  router.push(`/tickets/${id}`)
}
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">티켓 목록</div>
        <div class="page-subtitle">총 {{ activeFilter === 'all' ? '47' : '—' }}개 티켓 · 12개 처리 대기</div>
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
      <div class="table-wrap">
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
    <div class="pagination">
      <button class="pagination-btn" disabled>‹</button>
      <button class="pagination-btn active">1</button>
      <button class="pagination-btn">2</button>
      <button class="pagination-btn">3</button>
      <button class="pagination-btn">4</button>
      <button class="pagination-btn next">›</button>
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
