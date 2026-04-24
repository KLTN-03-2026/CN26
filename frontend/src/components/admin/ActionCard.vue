<template>
  <div class="action-card">
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <button class="btn-action" @click="$emit('click')">
      {{ buttonText }} →
    </button>
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
  },
  buttonText: {
    type: String,
    required: true
  }
})

defineEmits(['click'])
</script>

<style scoped>
.action-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.action-card h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.action-card p {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.btn-action {
  padding: 10px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
}
</style>
