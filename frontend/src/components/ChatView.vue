<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { getChatMessages, sendChatMessage, subscribeToChat, subscribeToTyping } from '../api/chat.js'

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
const fileInputRef = ref(null)

// typing indicator
const typingTimeout = ref(null)
const isTyping = ref(false)
const isOthersTyping = ref(false)

// file upload
const selectedFiles = ref([])
const uploadedImageUrls = ref([])

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

function sendTypingStart() {
  clearTimeout(typingTimeout.value)
  if (!isTyping.value) {
    isTyping.value = true
    sub?.publish('/app/typing.' + props.ticketId, {
      ticketId: props.ticketId,
      userId: 1,
      userName: '사용자',
    })
  }
  typingTimeout.value = setTimeout(() => {
    isTyping.value = false
  }, 2000)
}

function handleTyping() {
  sendTypingStart()
}

async function uploadSelectedFiles() {
  if (selectedFiles.value.length === 0) return []

  const urls = []
  for (const item of selectedFiles.value) {
    try {
      const formData = new FormData()
      formData.append('file', item.file)
      const response = await fetch('/api/v1/files/upload', {
        method: 'POST',
        body: formData,
      })
      if (response.ok) {
        const json = await response.json()
        if (json.url) urls.push(json.url)
      }
    } catch (e) {
      console.error('파일 업로드 실패:', e)
    }
  }

  selectedFiles.value = []
  return urls
}

async function sendMessage() {
  const trimmedInput = input.value.trim()
  if (!trimmedInput && uploadedImageUrls.value.length === 0) return

  const fileUrls = await uploadSelectedFiles()
  const allImageUrls = [...(uploadedImageUrls.value || []), ...fileUrls]

  sending.value = true
  try {
    await sendChatMessage(props.ticketId, trimmedInput, allImageUrls)

    // also publish via WebSocket if connected
    if (sub?.isActivated) {
      sub.publish('/app/chat.' + props.ticketId, {
        ticketId: props.ticketId,
        content: trimmedInput,
        imageUrl: allImageUrls,
      })
    }

    input.value = ''
    uploadedImageUrls.value = []
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

const handleFileSelect = async (event) => {
  const files = event.target.files
  if (!files || files.length === 0) return

  for (const file of files) {
    if (!file.type.startsWith('image/')) continue
    if (file.size > 10 * 1024 * 1024) continue

    const previewUrl = URL.createObjectURL(file)
    selectedFiles.value.push({
      file,
      previewUrl,
      name: file.name,
    })
  }

  event.target.value = ''
}

const removeFile = (index) => {
  const removed = selectedFiles.value.splice(index, 1)[0]
  if (removed && removed.previewUrl) {
    URL.revokeObjectURL(removed.previewUrl)
  }
}

let sub = null
let unsubTyping = null

onMounted(async () => {
  await loadHistory()

  sub = subscribeToChat(props.ticketId, (data) => {
    connected.value = true
    wsError.value = false
    const msg = formatMsg(data)
    messages.value.push(msg)
    scrollToBottom()
  })

  unsubTyping = subscribeToTyping(props.ticketId, () => {
    isOthersTyping.value = true
    clearTimeout(typingTimeout.value)
    typingTimeout.value = setTimeout(() => {
      isOthersTyping.value = false
    }, 3000)
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
  unsubTyping?.()
  clearTimeout(typingTimeout.value)
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

    <div v-if="isOthersTyping" class="typing-indicator-bar">
      <span class="typing-dot"></span>
      <span>메시지 작성 중...</span>
    </div>

    <div class="chat-input-area">
      <div class="chat-input-box">
        <input
          ref="fileInputRef"
          type="file"
          accept="image/jpeg,image/png,image/gif,image/webp"
          class="file-input-hidden"
          @change="handleFileSelect"
        />

        <button class="file-upload-btn" @click="fileInputRef.click()" title="파일 첨부">
          <svg viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M16.5 6v11.5c0 2.21-1.79 4-4 4s-4-1.79-4-4V5c0-1.38 1.12-2.5 2.5-2.5s2.5 1.12 2.5 2.5v10.5c0 .55-.45 1-1 1s-1-.45-1-1V6H10v9.5c0 1.38 1.12 2.5 2.5 2.5s2.5-1.12 2.5-2.5V5c0-2.21-1.79-4-4-4S7 2.79 7 5v12.5c0 3.04 2.46 5.5 5.5 5.5s5.5-2.46 5.5-5.5V6h-1.5z"/>
          </svg>
        </button>

        <div v-if="selectedFiles.length > 0" class="file-preview">
          <div v-for="(file, idx) in selectedFiles" :key="idx" class="file-preview-item">
            <img :src="file.previewUrl" :alt="file.name" class="preview-thumbnail" />
            <button class="remove-file-btn" @click="removeFile(idx)">&times;</button>
          </div>
        </div>

        <textarea
          v-model="input"
          @input="handleTyping"
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

.file-input-hidden {
  display: none;
}

.file-upload-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--gray-400);
  padding: 4px;
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color var(--anim);
  flex-shrink: 0;
}

.file-upload-btn:hover {
  color: var(--teal);
}

.file-preview {
  display: flex;
  gap: 8px;
  padding: 4px 0;
  overflow-x: auto;
  flex-shrink: 0;
}

.file-preview-item {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--gray-200);
  flex-shrink: 0;
}

.preview-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-file-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  padding: 0;
}

.typing-indicator-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 16px;
  font-size: 12px;
  color: var(--gray-500);
}

.typing-dot {
  width: 4px;
  height: 4px;
  background: var(--teal);
  border-radius: 50%;
  animation: typingBounce 1.4s infinite;
}

@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-4px); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
