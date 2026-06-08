<script setup>
import { ref } from 'vue'

const activeTab = ref('all')
const tabs = [
  { key: 'all', label: '전체', count: '68' },
  { key: 'admin', label: 'Admin', count: '3' },
  { key: 'support', label: 'Support', count: '12' },
  { key: 'customer', label: 'Customer', count: '53' },
]

const users = [
  {
    name: '홍길동',
    initials: '홍',
    email: 'hong@enubiz.co.kr',
    role: 'admin',
    roleName: 'ADMIN',
    roleClass: 'role-admin',
    company: '(주)엔유비즈',
    status: 'active',
    statusLabel: '활성',
    regDate: '2024/01/15',
    lastLogin: '방금 전',
    avatarGrad: 'linear-gradient(135deg,var(--teal),var(--mint))',
  },
  {
    name: '김지원',
    initials: '김',
    email: 'kim@enubiz.co.kr',
    role: 'support',
    roleName: 'SUPPORT',
    roleClass: 'role-support',
    company: '(주)엔유비즈',
    status: 'active',
    statusLabel: '활성',
    regDate: '2024/02/01',
    lastLogin: '2시간 전',
    avatarGrad: 'linear-gradient(135deg,var(--blue),#818cf8)',
  },
  {
    name: '박지수',
    initials: '박',
    email: 'park@enubiz.co.kr',
    role: 'support',
    roleName: 'SUPPORT',
    roleClass: 'role-support',
    company: '(주)엔유비즈',
    status: 'active',
    statusLabel: '활성',
    regDate: '2024/02/10',
    lastLogin: '어제',
    avatarGrad: 'linear-gradient(135deg,var(--purple),#c084fc)',
  },
  {
    name: '이영희',
    initials: '이',
    email: 'lee@abclang.co.kr',
    role: 'customer',
    roleName: 'CUSTOMER',
    roleClass: 'role-customer',
    company: 'ABC 물류(주)',
    status: 'active',
    statusLabel: '활성',
    regDate: '2024/03/01',
    lastLogin: '3일 전',
    avatarGrad: 'linear-gradient(135deg,var(--amber),#fbbf24)',
  },
  {
    name: '최관리',
    initials: '최',
    email: 'choi@defcommerce.com',
    role: 'customer',
    roleName: 'CUSTOMER',
    roleClass: 'role-customer',
    company: 'DEF 커머스(주)',
    status: 'inactive',
    statusLabel: '비활성',
    regDate: '2024/04/05',
    lastLogin: '1개월 전',
    avatarGrad: 'linear-gradient(135deg,#ef4444,#f97316)',
  },
  {
    name: '강동원',
    initials: '강',
    email: 'kang@xyztech.co.kr',
    role: 'customer',
    roleName: 'CUSTOMER',
    roleClass: 'role-customer',
    company: 'XYZ 테크놀로지',
    status: 'inactive',
    statusLabel: '비활성',
    regDate: '2024/05/20',
    lastLogin: '2개월 전',
    avatarGrad: 'linear-gradient(135deg,var(--teal),var(--blue))',
  },
]
</script>

<template>
  <div class="page-container">
    <!-- Page Toolbar -->
    <div class="page-toolbar">
      <div>
        <div class="page-title">사용자 관리</div>
        <div class="page-subtitle">총 68명 · Admin 3 / Support 12 / Customer 53</div>
      </div>
      <button class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="16" height="16" stroke-width="2" stroke-linecap="round">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        사용자 초대
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
      <div class="search-box" style="width:240px">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" width="14" height="14" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
        <input placeholder="이름 또는 이메일 검색">
      </div>
      <button class="btn btn-secondary btn-sm">필터 ▾</button>
    </div>

    <!-- User Table Card -->
    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>이름</th>
              <th>이메일</th>
              <th>역할</th>
              <th>소속 회사</th>
              <th>상태</th>
              <th>가입일</th>
              <th>마지막 로그인</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.email">
              <td>
                <span class="flex-center gap-2">
                  <span
                    class="avatar-circle"
                    :style="{ background: user.avatarGrad }"
                  >{{ user.initials }}</span>
                  <span style="font-weight:600">{{ user.name }}</span>
                </span>
              </td>
              <td class="text-muted">{{ user.email }}</td>
              <td>
                <span class="role-badge" :class="user.roleClass">{{ user.roleName }}</span>
              </td>
              <td>{{ user.company }}</td>
              <td>
                <span class="badge" :class="user.status === 'active' ? 'active-b' : 'inactive-b'">
                  <span class="badge-dot"></span>{{ user.statusLabel }}
                </span>
              </td>
              <td class="text-muted">{{ user.regDate }}</td>
              <td class="text-muted">{{ user.lastLogin }}</td>
              <td>
                <button class="btn btn-ghost btn-sm">편집</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.avatar-circle {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  color: white;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.role-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 4px;
  display: inline-block;
}

.role-admin {
  background: #edf2ff;
  color: #3730a3;
}

.role-support {
  background: var(--mint-light);
  color: var(--teal);
}

.role-customer {
  background: var(--amber-light, #FFFBEB);
  color: #92400e;
}
</style>
