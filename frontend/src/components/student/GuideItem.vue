<template>
  <div class="guide-item">
    <div class="guide-step">{{ step }}</div>
    <div class="guide-content">
      <h4>{{ title }}</h4>
      <p>{{ description }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  step: {
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
.guide-item {
  display: flex;
  gap: 20px;
  padding: 25px;
  background: #f9fafb;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.guide-item:hover {
  background: white;
  border-color: #1e40af;
}

.guide-step {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.guide-content {
  flex: 1;
}

.guide-content h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.guide-content p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}
</style>
