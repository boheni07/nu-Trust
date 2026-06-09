<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { getChatMessages, sendChatMessage, subscribeToChat } from '../api/chat.js'

const props = defineProps({
  ticketId: [String, Number],
})

const messages = ref([])
const input = ref('')
const sending = ref(false)
const loading = ref(false)
const connected = ref(false)
const wsError = ref(false)

const scrollRef = ref(null)

function scrollToBottom() {
  nextTick(() => {
    if (scrollRef.value) {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    }
  })
}

async function loadHistory() {
  if (!props.ticketId) return
  loading.value = true
  try {
    const data = await getChatMessages(props.ticketId)
    messages.value = Array.isArray(data) ? data.map(m => formatMsg(m)) : (data.content ?? [])
    scrollToBottom()
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

async function sendMessage() {
  if (!input.value.trim() || sending.value || !props.ticketId) return
  sending.value = true
  try {
    await sendChatMessage(props.ticketId, input.value.trim())
    input.value = ''
    await loadHistory()
  } catch {
    alert('메시지 전송 실패')
  } finally {
    sending.value = false
  }
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

let sub = null

onMounted(async () => {
  await loadHistory()

  sub = subscribeToChat(props.ticketId, (data) => {
    connected.value = true
    wsError.value = false
    const msg = formatMsg(data)
    messages.value.push(msg)
    scrollToBottom()
  })
})

function formatMsg(m) {
  const userId = m.userId || m.user_id || ''
  const name = m.userName || m.user_name || m.author_name || m.authorName || '사용자'
  const isMine = userId.length > 0 && String(m.currentUserId || '').endsWith(String(userId).slice(-4))
  return {
    id: m.id,
    content: m.content || '',
    imageUrl: m.imageUrl || m.image_url || null,
    createdAt: m.sentAt || m.sent_at || m.createdAt || m.created_at || '',
    userName: name,
    isMine,
    senderId: userId,
  }
}

watch(() => sub?.isActivated, (val) => {
  connected.value = !!val
  if (!val) wsError.value = true
})

onUnmounted(() => {
  sub?.disconnect()
})
</script>

<template>
  <div class="chat-view">
    <div class="chat-header">
      <div class="chat-title">💬 실시간 채팅</div>
      <div class="chat-status" :class="{ 'chat-status--online': connected, 'chat-status--error': wsError }">
        <span class="status-dot"></span>
        <span>{{ connected ? '연결됨' : wsError ? '연결 끊김' : '대기 중' }}</span>
      </div>
    </div>

    <div ref="scrollRef" class="chat-messages">
      <div v-for="msg in messages" :key="msg.id" class="msg" :class="{ 'msg--mine': msg.isMine }">
        <template v-if="!msg.isMine">
          <div class="msg-avatar" :style="{ background: msg.color }">{{ msg.userName.charAt(0) }}</div>
        </template>
        <div class="msg-body">
          <div class="msg-name">{{ msg.userName }}</div>
          <div class="msg-bubble">{{ msg.content }}</div>
          <img v-if="msg.imageUrl" :src="msg.imageUrl" alt="attachment" class="msg-image" />
          <div class="msg-time">{{ msg.createdAt }}</div>
        </div>
        <template v-if="msg.isMine">
          <div class="msg-avatar" :style="{ background: msg.color }">{{ msg.userName.charAt(0) }}</div>
        </template>
      </div>

      <div v-if="!messages.length && !loading" class="chat-empty">
        아직 채팅 내역이 없습니다. 첫 메시지를 보내보세요.
      </div>
    </div>

    <div class="chat-input-area">
      <div class="chat-input-box">
        <textarea
          v-model="input"
          @keydown="handleKeydown"
          placeholder="메시지를 입력하세요..."
          rows="1"
        ></textarea>
        <button class="chat-send-btn" @click="sendMessage" :disabled="sending" :aria-label="sending ? '전송 중' : '전송'">
          <svg v-if="!sending" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"/>
            <polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
          <svg v-else class="spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2v4m0 12v4m-7.07-3.93l2.83-2.83m8.5-8.5l2.83-2.83M2 12h4m12 0h4M4.93 4.93l2.83 2.83m8.5 8.5l2.83 2.83"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-view {
  width: 100%;
  margin-top: 20px;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius-lg);
  background: var(--white);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--gray-200);
  background: var(--off-white);
}

.chat-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-700);
}

.chat-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--gray-300);
}

.chat-status--online .status-dot {
  background: var(--green);
}

.chat-status--error .status-dot {
  background: var(--red);
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 12px 16px;
}

.msg {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 10px;
}

.msg--mine {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--blue);
  color: white;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.msg-body {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.msg-name {
  font-size: 11px;
  color: var(--gray-500);
  font-weight: 500;
  padding: 0 4px;
}

.msg--mine .msg-name {
  text-align: right;
}

.msg-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  word-break: break-word;
}

.msg:not(.msg--mine) .msg-bubble {
  background: var(--blue-light);
  color: var(--gray-900);
  border-bottom-left-radius: 4px;
}

.msg--mine .msg-bubble {
  background: var(--blue);
  color: white;
  border-bottom-right-radius: 4px;
}

.msg-image {
  max-width: 240px;
  border-radius: 8px;
  margin-top: 4px;
}

.msg-time {
  font-size: 10px;
  color: var(--gray-400);
  padding: 0 4px;
}

.chat-empty {
  text-align: center;
  color: var(--gray-400);
  font-size: 13px;
  padding: 32px 16px;
}

.chat-input-area {
  padding: 8px 12px;
  border-top: 1px solid var(--gray-200);
  background: var(--off-white);
}

.chat-input-box {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.chat-input-box textarea {
  flex: 1;
  resize: none;
  border: 1px solid var(--gray-200);
  border-radius: var(--radius);
  padding: 8px 12px;
  font-size: 13px;
  font-family: var(--font);
  outline: none;
  max-height: 120px;
  min-height: 36px;
  line-height: 1.4;
}

.chat-input-box textarea:focus {
  border-color: var(--blue);
}

.chat-send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: var(--blue);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: opacity var(--anim);
}

.chat-send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.chat-send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.chat-send-btn .spin {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
