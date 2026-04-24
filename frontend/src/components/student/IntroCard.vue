<template>
  <div class="intro-card">
    <div class="intro-number">{{ number }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  number: {
    type: String,
    required: true
  },
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
.intro-card {
  background: white;
  padding: 35px 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.intro-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.intro-number {
  font-size: 48px;
  font-weight: 800;
  color: #eff6ff;
  position: absolute;
  top: 20px;
  right: 20px;
  line-height: 1;
}

.intro-card h3 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.intro-card p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}
</style>
