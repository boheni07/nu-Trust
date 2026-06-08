<script setup>
import { ref } from 'vue'

const chatInput = ref('')

const attachments = [
  { name: 'server_log_20260528.txt', size: '2.4MB', icon: 'M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z M14 2v6h6 M14 2v6h6 M14 8l-2-2 M14 8l2-2' },
  { name: 'screenshot.png', size: '0.8MB', icon: 'M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z M9 22V12h6v10' },
]

const messages = [
  { name: '김갑식 · ABC 물류', init: '김', color: 'var(--blue)', bubble: '안녕하세요. 오늘 오전부터 로그인이 매우 느려졌습니다. 출근 시간대에 특히 심한데, 빨리 해결 부탁드립니다.', mine: false, time: '10:18' },
  { name: '김지원 · (주)엔유비즈', init: '이', color: 'var(--seafoam)', bubble: '네 확인했습니다. DB Connection Pool 로그를 살펴보고 있습니다. 30분 정도 시간 주시면 원인 파악해서 알려드리겠습니다.', mine: true, time: '10:22' },
  { name: '김갑식 · ABC 물류', init: '김', color: 'var(--blue)', bubble: '네 좋습니다. 출근 시간대 100명 이상 동시 접속이 발생하는 환경이라서 급한 상황입니다. 부트스트랩 테스트와 함께 확인 부탁드립니다.', mine: false, time: '10:23' },
]
</script>

<template>
  <div style="padding: 24px; max-width: 1400px;">
    <!-- Breadcrumb -->
    <div style="display:flex;align-items:center;gap:8px;margin-bottom:16px">
      <router-link to="/tickets" class="btn btn-ghost" style="padding:4px 8px;font-size:13px">
        ← 티켓 목록
      </router-link>
      <span style="color:var(--gray-300)">/</span>
      <span style="font-size:13px;color:var(--gray-500)">티켓 #156</span>
    </div>

    <div class="ticket-layout">
      <!-- Left: Main Content -->
      <div>
        <!-- Status Ribbon -->
        <div class="status-ribbon processing">
          <div class="ribbon-left">
            <div class="ribbon-stages">
              <div class="ribbon-stage done">
                <span class="stage-dot"></span>등록됨
              </div>
              <span class="ribbon-arrow">›</span>
              <div class="ribbon-stage done">
                <span class="stage-dot"></span>접수됨
              </div>
              <span class="ribbon-arrow">›</span>
              <div class="ribbon-stage current">
                <span class="stage-dot"></span>처리 중
              </div>
              <span class="ribbon-arrow">›</span>
              <div class="ribbon-stage">
                <span class="stage-dot"></span>완료 요청
              </div>
              <span class="ribbon-arrow">›</span>
              <div class="ribbon-stage">
                <span class="stage-dot"></span>완료됨
              </div>
            </div>
          </div>
          <div class="ribbon-timestamp">2026/05/29 14:30 진입</div>
        </div>

        <!-- Ticket Body Card -->
        <div class="detail-card">
          <!-- Ticket Info Section -->
          <div class="detail-section">
            <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:12px">
              <div style="flex:1">
                <div style="font-size:11px;color:var(--teal);font-weight:700;letter-spacing:0.06em;text-transform:uppercase;margin-bottom:6px">#156 · 서버 장애 유형</div>
                <div class="detail-title">로그인 페이지 응답 지연 현상</div>
                <div class="detail-body">
                  2026년 5월 28일 오전 10시경부터 로그인 페이지의 응답 속도가 현저하게 저하되고 있습니다. 일부 사용자의 경우 30초 이상 대기 후에도 로그인이 완료되지 않아 업무 차질이 발생하고 있는 상황입니다. 특히 출퇴근 시간대(09:00–09:30, 18:00–18:30)에 집중적으로 발생하고 있으며, DB Connection Pool 고갈이 원인으로 추정됩니다.
                </div>
              </div>
              <div style="flex-shrink:0;display:flex;flex-direction:column;align-items:flex-end;gap:6px">
                <span class="badge received"><span class="badge-dot"></span>접수됨</span>
                <span class="priority-badge high">HIGH</span>
              </div>
            </div>

            <!-- Attachments -->
            <div style="margin-top:16px;display:flex;gap:8px">
              <div
                v-for="(att, i) in attachments"
                :key="i"
                style="border:1px solid var(--gray-200);border-radius:8px;padding:8px 12px;display:flex;align-items:center;gap:8px;cursor:pointer;background:var(--gray-50);font-size:12px"
              >
                <svg width="16" height="16" fill="none" stroke="var(--gray-500)" viewBox="0 0 24 24">
                  <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
                <span>{{ att.name }}</span>
                <span class="text-muted">{{ att.size }}</span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="action-row" style="margin-top:16px">
              <button class="btn btn-primary">진행 상황 업데이트</button>
              <button class="btn btn-outline">연장 요청</button>
              <button class="btn btn-outline">완료 요청</button>
              <button class="btn btn-ghost" style="margin-left:auto">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="16" height="16">
                  <circle cx="12" cy="12" r="1" fill="currentColor"/>
                  <circle cx="19" cy="12" r="1" fill="currentColor"/>
                  <circle cx="5" cy="12" r="1" fill="currentColor"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Processing Plan -->
          <div class="detail-section">
            <div class="section-label">처리 계획</div>
            <div style="background:var(--gray-50);border:1px solid var(--gray-200);border-radius:var(--radius);padding:14px">
              <div style="font-size:13px;font-weight:600;color:var(--gray-800);margin-bottom:8px">DB Connection Pool 최적화</div>
              <div style="font-size:12px;color:var(--gray-600);line-height:1.8">
                1. HikariCP maxPoolSize 설정 검토 및 조정 (현재 10 → 25)<br>
                2. 쿼리 실행 계획 분석 및 인덱스 최적화<br>
                3. 로그인 API 응답 캐싱 전략 적용<br>
                4. 부하 테스트 수행 후 결과 보고
              </div>
              <div style="margin-top:10px;font-size:11px;color:var(--gray-400)">작성: 김지원 · 2026/05/29</div>
            </div>
          </div>

          <!-- History Timeline -->
          <div class="detail-section">
            <div class="section-label">이력 타임라인</div>
            <div class="timeline">
              <div class="tl-item">
                <div class="tl-dot processing">🔄</div>
                <div class="tl-content">
                  <div class="tl-title">처리 중으로 상태 변경</div>
                  <div class="tl-desc">김지원이 처리 계획을 작성하고 작업을 시작했습니다</div>
                  <div class="tl-time">2026/05/29 14:30</div>
                </div>
              </div>
              <div class="tl-item">
                <div class="tl-dot received">📥</div>
                <div class="tl-content">
                  <div class="tl-title">접수됨으로 상태 변경</div>
                  <div class="tl-desc">지원팀이 티켓을 검토하고 접수했습니다</div>
                  <div class="tl-time">2026/05/28 11:00</div>
                </div>
              </div>
              <div class="tl-item">
                <div class="tl-dot registered">📋</div>
                <div class="tl-content">
                  <div class="tl-title">티켓 등록됨</div>
                  <div class="tl-desc">ABC 물류 김갑식이 이슈를 접수했습니다</div>
                  <div class="tl-time">2026/05/28 10:15</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Chat Section -->
        <div class="chat-wrap">
          <div class="chat-header">
            <div class="chat-title">💬 실시간 채팅 / 댓글</div>
            <div class="online-dot"></div>
            <span style="font-size:11px;color:var(--green);font-weight:500">연결됨</span>
          </div>
          <div class="chat-messages">
            <div v-for="(msg, i) in messages" :key="i" class="msg" :class="{ mine: msg.mine }">
              <div v-if="!msg.mine" class="msg-avatar" :style="{ background: msg.color }">{{ msg.init }}</div>
              <div class="msg-body">
                <div class="msg-name">{{ msg.name }}</div>
                <div class="msg-bubble">{{ msg.bubble }}</div>
                <div class="msg-time">{{ msg.time }}</div>
              </div>
              <div v-if="msg.mine" class="msg-avatar" :style="{ background: msg.color }">{{ msg.init }}</div>
            </div>
          </div>
          <div class="chat-input-area">
            <div class="chat-input-box">
              <textarea
                v-model="chatInput"
                placeholder="메시지를 입력하세요..."
                rows="1"
              ></textarea>
              <button class="chat-send-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="22" y1="2" x2="11" y2="13"/>
                  <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Sidebar Panel -->
      <div class="right-panel">
        <!-- Ticket Info -->
        <div class="info-card">
          <div class="info-card-header">티켓 정보</div>
          <div style="padding:16px">
            <div class="info-grid">
              <div class="info-item">
                <label>티켓 ID</label>
                <span>#156</span>
              </div>
              <div class="info-item">
                <label>유형</label>
                <span>서버 장애</span>
              </div>
              <div class="info-item">
                <label>고객사</label>
                <router-link to="/companies">ABC 물류(주)</router-link>
              </div>
              <div class="info-item">
                <label>우선순위</label>
                <span class="priority-badge high">HIGH</span>
              </div>
              <div class="info-item">
                <label>등록일</label>
                <span>2026/05/28</span>
              </div>
              <div class="info-item">
                <label>마감일</label>
                <span style="color:var(--red);font-weight:600">2026/05/30</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Support Manager -->
        <div class="info-card">
          <div class="info-card-header">담당SupportManager</div>
          <div style="padding:16px">
            <div style="display:flex;align-items:center;gap:12px;margin-bottom:12px">
              <div style="width:40px;height:40px;border-radius:50%;background:linear-gradient(135deg,var(--blue),#818cf8);display:flex;align-items:center;justify-content:center;color:white;font-size:13px;font-weight:700">김</div>
              <div>
                <div style="font-size:13px;font-weight:600;color:var(--gray-900)">김지원</div>
                <div style="font-size:11px;color:var(--gray-500)">수석 엔지니어</div>
              </div>
            </div>
            <div class="info-item" style="margin-bottom:6px">
              <label>부서</label>
              <span>기술지원팀</span>
            </div>
            <div class="info-item" style="margin-bottom:6px">
              <label>담당 프로젝트</label>
              <span>3</span>
            </div>
            <div class="info-item">
              <label>처리 중인 티켓</label>
              <span style="color:var(--red);font-weight:600">2</span>
            </div>
          </div>
        </div>

        <!-- SLA Status -->
        <div class="info-card">
          <div class="info-card-header">SLA 진행 상황</div>
          <div style="padding:16px">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:10px">
              <span style="font-size:13px;font-weight:700;color:var(--amber)">D-0 (지체 임박)</span>
              <span style="font-size:12px;color:var(--gray-500)">90% 소요</span>
            </div>
            <div style="height:6px;background:var(--gray-100);border-radius:3px;overflow:hidden;margin-bottom:8px">
              <div style="height:100%;width:90%;background:linear-gradient(90deg,var(--amber),var(--red));border-radius:3px"></div>
            </div>
            <div style="font-size:10px;color:var(--gray-400);margin-bottom:12px">
              등록일: 2026/05/28 · HIGH SLA: 2일 · 마감: 2026/05/30
            </div>
            <div style="display:flex;align-items:center;gap:6px;padding:8px 10px;background:var(--amber-light);border-radius:var(--radius);border:1px solid #fde68a">
              <svg width="14" height="14" fill="none" stroke="var(--amber)" viewBox="0 0 24 24" stroke-width="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
              <span style="font-size:11px;color:#92400e;font-weight:500">SLA 마감 임박 — 내일 10시까지 응답 필수</span>
            </div>
          </div>
        </div>

        <!-- Extension History -->
        <div class="info-card">
          <div class="info-card-header">연장 기록</div>
          <div style="padding:20px;text-align:center;color:var(--gray-400)">
            <svg width="36" height="36" fill="none" stroke="var(--gray-300)" viewBox="0 0 24 24" stroke-width="1.5" style="margin:0 auto 8px">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            <div style="font-size:12px;font-weight:500;margin-bottom:2px">연장 기록 없음</div>
            <div style="font-size:11px">현재까지 연장 요청이 없습니다.</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Fine adjustments for inline reference HTML elements */
.detail-card {
  background: white;
  border: 1px solid var(--gray-200);
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
  margin-bottom: 16px;
}
.detail-section {
  padding: 20px;
  border-bottom: 1px solid var(--gray-100);
}
.detail-section:last-child {
  border-bottom: none;
}
.section-label {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--gray-400);
  margin-bottom: 12px;
}
.detail-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--gray-900);
  letter-spacing: -0.3px;
  margin-bottom: 8px;
}
.detail-body {
  font-size: 13px;
  color: var(--gray-600);
  line-height: 1.7;
}
.text-muted {
  color: var(--gray-400);
  font-size: 11px;
}
</style>
