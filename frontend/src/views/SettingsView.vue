<script setup>
import { ref } from 'vue'

const activeSection = ref('sla')
const sections = [
  { key: 'sla', label: 'SLA 설정' },
  { key: 'notification', label: '알림 설정' },
  { key: 'ratelimit', label: 'Rate Limit' },
  { key: 'cache', label: '캐시 정책' },
  { key: 'calendar', label: 'Business Calendar' },
  { key: 'auditlog', label: '감사 로그' },
]

const slaSettings = ref({
  high: 2,
  medium: 5,
  low: 14,
  warning: 1,
})

function handleSave() {
  // 기능 없음 - 시각적 레플리카만
}

function handleReset() {
  slaSettings.value = { high: 2, medium: 5, low: 14, warning: 1 }
}

function showPlaceholder() {
  // 기능 없음 - 시각적 레플리카만
}
</script>

<template>
  <div class="page-container">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-title">시스템 설정</div>
      <div class="page-subtitle">SLA, Rate Limit, 알림, 캐시 정책 등 전체 시스템 구성</div>
    </div>

    <!-- Two Column Layout -->
    <div style="display:grid;grid-template-columns:220px 1fr;gap:20px">
      <!-- Settings Sidebar -->
      <div class="card" style="height:fit-content">
        <div style="padding:8px 0">
          <div style="padding:8px 16px;font-size:11px;font-weight:700;color:var(--gray-400);text-transform:uppercase;letter-spacing:0.06em">설정 항목</div>
          <div
            v-for="section in sections"
            :key="section.key"
            style="padding:9px 16px;font-size:13px;cursor:pointer;transition:all var(--anim)"
            :class="activeSection === section.key ? 'is-active-section' : 'is-inactive-section'"
            :style="activeSection === section.key
              ? 'color:var(--teal);font-weight:600;background:var(--mint-light);border-right:2px solid var(--teal)'
              : 'color:var(--gray-600)'"
            @click="activeSection = section.key"
          >{{ section.label }}</div>
        </div>
      </div>

      <!-- Content Area -->
      <div>
        <!-- SLA 설정 -->
        <div v-show="activeSection === 'sla'" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:4px">SLA 설정</div>
          <div style="font-size:12px;color:var(--gray-500);margin-bottom:20px">우선순위별 SLA 기한 및 경고 시점을 설정합니다</div>
          <div class="form-grid" style="margin-bottom:20px">
            <div class="form-group">
              <label class="form-label">HIGH 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.high">
            </div>
            <div class="form-group">
              <label class="form-label">MEDIUM 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.medium">
            </div>
            <div class="form-group">
              <label class="form-label">LOW 우선순위 SLA (일)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.low">
            </div>
            <div class="form-group">
              <label class="form-label">SLA 경고 시점 (D-)</label>
              <input type="number" class="form-input" v-model.number="slaSettings.warning">
            </div>
          </div>
          <div style="display:flex;gap:8px">
            <button class="btn btn-primary" @click="handleSave">저장</button>
            <button class="btn btn-secondary" @click="handleReset">초기화</button>
          </div>
        </div>

        <!-- 알림 설정 (placeholder) -->
        <div v-show="activeSection !== 'sla'" class="card" style="padding:24px">
          <div style="font-size:15px;font-weight:700;color:var(--gray-900);margin-bottom:8px">{{ sections.find(s => s.key === activeSection)?.label }}</div>
          <div style="font-size:12px;color:var(--gray-500)">이 기능은 준비 중입니다.</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.is-active-section {
  color: var(--teal);
  font-weight: 600;
  background: var(--mint-light);
  border-right: 2px solid var(--teal);
}

.is-inactive-section {
  color: var(--gray-600);
}

.is-inactive-section:hover {
  color: var(--gray-900);
  background: var(--gray-50);
}
</style>
