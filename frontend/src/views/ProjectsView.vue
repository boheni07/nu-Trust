<script setup>
import { ref } from 'vue'

const projects = [
  {
    name: 'POSM Web App',
    company: 'ABC 물류(주)',
    status: 'active',
    statusLabel: '활성',
    activeTickets: 8,
    teamMembers: 3,
    slaRisk: 1,
    dateRange: '2024/01 ~ 2026/12',
    iconLetter: 'P',
    avatarGrad: 'linear-gradient(135deg,var(--teal),var(--mint))',
  },
  {
    name: 'MOUAP Smart App',
    company: 'DEF 커머스(주)',
    status: 'active',
    statusLabel: '활성',
    activeTickets: 5,
    teamMembers: 5,
    slaRisk: 0,
    dateRange: '2024/06 ~ 2025/02',
    iconLetter: 'M',
    avatarGrad: 'linear-gradient(135deg,var(--blue),#818cf8)',
  },
  {
    name: 'NCS CRM',
    company: 'XYZ 테크놀로지',
    status: 'active',
    statusLabel: '활성',
    activeTickets: 3,
    teamMembers: 2,
    slaRisk: 0,
    dateRange: '2025/01 ~ 2026/01',
    iconLetter: 'N',
    avatarGrad: 'linear-gradient(135deg,var(--purple),#c084fc)',
  },
]

function handleNewProject() {
  // placeholder로만 표시 - 기능 없음
}
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">프로젝트 관리</div>
        <div class="page-subtitle">활성 18 · 보류 3 · 완료 12</div>
      </div>
      <button class="btn btn-primary">+ 프로젝트 등록</button>
    </div>

    <!-- Project Cards Grid -->
    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:14px">
      <!-- Project Card -->
      <div
        class="card"
        v-for="p in projects"
        :key="p.name"
        style="cursor:pointer;transition:all var(--anim)"
        :style="{
          boxShadow: 'var(--shadow-lg)',
          transform: 'translateY(-2px)',
        }"
        @mouseenter="e => { e.currentTarget.style.boxShadow = 'var(--shadow-lg)'; e.currentTarget.style.transform = 'translateY(-2px)'; }"
        @mouseleave="e => { e.currentTarget.style.boxShadow = ''; e.currentTarget.style.transform = ''; }"
      >
        <div style="padding:16px 20px;border-bottom:1px solid var(--gray-100)">
          <div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:8px">
            <div
              style="width:36px;height:36px;border-radius:9px;display:flex;align-items:center;justify-content:center;color:white;font-size:13px;font-weight:800"
              :style="{ background: p.avatarGrad }"
            >{{ p.iconLetter }}</div>
            <span class="badge" :class="p.status === 'active' ? 'active-b' : 'inactive-b'" style="font-size:10px">
              <span class="badge-dot"></span>{{ p.statusLabel }}
            </span>
          </div>
          <div style="font-size:14px;font-weight:700;color:var(--gray-900);margin-bottom:3px">{{ p.name }}</div>
          <div style="font-size:12px;color:var(--gray-500)">{{ p.company }}</div>
        </div>
        <div style="padding:12px 20px;display:grid;grid-template-columns:1fr 1fr 1fr;gap:12px;border-bottom:1px solid var(--gray-100)">
          <div style="text-align:center">
            <div style="font-size:18px;font-weight:700;color:var(--gray-900)">{{ p.activeTickets }}</div>
            <div style="font-size:10px;color:var(--gray-500)">활성 티켓</div>
          </div>
          <div style="text-align:center">
            <div style="font-size:18px;font-weight:700;color:var(--gray-900)">{{ p.teamMembers }}</div>
            <div style="font-size:10px;color:var(--gray-500)">팀원</div>
          </div>
          <div style="text-align:center">
            <div style="font-size:18px;font-weight:700" :style="{ color: p.slaRisk > 0 ? 'var(--red)' : 'var(--gray-900)' }">{{ p.slaRisk }}</div>
            <div style="font-size:10px;color:var(--gray-500)">SLA 위험</div>
          </div>
        </div>
        <div style="padding:10px 20px;display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:11px;color:var(--gray-400)">{{ p.dateRange }}</span>
          <span class="text-sm" style="color:var(--teal);font-weight:500">상세 보기 →</span>
        </div>
      </div>

      <!-- New Project Placeholder -->
      <div
        class="card"
        style="cursor:pointer;transition:all var(--anim);display:flex;align-items:center;justify-content:center;min-height:200px"
        @mouseenter="e => { e.currentTarget.style.borderColor = 'var(--teal)'; }"
        @mouseleave="e => { e.currentTarget.style.borderColor = ''; }"
        @click="handleNewProject"
      >
        <div style="text-align:center;color:var(--gray-400)">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="32" height="32" stroke-width="1.5" stroke-linecap="round" style="margin:0 auto 8px;display:block">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          <div style="font-size:13px;font-weight:600">새 프로젝트 등록</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card:hover:not(.placeholder) {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}
</style>
