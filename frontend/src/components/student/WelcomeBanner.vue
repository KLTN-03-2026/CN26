<template>
  <div class="welcome-section">
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.welcome-section {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  padding: 50px 40px;
  border-radius: 16px;
  margin-bottom: 30px;
  color: white;
  text-align: center;
}

.welcome-section h2 {
  font-size: 32px;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.welcome-section p {
  font-size: 18px;
  margin: 0;
  opacity: 0.95;
}
</style>
