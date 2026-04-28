<template>
  <ProfilePage 
    :layoutComponent="StudentLayout" 
    roleName="Học sinh"
    activeMenu="profile"
  />
</template>

<script setup>
import ProfilePage from '../../components/shared/ProfilePage.vue'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
</script>

<style scoped>
/* All logic moved to shared ProfilePage component */
</style>
