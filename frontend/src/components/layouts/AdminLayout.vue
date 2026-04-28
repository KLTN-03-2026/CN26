<template>
  <AppLayout
    roleLabel="Quản trị viên"
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
  { path: '/admin', label: 'Trang chủ', key: 'home' },
  { path: '/admin/users', label: 'Người dùng', key: 'users' },
  { path: '/admin/teacher-requests', label: 'Yêu cầu GV', key: 'requests' }
]
</script>

