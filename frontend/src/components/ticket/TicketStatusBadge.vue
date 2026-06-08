<template>
  <span :class="['ticket-status-badge', statusClass]">{{ label }}</span>
</template>

<script>
const STATUS_MAP = {
  REGISTERED: { label: '등록됨', cssVar: 'var(--color-status-registered)' },
  RECEIVED: { label: '접수됨', cssVar: 'var(--color-status-received)' },
  PROCESSING: { label: '처리 중', cssVar: 'var(--color-status-processing)' },
  DELAYED: { label: '지체됨', cssVar: 'var(--color-status-delayed)' },
  COMPLETION_REQUESTED: { label: '완료 요청', cssVar: 'var(--color-status-completion-requested)' },
  APPROVED: { label: '승인됨', cssVar: 'var(--color-status-approved)' },
  COMPLETED: { label: '완료', cssVar: 'var(--color-status-completed)' }
}

export default {
  name: 'TicketStatusBadge',
  props: {
    status: { type: String, required: true }
  },
  computed: {
    label() {
      return (STATUS_MAP[this.status] && STATUS_MAP[this.status].label) || this.status
    },
    statusClass() {
      return 'ticket-status-badge--' + (this.status || '').toLowerCase()
    }
  }
}
</script>

<style scoped>
.ticket-status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  line-height: 22px;
  white-space: nowrap;
}
.ticket-status-badge--registered {
  background: #f1f3f5;
  color: #5f6368;
}
.ticket-status-badge--received {
  background: #e8f0fe;
  color: #1a73e8;
}
.ticket-status-badge--processing {
  background: #fef3e0;
  color: #ea8c00;
}
.ticket-status-badge--delayed {
  background: #fde7e7;
  color: #d93025;
}
.ticket-status-badge--completion-requested {
  background: #f3e8fd;
  color: #9334e6;
}
.ticket-status-badge--approved {
  background: #e6f7ed;
  color: #137333;
}
.ticket-status-badge--completed {
  background: #e6f7ed;
  color: #137333;
}
</style>
