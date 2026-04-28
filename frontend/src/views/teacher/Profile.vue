<template>
  <ProfilePage 
    :layoutComponent="TeacherLayout" 
    roleName="Giáo viên"
    activeMenu="profile"
  />
</template>

<script setup>
import ProfilePage from '../../components/shared/ProfilePage.vue'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'
</script>

<style scoped>
/* All logic moved to shared ProfilePage component */
</style>
