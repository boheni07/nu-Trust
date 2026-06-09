import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { Client } from '@stomp/stompjs'
import { req } from './projects.js'

const WS_BASE = '/ws'
const API_BASE = '/api/v1/tickets'

/* ─── REST API ─────────────────────────────────── */

export async function getChatMessages(ticketId, since) {
  const qs = since ? `?since=${encodeURIComponent(since)}` : ''
  return req(`${API_BASE}/${ticketId}/chat/messages${qs}`)
}

export async function sendChatMessage(ticketId, content, imageUrl) {
  return req(`${API_BASE}/${ticketId}/chat/messages`, {
    method: 'POST',
    body: JSON.stringify({ ticketId, content, imageUrl: imageUrl || [] }),
  })
}

/* ─── WebSocket (SockJS + STOMP) ────────────────── */

const stompClients = new Map() // key: ticketId

function buildClient(ticketId) {
  const client = new Client({
    webSocketFactory: () => new SockJS(WS_BASE),
    connectHeaders: {},
    connectDelay: 0,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: (frame) => {
      const subPath = `/topic/ticket.${ticketId}`
      client.subscribe(subPath, (msg) => {
        if (msg.body) {
          try {
            const data = JSON.parse(msg.body)
            client.onMessageEvent?.(data)
          } catch {
            client.onMessageEvent?.(msg.body)
          }
        }
      })
    },
    onStompError: (frame) => {
      console.error('STOMP error:', frame.headers['message'])
    },
    onWebSocketError: (event) => {
      console.error('WebSocket error:', event)
    },
  })
  client.activate()
  return client
}

/**
 * Connect to the chat topic for a ticket.
 * @param {number|string} ticketId
 * @returns {{
 *   disconnect: () => void,
 *   onMessage: (fn: (data) => void) => void,
 *   isActivated: boolean,
 * }}
 */
export function subscribeToChat(ticketId, onMessage) {
  const id = String(ticketId)
  let client = stompClients.get(id)

  if (!client || !client.active) {
    if (client) {
      try { client.deactivate() } catch {}
      stompClients.delete(id)
    }
    client = buildClient(id)
    stompClients.set(id, client)
  }

  client.onMessageEvent = onMessage

  return {
    get isActivated() { return client?.active || false },
    disconnect() {
      try { client?.deactivate() } catch {}
      stompClients.delete(id)
    },
  }
}

/** Disconnect all chat clients */
export function disconnectAllChat() {
  for (const [id, c] of stompClients.entries()) {
    try { c.deactivate() } catch {}
  }
  stompClients.clear()
}
