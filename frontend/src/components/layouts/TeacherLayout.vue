<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Giáo viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/teacher')" :class="['menu-item', { active: activeMenu === 'home' }]">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/exams')" :class="['menu-item', { active: activeMenu === 'exams' }]">
          Đề thi
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/questions')" :class="['menu-item', { active: activeMenu === 'questions' }]">
          Câu hỏi
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/profile')" :class="['menu-item', { active: activeMenu === 'profile' }]">
          Cài đặt
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
