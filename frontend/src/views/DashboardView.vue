<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeChart = ref('today')

function navigateToTicket(id) {
  router.push(`/tickets/${id}`)
}

const stats = [
  { label: '전체 회사', value: '42', change: '↑ 3 이번 달', color: 'teal', arrow: true, icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
  { label: '활성 프로젝트', value: '18', change: '↑ 2 이번 달', color: 'blue', arrow: true, icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z' },
  { label: '오늘 접수 티켓', value: '12', change: '↓ 2 어제 대비', color: 'amber', arrow: false, icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { label: 'SLA 경고', value: '3', change: '처리 필요', color: 'red', arrow: false, icon: 'M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z' },
]

const recentTickets = [
  { id: 156, title: '로그인 페이지 응답 지연 현상', status: 'received', statusLabel: '접수됨', priority: 'high', priorityLabel: 'HIGH', company: 'ABC 물류', deadline: '오늘', urgent: true },
  { id: 155, title: '대시보드 레이아웃 깨짐 버그', status: 'processing', statusLabel: '처리 중', priority: 'medium', priorityLabel: 'MEDIUM', company: 'XYZ 테크', deadline: '06/02', urgent: false },
  { id: 154, title: 'Feature X 추가 개발 요청', status: 'registered', statusLabel: '등록됨', priority: 'low', priorityLabel: 'LOW', company: 'DEF 커머스', deadline: '06/10', urgent: false },
  { id: 153, title: '서버 CPU 사용률 비정상 급등', status: 'delayed', statusLabel: '지체됨', priority: 'high', priorityLabel: 'HIGH', company: 'GHI 솔루션', deadline: '-2일', urgent: true },
  { id: 152, title: 'OAuth 토큰 갱신 실패 오류', status: 'comp-req', statusLabel: '완료 요청', priority: 'medium', priorityLabel: 'MEDIUM', company: 'JKL 뱅크', deadline: '05/31', urgent: false },
]

const activities = [
  { text: '김지원 님이 티켓 #156 را 처리 중으로 변경함', time: '10분 전', color: 'var(--blue)' },
  { text: '신규 회사 "MNO 데이터" 등록됨', time: '1시간 전', color: 'var(--mint)' },
  { text: '티켓 #150 완료 요청 처리됨', time: '2시간 전', color: 'var(--green-light)' },
  { text: 'SLA 경고: 티켓 #153 기한 2일 경과', time: '3시간 전', color: 'var(--red-light)' },
  { text: '박개발 님이 티켓 #148 완료함', time: '어제', color: 'var(--blue-light)' },
]
</script>

<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">안녕하세요, 홍길동님 👋</div>
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
          <span class="card-title">최근 접수 티켓</span>
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
              <div class="activity-content">
                <div class="activity-text">{{ activity.text }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Chart Card -->
        <div class="card">
          <div class="card-header">
            <span class="card-title">티켓 진행 추이</span>
            <div class="chart-tabs">
              <button
                class="chart-tab"
                :class="{ 'chart-tab--active': activeChart === 'today' }"
                @click="activeChart = 'today'"
              >오늘</button>
              <button
                class="chart-tab"
                :class="{ 'chart-tab--active': activeChart === 'week' }"
                @click="activeChart = 'week'"
              >이번 주</button>
              <button
                class="chart-tab"
                :class="{ 'chart-tab--active': activeChart === 'month' }"
                @click="activeChart = 'month'"
              >이번 달</button>
            </div>
          </div>
          <div class="card-body">
            <div class="chart-container">
              <div class="bar-chart" :class="activeChart">
                <div v-if="activeChart === 'today'" class="chart-bars">
                  <div class="bar-item"><div class="bar-label">등록됨</div><div class="bar-track"><div class="bar-fill" style="width:25%"></div></div><div class="bar-count">2</div></div>
                  <div class="bar-item"><div class="bar-label">접수됨</div><div class="bar-track"><div class="bar-fill" style="width:60%;background:var(--blue)"></div></div><div class="bar-count">5</div></div>
                  <div class="bar-item"><div class="bar-label">처리 중</div><div class="bar-track"><div class="bar-fill" style="width:90%;background:var(--amber)"></div></div><div class="bar-count">7</div></div>
                  <div class="bar-item"><div class="bar-label">완료 요청</div><div class="bar-track"><div class="bar-fill" style="width:30%;background:var(--purple)"></div></div><div class="bar-count">3</div></div>
                  <div class="bar-item"><div class="bar-label">완료됨</div><div class="bar-track"><div class="bar-fill" style="width:75%;background:var(--green)"></div></div><div class="bar-count">6</div></div>
                </div>
                <div v-else-if="activeChart === 'week'" class="chart-bars">
                  <div class="bar-item"><div class="bar-label">월</div><div class="bar-track"><div class="bar-fill" style="width:50%"></div></div><div class="bar-count">12</div></div>
                  <div class="bar-item"><div class="bar-label">화</div><div class="bar-track"><div class="bar-fill" style="width:70%;background:var(--blue)"></div></div><div class="bar-count">18</div></div>
                  <div class="bar-item"><div class="bar-label">수</div><div class="bar-track"><div class="bar-fill" style="width:40%;background:var(--amber)"></div></div><div class="bar-count">10</div></div>
                  <div class="bar-item"><div class="bar-label">목</div><div class="bar-track"><div class="bar-fill" style="width:85%;background:var(--purple)"></div></div><div class="bar-count">22</div></div>
                  <div class="bar-item"><div class="bar-label">금</div><div class="bar-track"><div class="bar-fill" style="width:65%;background:var(--green)"></div></div><div class="bar-count">16</div></div>
                </div>
                <div v-else class="chart-bars">
                  <div class="bar-item"><div class="bar-label">주1</div><div class="bar-track"><div class="bar-fill" style="width:55%"></div></div><div class="bar-count">35</div></div>
                  <div class="bar-item"><div class="bar-label">주2</div><div class="bar-track"><div class="bar-fill" style="width:75%;background:var(--blue)"></div></div><div class="bar-count">48</div></div>
                  <div class="bar-item"><div class="bar-label">주3</div><div class="bar-track"><div class="bar-fill" style="width:45%;background:var(--amber)"></div></div><div class="bar-count">29</div></div>
                  <div class="bar-item"><div class="bar-label">주4</div><div class="bar-track"><div class="bar-fill" style="width:95%;background:var(--purple)"></div></div><div class="bar-count">62</div></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 18px;
}

.stat-card {
  background: var(--white);
  border-radius: var(--radius-lg);
  padding: 18px 20px;
  border: 1px solid var(--gray-200);
  position: relative;
  overflow: hidden;
  transition: all var(--anim);
}

.stat-card:hover {
  box-shadow: var(--shadow);
}

.stat-label {
  font-size: 12px;
  color: var(--gray-500);
  font-weight: 500;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: var(--gray-900);
  letter-spacing: -1px;
  margin-top: 4px;
  font-family: var(--mono);
}

.stat-change {
  font-size: 11px;
  color: var(--gray-400);
  margin-top: 4px;
}

.stat-change.down {
  color: var(--red);
}

.stat-icon {
  position: absolute;
  top: 18px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.15;
}

.stat-card.teal .stat-icon { background: var(--teal); }
.stat-card.blue .stat-icon { background: var(--blue); }
.stat-card.amber .stat-icon { background: var(--amber); }
.stat-card.red .stat-icon { background: var(--red); }

.two-col {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 14px;
}

.chart-tabs {
  display: flex;
  gap: 2px;
  background: var(--gray-100);
  border-radius: 6px;
  padding: 2px;
}

.chart-tab {
  padding: 5px 12px;
  border: none;
  background: none;
  border-radius: 5px;
  font-size: 12px;
  color: var(--gray-500);
  cursor: pointer;
  font-weight: 500;
  transition: all var(--anim);
}

.chart-tab--active {
  background: var(--white);
  color: var(--gray-900);
  box-shadow: 0 1px 2px rgba(0,0,0,0.08);
}

.chart-container {
  padding: 8px 0;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  height: 120px;
  padding: 0 20px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.bar-label {
  font-size: 10px;
  color: var(--gray-400);
  font-weight: 500;
}

.bar-track {
  width: 100%;
  height: 6px;
  background: var(--gray-100);
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: var(--teal);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.bar-count {
  font-size: 12px;
  font-weight: 700;
  color: var(--gray-900);
  font-family: var(--mono);
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .two-col {
    grid-template-columns: 1fr;
  }
}
</style>
