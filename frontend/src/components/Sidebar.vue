<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const navItems = [
  { label: '대시보드', path: '/dashboard', icon: 'M3 13h8v8H3zm0-9h8v8H3zm9 0h8v8H12zm0 9h8v8H12z' },
  { label: '티켓 목록', path: '/tickets', icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2' },
  { label: '회사 관리', path: '/companies', icon: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4' },
  { label: '사용자 관리', path: '/users', icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2M9 7a4 4 0 118 0H9z' },
  { label: '프로젝트', path: '/projects', icon: 'M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z' },
]

const isLoading = ref(true)

function isActive(path) {
  return route.path.startsWith(path)
}

// Simulate loading
onMounted(() => {
  const t = setInterval(() => {
    if (isLoading.value) {
      isLoading.value = false
      clearInterval(t)
    }
  }, 100)
})
</script>

<template>
  <div class="sidebar" :class="{ 'sidebar--collapsed': !expanded }">
    <!-- Logo Area -->
    <div class="sidebar__header">
      <div class="logo">
        <div class="logo__mark">
          <span>NT</span>
        </div>
        <div class="logo__text">
          <div class="logo__title">nu_Trust</div>
          <div class="logo__subtitle">신뢰 구축 플랫폼</div>
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
  </div>
</template>

<style scoped>
.sidebar {
  width: 240px;
  min-width: 240px;
  height: 100vh;
  background: var(--navy);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.2s ease;
}

/* Logo */
.sidebar__header {
  padding: 20px 16px;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo__mark {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--teal);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo__mark span {
  color: var(--white);
  font-weight: 700;
  font-size: 14px;
  letter-spacing: -0.5px;
}

.logo__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
}

.logo__title {
  color: var(--white);
  font-weight: 600;
  font-size: 15px;
  white-space: nowrap;
}

.logo__subtitle {
  color: var(--gray-400);
  font-size: 11px;
  white-space: nowrap;
}

/* Navigation */
.sidebar__nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
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
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--white);
}

.nav-item--active {
  background: rgba(2, 195, 154, 0.15);
  color: var(--mint);
}

.nav-item__icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-item__label {
  white-space: nowrap;
}

/* User Pill */
.sidebar__footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 8px;
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
  color: var(--white);
  font-weight: 600;
  font-size: 13px;
  flex-shrink: 0;
}

.user-pill__info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow: hidden;
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
  width: fit-content;
}
</style>
