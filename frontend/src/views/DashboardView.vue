<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboard } from '../api/dashboard.js'

const router = useRouter()
const activeChart = ref('today')
const loading = ref(true)
const error = ref(null)

const stats = ref([
  { label: '전체 티켓', value: '—', change: '', color: 'teal', arrow: true, icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { label: '활성 프로젝트', value: '—', change: '', color: 'blue', arrow: true, icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z' },
  { label: '오늘 접수 티켓', value: '—', change: '', color: 'amber', arrow: false, icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { label: 'SLA 경고', value: '—', change: '처리 필요', color: 'red', arrow: false, icon: 'M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z' },
])

const recentTickets = ref([])
const activities = ref([])

function navigateToTicket(id) {
  router.push(`/tickets/${id}`)
}

onMounted(() => {
  getDashboard()
    .then(data => {
      stats.value[0].value = data.totalTicketCount ?? '—'
      stats.value[1].value = data.activeProjectCount ?? '—'
      stats.value[2].value = data.todayTicketCount ?? '—'
      stats.value[3].value = data.slaWarningCount ?? '—'
      recentTickets.value = (data.recentTickets || []).map(t => ({
        id: t.id,
        title: t.title,
        status: t.status,
        statusLabel: t.statusLabel,
        priority: t.priority,
        priorityLabel: t.priorityLabel,
        company: t.companyName,
        deadline: t.deadline,
        urgent: t.urgent,
      }))
      activities.value = (data.activities || []).map(a => ({
        text: a.text,
        time: a.time,
        color: a.color,
      }))
      loading.value = false
    })
    .catch(err => {
      console.error('Failed to load dashboard:', err)
      error.value = '대시보드 데이터를 불러오지 못했습니다.'
      loading.value = false
    })
})
</script>

<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">안녕하세요,洪길동님 👋</div>
      <div class="page-subtitle">오늘의 업무 상태를 확인하세요</div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div
        v-for="stat in stats"
        :key="stat.label"
        class="stat-card"
        :class="stat.color"
      >
        <div class="stat-label">{{ stat.label }}</div>
        <div class="stat-value">{{ stat.value }}</div>
        <div class="stat-change" :class="{ down: !stat.arrow }">{{ stat.change }}</div>
        <div class="stat-icon">
          <svg fill="currentColor" viewBox="0 0 24 24"><path :d="stat.icon" /></svg>
        </div>
      </div>
    </div>

    <!-- Two Column Layout -->
    <div class="two-col">
      <!-- Recent Tickets Table -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">최근 접속 티켓</span>
          <router-link to="/tickets" class="card-action">전체 보기 →</router-link>
        </div>
        <div class="card-body table-wrap">
          <table>
            <thead>
              <tr>
                <th>티켓 번호</th>
                <th>제목</th>
                <th>상태</th>
                <th>우선순위</th>
                <th>고객사</th>
                <th>마감일</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ticket in recentTickets" :key="ticket.id" @click="navigateToTicket(ticket.id)">
                <td style="font-family:var(--mono);color:var(--teal);font-weight:600">#{{ ticket.id }}</td>
                <td class="truncate">{{ ticket.title }}</td>
                <td><span class="badge" :class="ticket.status"><span class="badge-dot"></span>{{ ticket.statusLabel }}</span></td>
                <td><span class="priority-badge" :class="ticket.priority">{{ ticket.priorityLabel }}</span></td>
                <td>{{ ticket.company }}</td>
                <td :style="ticket.urgent ? 'color:var(--red);font-weight:600' : ''">{{ ticket.deadline }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Right Column -->
      <div>
        <!-- Activity Feed -->
        <div class="card" style="margin-bottom:14px">
          <div class="card-header">
            <span class="card-title">활동 내역</span>
            <span class="card-action">전체 보기 →</span>
          </div>
          <div class="card-body">
            <div v-for="(activity, idx) in activities" :key="idx" class="activity-item">
              <div class="activity-dot" :style="{ background: activity.color }"></div>
              <div class="activity-text">{{ activity.text }}</div>
              <div class="activity-time">{{ activity.time }}</div>
            </div>
          </div>
        </div>

        <!-- SLA Warning Chart -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">SLA 경고 현황</span>
            <div class="chart-toggle">
              <button
                v-for="opt in ['today', 'week', 'month']"
                :key="opt"
                :class="{ active: activeChart === opt }"
                @click="activeChart = opt"
              >
                {{ opt === 'today' ? '오늘' : opt === 'week' ? '주간' : '월간' }}
              </button>
            </div>
          </div>
          <div class="card-body chart-body">
            <div
              v-for="(bar, idx) in [3, 5, 2, 4, 1]"
              :key="idx"
              class="chart-bar"
            >
              <div class="chart-bar-fill" :style="{ height: bar * 20 + '%' }"></div>
              <div class="chart-bar-label">{{ ['mon', 'tue', 'wed', 'thu', 'fri'][idx] }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 32px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; margin-bottom: 4px; }
.page-subtitle { color: var(--gray-6); font-size: 14px; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  padding: 18px;
  border-radius: 12px;
  color: #fff;
  position: relative;
  min-height: 110px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  transform: translate(30px, -30px);
}
.stat-card.teal { background: var(--green); }
.stat-card.blue { background: var(--blue); }
.stat-card.amber { background: var(--amber); }
.stat-card.red { background: var(--red); }
.stat-label { font-size: 13px; font-weight: 500; opacity: 0.9; z-index: 1; }
.stat-value { font-size: 32px; font-weight: 700; z-index: 1; }
.stat-change { font-size: 12px; opacity: 0.85; z-index: 1; }
.stat-change.down { opacity: 0.7; }
.stat-icon { position: absolute; bottom: 18px; right: 18px; opacity: 0.3; z-index: 1; }
.stat-icon svg { width: 32px; height: 32px; }

.two-col {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 20px;
}
.card { border-radius: 12px; background: #fff; border: 1px solid var(--border); overflow: hidden; }
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}
.card-title { font-size: 15px; font-weight: 600; }
.card-action { font-size: 13px; color: var(--blue); text-decoration: none; font-weight: 500; }
.card-body { padding: 16px 20px; }
.table-wrap table { width: 100%; border-collapse: collapse; }
.table-wrap th, .table-wrap td { padding: 10px 12px; text-align: left; font-size: 13px; border-bottom: 1px solid var(--border); }
.table-wrap th { color: var(--gray-6); font-weight: 500; background: var(--gray-0); font-size: 12px; }
.table-wrap tr { cursor: pointer; }
.table-wrap tr:hover { background: var(--blue-1); }
.table-wrap tr:last-child td { border-bottom: none; }

.badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-dot { width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
.badge_received { background: #e3f2fd; color: var(--blue); }
.badge_processing { background: #fff8e1; color: var(--amber); }
.badge_registered { background: #e8f5e9; color: var(--green); }
.badge_delayed { background: #ffebee; color: var(--red); }
.badge_comp-req { background: #f3e5f5; color: #7b1fa2; }
.badge_completed { background: #e0e0e0; color: var(--gray-5); }

.priority-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.priority-high { background: #ffebee; color: var(--red); }
.priority-medium { background: #fff8e1; color: var(--amber); }
.priority-low { background: #e8f5e9; }

.truncate { max-width: 200px; overflow: hidden; }

.chart-toggle { display: flex; gap: 6px; }
.chart-toggle button {
  padding: 4px 10px;
  font-size: 11px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  color: var(--gray-7);
  transition: all 0.15s;
}
.chart-toggle button.active {
  background: var(--blue);
  color: #fff;
  border-color: var(--blue);
}

.chart-body { display: flex; justify-content: space-between; align-items: flex-end; padding: 20px; height: 180px; }
.chart-bar { display: flex; flex-direction: column; align-items: center; flex: 1; }
.chart-bar-fill { width: 32px; border-radius: 6px 6px 0 0; background: var(--blue); transition: height 0.3s; }
.chart-bar-label { font-size: 11px; color: var(--gray-6); margin-top: 6px; }

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
}
.activity-dot { width: 8px; height: 8px; border-radius: 50%; margin-top: 5px; flex-shrink: 0; }
.activity-text { font-size: 13px; flex: 1; }
.activity-time { font-size: 11px; color: var(--gray-6); white-space: nowrap; }
</style>
