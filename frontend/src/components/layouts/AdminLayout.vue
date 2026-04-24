<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Quản trị viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/admin')" :class="['menu-item', { active: activeMenu === 'home' }]">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/admin/users')" :class="['menu-item', { active: activeMenu === 'users' }]">
          Người dùng
        </a>
        <a href="#" @click.prevent="$router.push('/admin/teacher-requests')" :class="['menu-item', { active: activeMenu === 'requests' }]">
          Yêu cầu GV
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-logout" @click="handleLogout">Đăng xuất</button>
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
  activeMenu: {
    type: String,
    default: 'home'
  }
})

const { handleLogout } = useAuth()
</script>
