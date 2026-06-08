<script>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getById, transition, listComments, createComment, listProcessingPlans } from '../api/tickets'

const STATUS_LABELS = {
  REGISTERED: '등록됨',
  RECEIVED: '접수됨',
  PROCESSING: '처리 중',
  DELAYED: '지체',
  COMPLETION_REQUESTED: '완료 요청',
  APPROVED: '승인',
  COMPLETED: '완료됨',
}

const STATUS_BADGE_CLASS = {
  REGISTERED: '',
  RECEIVED: 'received',
  PROCESSING: 'processing',
  DELAYED: 'delayed',
  COMPLETION_REQUESTED: 'completion-requested',
  APPROVED: 'approved',
  COMPLETED: 'completed',
}

const TRANSITIONS = {
  REGISTERED: ['RECEIVED', 'COMPLETED'],
  RECEIVED: ['PROCESSING', 'COMPLETED'],
  PROCESSING: ['COMPLETION_REQUESTED', 'DELAYED'],
  DELAYED: ['PROCESSING', 'COMPLETION_REQUESTED'],
  COMPLETION_REQUESTED: ['PROCESSING', 'COMPLETED'],
}

const TRANSITION_LABELS = {
  RECEIVED: '접수',
  PROCESSING: '처리 시작',
  COMPLETION_REQUESTED: '완료 요청',
  DELAYED: '지체 등록',
  COMPLETED: '완료',
}

const STAGE_ORDER = ['REGISTERED', 'RECEIVED', 'PROCESSING', 'COMPLETION_REQUESTED', 'COMPLETED']
const STAGE_LABELS = {
  REGISTERED: '등록됨',
  RECEIVED: '접수됨',
  PROCESSING: '처리 중',
  COMPLETION_REQUESTED: '완료 요청',
  COMPLETED: '완료됨',
}

const PRIORITY_BADGE_CLASS = {
  LOW: 'low',
  MEDIUM: 'medium',
  HIGH: 'high',
  URGENT: 'urgent',
}

export default {
  setup() {
    const route = useRoute()
    const id = route.params.id

    const ticket = ref(null)
    const comments = ref([])
    const plans = ref([])
    const loading = ref(true)
    const error = ref(null)
    const chatInput = ref('')
    const sending = ref(false)

    const statusLabel = computed(() => ticket.value ? STATUS_LABELS[ticket.value.status] || ticket.value.status : '')
    const statusClass = computed(() => ticket.value ? STATUS_BADGE_CLASS[ticket.value.status] || '' : '')
    const availableTransitions = computed(() => {
      const statuses = ticket.value ? (TRANSITIONS[ticket.value.status] || []) : []
      return statuses.map((s, i) => ({ nextStatus: s, primary: i === 0 }))
    })
    const attachments = computed(() => {
      const atts = ticket.value?.attachments
      return Array.isArray(atts) ? atts : []
    })
    const formattedComments = computed(() => {
      const COLORS = ['#60a5fa', '#f472b6', '#34d399', '#fbbf24', '#a78bfa']
      return (comments.value || []).map((c, i) => ({
        content: c.content || '',
        createdAt: c.created_at || c.createdAt || '',
        userName: c.author_name || c.creator_name || c.userName || '사용자',
        userNameInitial: (c.author_name || c.creator_name || '사용자')[0] || ' ?',
        isMine: c.isMine || c.role === 'SUPPORT',
        color: COLORS[i % COLORS.length],
      }))
    })
    const timelineItems = computed(() => {
      if (!ticket.value) return []
      const t = ticket.value
      const items = []
      const statusEntries = t.status_history
      const historyArr = Array.isArray(statusEntries) ? statusEntries : (statusEntries?.items || [])
      historyArr.forEach((h) => {
        items.push({ type: 'status', status: h.status || h.nextStatus, label: STATUS_LABELS[h.status || h.nextStatus] || '', desc: h.description || h.desc || h.comment || '', time: h.created_at || h.updated_at || h.time || '' })
      })
      if (t.created_at) {
        items.push({ type: 'registered', status: 'REGISTERED', label: STATUS_LABELS.REGISTERED, desc: t.requester_name || t.requester || '고객' + '이 이슈를 등록했습니다', time: t.created_at })
      }
      items.sort((a, b) => (b.time > a.time ? 1 : -1))
      return items
    })
    const managerName = computed(() => ticket.value?.assignee_name || ticket.value?.support_manager || '')
    const managerInitial = computed(() => managerName.value ? managerName.value[0] : '?')
    const managerTitle = computed(() => ticket.value?.assignee_title || ticket.value?.role || '')
    const managerDept = computed(() => ticket.value?.assignee_dept || ticket.value?.department || '')
    const managerActiveProjects = computed(() => ticket.value?.assignee_active_projects ?? 0)
    const managerProcessingTickets = computed(() => ticket.value?.assignee_processing_tickets ?? 0)
    const slaProgressPct = computed(() => {
      if (!ticket.value?.sla_due_date || !ticket.value?.created_at) return 0
      const created = new Date(ticket.value.created_at).getTime()
      const due = new Date(ticket.value.sla_due_date).getTime()
      const now = Date.now()
      if (now >= due) return 100
      return Math.min(100, Math.round(((now - created) / (due - created)) * 100))
    })
    const slaStatusText = computed(() => {
      if (!ticket.value?.sla_due_date) return ''
      const due = new Date(ticket.value.sla_due_date)
      const now = new Date()
      const diffDays = Math.ceil((due - now) / (1000 * 60 * 60 * 24))
      if (diffDays < 0) return 'D+' + (-diffDays) + ' (지체)'
      if (diffDays === 0) return 'D-0 (지체 임박)'
      const dueHour = due.getHours()
      return 'D-' + diffDays + ' (' + dueHour + '시까지 마감)'
    })
    const slaStatusClass = computed(() => {
      const pct = slaProgressPct.value
      if (pct >= 100) return 'danger'
      if (pct >= 80) return 'warning'
      return ''
    })
    const slaWarningText = computed(() => {
      const pct = slaProgressPct.value
      if (pct >= 100) return 'SLA 초과 — 즉시 처리 필요'
      if (pct >= 80) return 'SLA 마감 임박 — ' + ticket.value?.sla_response_deadline + '까지 응답 필수'
      return ''
    })
    const priorityLabel = computed(() => {
      const p = ticket.value?.priority
      return p ? p.toUpperCase() : 'LOW'
    })
    const formatDate = (d) => {
      if (!d) return ''
      const dt = new Date(d)
      return dt.getFullYear() + '/' + String(dt.getMonth() + 1).padStart(2, '0') + '/' + String(dt.getDate()).padStart(2, '0') + ' ' + String(dt.getHours()).padStart(2, '0') + ':' + String(dt.getMinutes()).padStart(2, '0')
    }
    const formatDateShort = (d) => {
      if (!d) return ''
      const dt = new Date(d)
      return dt.getFullYear() + '/' + String(dt.getMonth() + 1).padStart(2, '0') + '/' + String(dt.getDate()).padStart(2, '0')
    }

    const loadTicket = async () => {
      loading.value = true
      error.value = null
      try {
        ticket.value = await getById(id)
      } catch (e) {
        error.value = e.message || '데이터를 불러오지 못했습니다'
      } finally {
        loading.value = false
      }
    }

    const loadComments = async () => {
      try {
        const data = await listComments(id)
        comments.value = Array.isArray(data) ? data : (data?.content || [])
      } catch (e) {
        comments.value = []
      }
    }

    const loadPlans = async () => {
      try {
        const data = await listProcessingPlans(id)
        plans.value = Array.isArray(data) ? data : (data?.content || [])
      } catch (e) {
        plans.value = []
      }
    }

    const handleTransition = async (newStatus) => {
      if (!confirm(TRANSITION_LABELS[newStatus] + ' 상태로 변경하시겠습니까?')) return
      try {
        await transition(id, newStatus)
        await loadTicket()
      } catch (e) {
        alert('상태 변경 실패: ' + (e.message || '알 수 없는 오류'))
      }
    }

    const sendMessage = async () => {
      if (!chatInput.value.trim() || sending.value) return
      sending.value = true
      try {
        await createComment(id, { content: chatInput.value.trim() })
        chatInput.value = ''
        await loadComments()
      } catch (e) {
        alert('메시지 전송 실패: ' + (e.message || '알 수 없는 오류'))
      } finally {
        sending.value = false
      }
    }

    const handleKeydown = (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault()
        sendMessage()
      }
    }

    loadTicket()
    loadComments()
    loadPlans()

    return {
      id, ticket, comments, plans, loading, error, chatInput, sending,
      statusLabel, statusClass, availableTransitions, ribbonClass, currentStageIndex, priorityBadgeClass,
      managerName, managerInitial, managerTitle, managerDept, managerActiveProjects, managerProcessingTickets,
      slaProgressPct, slaStatusText, slaStatusClass, slaWarningText, priorityLabel,
      timelineItems, formattedComments, attachments, formatDate, formatDateShort,
      STATUS_LABELS, STATUS_BADGE_CLASS, STAGE_ORDER, STAGE_LABELS, PRIORITY_BADGE_CLASS,
      TRANSITION_LABELS,
      handleTransition, sendMessage, handleKeydown,
    }
  },
}
</script>

<template>
  <div style="padding: 24px; max-width: 1400px;">
    <!-- Breadcrumb -->
    <div style="display:flex;align-items:center;gap:8px;margin-bottom:16px">
      <router-link to="/tickets" class="btn btn-ghost" style="padding:4px 8px;font-size:13px">
        ← 티켓 목록
      </router-link>
      <span style="color:var(--gray-300)">/</span>
      <span style="font-size:13px;color:var(--gray-500)">티켓 #{{ id }}</span>
    </div>

    <div class="ticket-layout">
      <!-- Left: Main Content -->
      <div>
        <!-- Status Ribbon -->
        <div class="status-ribbon" :class="ribbonClass">
          <div class="ribbon-left">
            <div class="ribbon-stages">
              <template v-for="(stage, idx) in STAGE_ORDER" :key="stage">
                <div class="ribbon-stage" :class="{ done: idx < currentStageIndex, current: idx === currentStageIndex }">
                  <span class="stage-dot"></span>{{ STAGE_LABELS[stage] }}
                </div>
                <span v-if="idx < STAGE_ORDER.length - 1" class="ribbon-arrow">›</span>
              </template>
            </div>
          </div>
          <div class="ribbon-timestamp">{{ ticket.updated_at ?? ticket.created_at }} 진입</div>
        </div>

        <!-- Ticket Body Card -->
        <div class="detail-card">
          <!-- Ticket Info Section -->
          <div class="detail-section">
            <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:12px">
              <div style="flex:1">
                <div style="font-size:11px;color:var(--teal);font-weight:700;letter-spacing:0.06em;text-transform:uppercase;margin-bottom:6px">#{{ id }} · {{ ticket.issue_type_name ?? '이슈' }}</div>
                <div class="detail-title">{{ ticket.title }}</div>
                <div class="detail-body">
                  {{ ticket.description }}
                </div>
              </div>
              <div style="flex-shrink:0;display:flex;flex-direction:column;align-items:flex-end;gap:6px">
                <span class="badge" :class="statusClass"><span class="badge-dot"></span>{{ statusLabel }}</span>
                <span class="priority-badge" :class="priorityBadgeClass">{{ ticket.priority?.toUpperCase() ?? 'LOW' }}</span>
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
              <button
                v-for="opt in availableTransitions"
                :key="opt.nextStatus"
                class="btn"
                :class="opt.primary ? 'btn-primary' : 'btn-outline'"
                @click="handleTransition(opt)"
              >
                {{ TRANSITION_LABELS[opt.nextStatus] ?? opt.nextStatus }}
              </button>
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
            <div v-for="(plan, idx) in plans" :key="plan.id ?? idx" style="background:var(--gray-50);border:1px solid var(--gray-200);border-radius:var(--radius);padding:14px;margin-bottom:8px">
              <div style="font-size:13px;font-weight:600;color:var(--gray-800);margin-bottom:8px">{{ plan.title }}</div>
              <div style="font-size:12px;color:var(--gray-600);line-height:1.8">{{ plan.content }}</div>
              <div style="margin-top:10px;font-size:11px;color:var(--gray-400)">작성: {{ plan.author_name ?? plan.creator_name }} · {{ plan.created_at }}</div>
            </div>
            <div v-if="!plans.length && !loading" style="font-size:13px;color:var(--gray-400);text-align:center;padding:16px">등록된 처리 계획이 없습니다</div>
          </div>

          <!-- History Timeline -->
          <div class="detail-section">
            <div class="section-label">이력 타임라인</div>
            <div class="timeline">
              <div v-for="(item, i) in timelineItems" :key="i" class="tl-item">
                <div class="tl-dot" :class="item.type">{{ STATUS_LABELS[item.status] ?? item.status }}</div>
                <div class="tl-content">
                  <div class="tl-title">{{ item.title ?? item.label }}</div>
                  <div class="tl-desc">{{ item.desc }}</div>
                  <div class="tl-time">{{ formatDate(item.time) }}</div>
                </div>
              </div>
              <div v-if="!timelineItems.length && !loading" style="font-size:13px;color:var(--gray-400);text-align:center;padding:16px">이력이 없습니다</div>
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
            <div v-for="(c, i) in comments" :key="i" class="msg" :class="{ mine: c.isMine }">
              <div v-if="!c.isMine" class="msg-avatar" :style="{ background: c.color }">{{ c.userNameInitial }}</div>
              <div class="msg-body">
                <div class="msg-name">{{ c.userName }}</div>
                <div class="msg-bubble">{{ c.content }}</div>
                <div class="msg-time">{{ c.createdAt }}</div>
              </div>
              <div v-if="c.isMine" class="msg-avatar" :style="{ background: c.color }">{{ c.userNameInitial }}</div>
            </div>
          </div>
          <div class="chat-input-area">
            <div class="chat-input-box">
              <textarea
                v-model="chatInput"
                @keydown="handleKeydown"
                placeholder="메시지를 입력하세요..."
                rows="1"
              ></textarea>
              <button class="chat-send-btn" @click="sendMessage" :disabled="sending">
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
                <span>#{{ ticket.ticket_no ?? id }}</span>
              </div>
              <div class="info-item">
                <label>유형</label>
                <span>{{ ticket.issue_type_name ?? '이슈' }}</span>
              </div>
              <div class="info-item">
                <label>고객사</label>
                <router-link to="/companies">{{ ticket.company_name ?? ticket.company_id }}</router-link>
              </div>
              <div class="info-item">
                <label>우선순위</label>
                <span class="priority-badge" :class="priorityBadgeClass">{{ priorityLabel }}</span>
              </div>
              <div class="info-item">
                <label>등록일</label>
                <span>{{ formatDateShort(ticket.created_at) }}</span>
              </div>
              <div class="info-item">
                <label>마감일</label>
                <span style="color:var(--red);font-weight:600">{{ formatDateShort(ticket.due_date) ?? (formatDateShort(ticket.sla_due_date) ?? '-') }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Support Manager -->
        <div class="info-card">
          <div class="info-card-header">담당SupportManager</div>
          <div style="padding:16px">
            <div style="display:flex;align-items:center;gap:12px;margin-bottom:12px">
              <div style="width:40px;height:40px;border-radius:50%;background:linear-gradient(135deg,var(--blue),#818cf8);display:flex;align-items:center;justify-content:center;color:white;font-size:13px;font-weight:700">{{ managerInitial }}</div>
              <div>
                <div style="font-size:13px;font-weight:600;color:var(--gray-900)">{{ managerName }}</div>
                <div style="font-size:11px;color:var(--gray-500)">{{ managerTitle }}</div>
              </div>
            </div>
            <div class="info-item" style="margin-bottom:6px">
              <label>부서</label>
              <span>{{ managerDept }}</span>
            </div>
            <div class="info-item" style="margin-bottom:6px">
              <label>담당 프로젝트</label>
              <span>{{ managerActiveProjects }}</span>
            </div>
            <div class="info-item">
              <label>처리 중인 티켓</label>
              <span style="color:var(--red);font-weight:600">{{ managerProcessingTickets }}</span>
            </div>
          </div>
        </div>

        <!-- SLA Status -->
        <div class="info-card">
          <div class="info-card-header">SLA 진행 상황</div>
          <div style="padding:16px">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:10px">
              <span style="font-size:13px;font-weight:700" :class="slaStatusClass">{{ slaStatusText }}</span>
              <span style="font-size:12px;color:var(--gray-500)">{{ slaProgressPct }}% 소요</span>
            </div>
            <div style="height:6px;background:var(--gray-100);border-radius:3px;overflow:hidden;margin-bottom:8px">
              <div style="height:100%" :style="{ width: slaProgressPct + '%', background: slaStatusClass === 'text-green' ? 'var(--green)' : slaStatusClass === 'text-amber' ? 'linear-gradient(90deg,var(--amber),var(--red))' : 'var(--red)' }" :class="'rounded'"></div>
            </div>
            <div style="font-size:10px;color:var(--gray-400);margin-bottom:12px">
              등록일: {{ formatDateShort(ticket.created_at) }} · {{ priorityLabel }} SLA · 마감: {{ formatDateShort(ticket.sla_due_date) ?? '-' }}
            </div>
            <div v-if="slaWarningText" style="display:flex;align-items:center;gap:6px;padding:8px 10px;background:var(--amber-light);border-radius:var(--radius);border:1px solid #fde68a">
              <svg width="14" height="14" fill="none" stroke="var(--amber)" viewBox="0 0 24 24" stroke-width="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                <line x1="12" y1="9" x2="12" y2="13"/>
                <line x1="12" y1="17" x2="12.01" y2="17"/>
              </svg>
              <span style="font-size:11px;color:#92400e;font-weight:500">{{ slaWarningText }}</span>
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
