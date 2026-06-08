<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const expanded = ref(true)
const showNotifications = ref(false)
const currentTime = ref('')

const navItems = [
  { label: '대시보드', path: '/dashboard', icon: 'M3 13h8v8H3zm0-9h8v8H3zm9 0h8v8H12zm0 9h8v8H12z' },
  { label: '티켓 목록', path: '/tickets', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { label: '회사 관리', path: '/companies', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
  { label: '사용자 관리', path: '/users', icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2M9 7a4 4 0 118 0H9z' },
  { label: '프로젝트', path: '/projects', icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z' },
]

function isActive(path) {
  return route.path === path || (path !== '/dashboard' && route.path.startsWith(path))
}

function toggleNotifications() {
  showNotifications.value = !showNotifications.value
}

function updateDate() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const h = String(now.getHours()).padStart(2, '0')
  const min = String(now.getMinutes()).padStart(2, '0')
  currentTime.value = `${y}-${m}-${d} ${h}:${min}`
}

onMounted(() => {
  updateDate()
  const id = setInterval(updateDate, 60000)
  onUnmounted(() => clearInterval(id))
})
</script>

<template>
  <div class="app-shell">
    <!-- Sidebar -->
    <aside class="sidebar">
      <!-- Brand -->
      <div class="sidebar__header">
        <div class="brand">
          <div class="brand__icon">NT</div>
          <div class="brand__texts">
            <div class="brand__title">nu_Trust</div>
            <div class="brand__subtitle">신뢰 구축 플랫폼</div>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="sidebar__nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ 'nav-item--active': isActive(item.path) }"
        >
          <svg class="nav-item__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path :d="item.icon" />
          </svg>
          <span class="nav-item__label">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- User Pill -->
      <div class="sidebar__footer">
        <div class="user-pill">
          <div class="user-pill__avatar">홍</div>
          <div class="user-pill__info">
            <div class="user-pill__name">(주)엔유비즈</div>
            <div class="user-pill__role">Admin</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main -->
    <div class="main">
      <!-- Header -->
      <header class="header">
        <div class="header__left">
          <div class="header__page-title">{{ $route.meta.title || $route.name }}</div>
          <div class="breadcrumb">
            <router-link to="/dashboard" class="breadcrumb__item">홈</router-link>
            <span class="breadcrumb__sep">/</span>
            <router-link
              v-if="$route.name === 'TicketDetail'"
              to="/tickets"
              class="breadcrumb__item breadcrumb__item--active"
            >
              티켓 목록
            </router-link>
            <span v-else class="breadcrumb__item breadcrumb__item--active">
              {{ $route.meta.title || $route.name }}
            </span>
          </div>
        </div>
        <div class="header__right">
          <div class="search-box">
            <svg class="search-box__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.35-4.35" />
            </svg>
            <input type="text" placeholder="검색..." class="search-box__input" />
          </div>
          <button class="notification-btn" @click="toggleNotifications">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
              <path d="M13.73 21a2 2 0 0 1-3.46 0" />
            </svg>
            <span class="notification-badge">1</span>
          </button>
          <div class="datetime">{{ currentTime }}</div>
          <div class="company-name">(주)엔유비즈</div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  width: 100vw;
  min-height: 100vh;
  overflow: hidden;
}

/* Sidebar */
.sidebar {
  width: 240px;
  min-width: 240px;
  height: 100vh;
  background: var(--navy);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
}

.sidebar__header {
  padding: 20px 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand__icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--teal);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: var(--white);
  flex-shrink: 0;
  letter-spacing: -0.5px;
}

.brand__texts {
  display: flex;
  flex-direction: column;
}

.brand__title {
  color: var(--white);
  font-size: 15px;
  font-weight: 600;
}

.brand__subtitle {
  color: var(--gray-400);
  font-size: 11px;
  margin-top: 2px;
}

.sidebar__nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--gray-400);
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.15s ease;
  cursor: pointer;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--white);
}

.nav-item--active {
  background: rgba(2, 195, 154, 0.15);
  color: #5EEAD4;
}

.nav-item__icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.sidebar__footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
}

.user-pill__avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--mint);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
  color: var(--white);
  flex-shrink: 0;
}

.user-pill__info {
  flex: 1;
  min-width: 0;
}

.user-pill__name {
  color: var(--white);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-pill__role {
  display: inline-block;
  background: rgba(2, 195, 154, 0.2);
  color: var(--mint);
  font-size: 10px;
  font-weight: 600;
  padding: 1px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  margin-top: 2px;
  width: fit-content;
}

/* Main area */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* Header */
.header {
  height: 56px;
  background: var(--white);
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  padding: 0 24px;
  flex-shrink: 0;
}

.header__left {
  flex: 1;
}

.header__page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--navy);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: var(--gray-500);
}

.breadcrumb__item {
  color: var(--gray-500);
  text-decoration: none;
  transition: color 0.15s;
}

.breadcrumb__item:hover {
  color: var(--navy);
}

.breadcrumb__item--active {
  color: var(--gray-700);
  font-weight: 500;
}

.breadcrumb__sep {
  color: var(--gray-200);
}

.header__right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* Search */
.search-box {
  position: relative;
  display: inline-flex;
}

.search-box__icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  stroke: var(--gray-400);
  pointer-events: none;
}

.search-box__input {
  width: 240px;
  height: 34px;
  padding: 0 12px 0 36px;
  background: var(--gray-50);
  border: 1px solid var(--gray-200);
  border-radius: 8px;
  font-size: 13px;
  color: var(--gray-700);
  outline: none;
  transition: all 0.2s;
}

.search-box__input:focus {
  border-color: var(--teal);
  background: var(--white);
}

.search-box__input::placeholder {
  color: var(--gray-400);
}

/* Notification */
.notification-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--gray-500);
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: background 0.15s;
}

.notification-btn:hover {
  background: var(--gray-100);
}

.notification-badge {
  position: absolute;
  top: -2px;
  right: -4px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  background: var(--red);
  color: var(--white);
  font-size: 10px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.datetime {
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  color: var(--gray-700);
}

.company-name {
  font-size: 13px;
  color: var(--gray-500);
}

/* Content */
.main-content {
  flex: 1;
  overflow-y: auto;
  background: var(--off-white);
  padding: 24px;
}
</style>
