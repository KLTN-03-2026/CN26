<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>{{ roleLabel }}</p>
      </div>
      <nav class="sidebar-menu">
        <a 
          v-for="item in menuItems" 
          :key="item.key"
          href="#" 
          @click.prevent="$router.push(item.path)" 
          :class="['menu-item', { active: activeMenu === item.key }]"
        >
          {{ item.label }}
        </a>
      </nav>
      <div class="sidebar-footer">
        <button 
          v-for="btn in footerButtons" 
          :key="btn.label"
          :class="btn.class"
          @click="$router.push(btn.path)"
          :style="btn.style"
        >
          {{ btn.label }}
        </button>
        <button class="btn-logout" @click="handleLogout" :style="footerButtons.length > 0 ? 'margin-top: 10px;' : ''">
          Đăng xuất
        </button>
      </div>
    </aside>
    <div class="main-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { useAuth } from '../../composables/useAuth'

defineProps({
  roleLabel: {
    type: String,
    required: true
  },
  menuItems: {
    type: Array,
    required: true
  },
  activeMenu: {
    type: String,
    default: 'home'
  },
  footerButtons: {
    type: Array,
    default: () => []
  }
})

const { handleLogout } = useAuth()
</script>

<style scoped>
/* All layout styles are in global style.css */
</style>
