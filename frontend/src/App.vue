<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Login from './views/Login.vue'

const route = useRoute()
const isLogin = computed(() => route.name === 'Login')
</script>

<template>
  <Login v-if="isLogin" />
  <router-view v-else />
</template>
