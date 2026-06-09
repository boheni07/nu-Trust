<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getMyNotifications, markAsRead } from '../api/notifications.js'

const props = defineProps({
  show: { type: Boolean, default: false },
})

const emits = defineEmits(['close'])

const items = ref([])
const loading = ref(false)
const unreadCount = ref(0)

async function loadNotifications() {
  loading.value = true
  try {
    const res = await getMyNotifications({ page: 0, pageSize: 20 })
    items.value = Array.isArray(res) ? res : (res.content ?? res.items ?? [])
    unreadCount.value = items.value.filter(n => !n.isRead).length
  } catch {
    items.value = []
  } finally {
    loading.value = false
  }
}

async function handleClickRead(item) {
  try {
    await markAsRead(item.id)
    item.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  } catch {}
}

function formatTime(d) {
  if (!d) return ''
  const t = new Date(d)
  const now = new Date()
  const ms = now - t
  if (ms < 60000) return '방금 전'
  if (ms < 3600000) return `${Math.floor(ms / 60000)}분 전`
  if (ms < 86400000) return `${Math.floor(ms / 3600000)}시간 전`
  return t.toLocaleDateString('ko-KR')
}

onMounted(loadNotifications)
</script>

<template>
  <Transition name="notif-panel">
    <div v-if="show" class="notif-panel" @click.self="emits('close')">
      <div class="notif-header">
        <span class="notif-title">알림</span>
        <span class="notif-badge" v-if="unreadCount">{{ unreadCount }}건</span>
      </div>

      <div v-if="loading" class="notif-empty">불러오는 중…</div>
      <div v-else-if="!items.length" class="notif-empty">새로운 알림이 없습니다</div>

      <ul v-else class="notif-list">
        <li
          v-for="item in items"
          :key="item.id"
          class="notif-item"
          :class="{ 'notif-item--read': item.isRead }"
          @click="handleClickRead(item)"
        >
          <div class="notif-dot" :class="{ 'notif-dot--unread': !item.isRead }"></div>
          <div class="notif-body">
            <div class="notif-text">{{ item.payload ?? '새 알림' }}</div>
            <div class="notif-time">{{ formatTime(item.createdAt || item.created_at) }}</div>
          </div>
        </li>
      </ul>

      <div class="notif-footer" @click="emits('close')">
        닫기
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.notif-panel {
  position: fixed;
  top: 64px;
  right: 16px;
  width: 360px;
  max-height: 480px;
  background: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--gray-200);
  z-index: 100;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notif-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--gray-200);
}

.notif-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--gray-900);
}

.notif-badge {
  font-size: 11px;
  color: var(--red);
  font-weight: 600;
}

.notif-list {
  list-style: none;
  overflow-y: auto;
  flex: 1;
}

.notif-item {
  display: flex;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  transition: background var(--anim);
}

.notif-item:hover {
  background: var(--gray-50);
}

.notif-item--read {
  opacity: 0.6;
}

.notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--gray-300);
  flex-shrink: 0;
  margin-top: 4px;
}

.notif-dot--unread {
  background: var(--blue);
}

.notif-body {
  flex: 1;
  min-width: 0;
}

.notif-text {
  font-size: 13px;
  color: var(--gray-700);
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notif-time {
  font-size: 11px;
  color: var(--gray-400);
  margin-top: 2px;
}

.notif-empty {
  padding: 24px 16px;
  text-align: center;
  font-size: 13px;
  color: var(--gray-400);
}

.notif-footer {
  padding: 8px 16px;
  text-align: center;
  font-size: 12px;
  color: var(--gray-400);
  border-top: 1px solid var(--gray-200);
  cursor: pointer;
}

.notif-footer:hover {
  color: var(--gray-600);
}

.notif-panel-enter-active,
.notif-panel-leave-active {
  transition: opacity var(--anim);
}

.notif-panel-enter-active .notif-panel,
.notif-panel-leave-active .notif-panel {
  transition: transform var(--anim);
}

.notif-panel-enter-from,
.notif-panel-leave-to {
  opacity: 0;
}
</style>
