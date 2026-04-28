<template>
  <AppLayout
    roleLabel="Giáo viên"
    :menuItems="menuItems"
    :activeMenu="activeMenu"
  >
    <slot></slot>
  </AppLayout>
</template>

<script setup>
import AppLayout from './AppLayout.vue'

defineProps({
  activeMenu: {
    type: String,
    default: 'home'
  }
})

const menuItems = [
  { path: '/teacher', label: 'Trang chủ', key: 'home' },
  { path: '/teacher/exams', label: 'Đề thi', key: 'exams' },
  { path: '/teacher/questions', label: 'Câu hỏi', key: 'questions' },
  { path: '/teacher/profile', label: 'Cài đặt', key: 'profile' }
]
</script>

