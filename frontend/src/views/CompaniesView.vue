<script setup>
import { ref } from 'vue'

const activeTab = ref('all')
const tabs = [
  { key: 'all', label: '전체', count: '42' },
  { key: 'dev', label: '개발사', count: '3' },
  { key: 'client', label: '고객사', count: '39' },
  { key: 'inactive', label: '비활성', count: '4' },
]

const companies = [
  { name: '(주)엔유비즈', type: 'DEV', typeName: 'DEV', ceo: '홍길동', biz: '123-45-67890', members: 8, projects: 12, status: 'active', statusLabel: '활성', regDate: '2024/01/15' },
  { name: 'ABC 물류(주)', type: 'CLIENT', typeName: 'CLIENT', ceo: '김갑식', biz: '987-65-43210', members: 5, projects: 3, status: 'active', statusLabel: '활성', regDate: '2024/02/20' },
  { name: 'XYZ 테크놀로지', type: 'CLIENT', typeName: 'CLIENT', ceo: '이영희', biz: '111-22-33333', members: 3, projects: 2, status: 'inactive', statusLabel: '비활성', regDate: '2024/03/10' },
  { name: 'DEF 커머스(주)', type: 'CLIENT', typeName: 'CLIENT', ceo: '최관리', biz: '444-55-66777', members: 12, projects: 5, status: 'active', statusLabel: '활성', regDate: '2024/04/05' },
]
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">회사 관리</div>
        <div class="page-subtitle">총 42개 회사 · 개발사 3 / 고객사 39</div>
      </div>
      <button class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="16" height="16" stroke-width="2" stroke-linecap="round">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        회사 등록
      </button>
    </div>

    <!-- Tab Row -->
    <div class="tab-row">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ 'tab-btn--active': activeTab === tab.key }"
      >
        {{ tab.label }} <span class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="search-box" style="width:260px">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="14" height="14" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
        <input placeholder="회사명 또는 사업자번호 검색">
      </div>
      <button class="btn btn-secondary btn-sm">필터 ▾</button>
      <button class="btn btn-secondary btn-sm">정렬 ▾</button>
      <div style="margin-left:auto;display:flex;gap:8px">
        <button class="btn btn-outline btn-sm">↓ 내보내기</button>
      </div>
    </div>

    <!-- Company Table Card -->
    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th style="width:30px"><input type="checkbox" style="accent-color:var(--teal)"></th>
              <th>회사명</th>
              <th>타입</th>
              <th>대표자</th>
              <th>사업자번호</th>
              <th>멤버</th>
              <th>프로젝트</th>
              <th>상태</th>
              <th>등록일</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(comp, i) in companies" :key="i">
              <td><input type="checkbox" style="accent-color:var(--teal)"></td>
              <td style="font-weight:600;color:var(--gray-900)">{{ comp.name }}</td>
              <td>
                <span
                  v-if="comp.type === 'DEV'"
                  style="font-size:11px;font-weight:700;background:#edf2ff;color:#3730a3;padding:2px 7px;border-radius:4px"
                >{{ comp.typeName }}</span>
                <span
                  v-else
                  style="font-size:11px;font-weight:700;background:var(--mint-light);color:#065f46;padding:2px 7px;border-radius:4px"
                >{{ comp.typeName }}</span>
              </td>
              <td>{{ comp.ceo }}</td>
              <td style="font-family:var(--mono);font-size:12px">{{ comp.biz }}</td>
              <td>{{ comp.members }}</td>
              <td>{{ comp.projects }}</td>
              <td>
                <span
                  class="badge"
                  :class="comp.status === 'active' ? 'active-b' : 'inactive-b'"
                >
                  <span class="badge-dot"></span>{{ comp.statusLabel }}
                </span>
              </td>
              <td class="text-muted">{{ comp.regDate }}</td>
              <td>
                <div style="display:flex;gap:4px">
                  <button class="btn btn-ghost btn-sm">편집</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-muted {
  color: var(--gray-500);
}
</style>
