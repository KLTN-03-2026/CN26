<template>
  <AppLayout
    roleLabel="Học sinh"
    :menuItems="menuItems"
    :activeMenu="activeMenu"
    :footerButtons="footerButtons"
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
  { path: '/student', label: 'Trang chủ', key: 'home' },
  { path: '/student/exams', label: 'Đề thi', key: 'exams' },
  { path: '/student/results', label: 'Kết quả', key: 'results' },
  { path: '/student/profile', label: 'Cài đặt', key: 'profile' }
]

const footerButtons = [
  { 
    label: 'Trở thành giáo viên', 
    path: '/student/teacher-request',
    class: 'btn-help',
    style: ''
  }
]
</script>

