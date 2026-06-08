<script setup>
import { ref } from 'vue'

const loginType = ref('email')
const rememberEmail = ref(false)
const autoLogin = ref(false)
const email = ref('')
const password = ref('')
</script>

<template>
  <div class="login-screen">
    <div class="login-card">
      <!-- Logo -->
      <div class="login-card__brand">
        <div class="login-card__icon">NT</div>
        <div class="login-card__title">nu_Trust</div>
        <div class="login-card__subtitle">신뢰 구축 플랫폼</div>
      </div>

      <!-- Tabs -->
      <div class="login-tabs">
        <button
          class="login-tab"
          :class="{ 'login-tab--active': loginType === 'email' }"
          @click="loginType = 'email'"
        >
          이메일
        </button>
        <button
          class="login-tab"
          :class="{ 'login-tab--active': loginType === 'company' }"
          @click="loginType = 'company'"
        >
          회사
        </button>
      </div>

      <!-- Form -->
      <div class="login-form">
        <div class="form-group">
          <label class="form-label">이메일</label>
          <input
            v-model="email"
            type="email"
            class="form-input"
            placeholder="이메일을 입력하세요"
          />
        </div>
        <div class="form-group">
          <label class="form-label">비밀번호</label>
          <input
            v-model="password"
            type="password"
            class="form-input"
            placeholder="비밀번호를 입력하세요"
          />
        </div>

        <div class="form-options">
          <label class="checkbox-label">
            <div class="checkbox-circle">
              <div v-if="rememberEmail" class="checkmark"></div>
            </div>
            <span>비밀번호 저장</span>
          </label>
          <label class="checkbox-label">
            <div class="checkbox-circle">
              <div v-if="autoLogin" class="checkmark"></div>
            </div>
            <span>자동 로그인</span>
          </label>
        </div>

        <button class="btn-login">로그인</button>

        <div class="login-divider">
          <span class="divider-line"></span>
          <span class="divider-text">or</span>
          <span class="divider-line"></span>
        </div>

        <div class="social-buttons">
          <button class="social-btn">
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" />
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
            </svg>
          </button>
          <button class="social-btn">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#FEE500">
              <path d="M12 0C5.37 0 0 3.87 0 8.64c0 2.7 1.39 5.11 3.57 6.69L3 21l6.11-3.17c1.29.56 2.72.87 4.13.87 6.63 0 12-3.87 12-8.64S18.63 0 12 0z" />
              <text x="12" y="14" text-anchor="middle" font-size="9" font-weight="bold" fill="#333">K</text>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-screen {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0D1B3E 0%, #152040 50%, #1E2F55 100%);
}

.login-card {
  width: 380px;
  max-width: 90vw;
  background: var(--white);
  border-radius: 12px;
  padding: 48px 40px;
}

.login-card__brand {
  text-align: center;
  margin-bottom: 40px;
}

.login-card__icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: var(--teal);
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: var(--white);
  letter-spacing: -1px;
}

.login-card__title {
  margin-top: 16px;
  font-size: 24px;
  font-weight: 700;
  color: var(--navy);
  letter-spacing: -0.5px;
}

.login-card__subtitle {
  font-size: 13px;
  color: var(--gray-500);
  margin-top: 4px;
}

/* Tabs */
.login-tabs {
  display: flex;
  border-bottom: 1px solid var(--gray-200);
  margin-bottom: 24px;
}

.login-tab {
  flex: 1;
  padding: 12px 0;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--gray-500);
  cursor: pointer;
  text-align: center;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.login-tab--active {
  color: var(--teal);
  border-bottom-color: var(--teal);
}

/* Form */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--gray-700);
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 14px;
  border: 1px solid var(--gray-200);
  border-radius: 8px;
  font-size: 14px;
  color: var(--gray-700);
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: var(--teal);
}

.form-input::placeholder {
  color: var(--gray-400);
}

/* Options */
.form-options {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--gray-500);
  cursor: pointer;
}

.checkbox-circle {
  width: 16px;
  height: 16px;
  border: 1.5px solid var(--gray-300);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.checkbox-circle + span,
.checkbox-label:hover {
  color: var(--gray-700);
}

.checkmark {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--white);
}

/* Login Button */
.btn-login {
  width: 100%;
  height: 48px;
  background: var(--teal);
  color: var(--white);
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 20px;
}

.btn-login:hover {
  background: #016875;
}

/* Divider */
.login-divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: var(--gray-200);
}

.divider-text {
  font-size: 12px;
  color: var(--gray-400);
}

/* Social */
.social-buttons {
  display: flex;
  gap: 12px;
}

.social-btn {
  flex: 1;
  height: 44px;
  border: 1px solid var(--gray-200);
  border-radius: 8px;
  background: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
}

.social-btn:hover {
  background: var(--gray-50);
}
</style>
