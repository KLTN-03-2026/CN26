<template>
  <div class="tip-card">
    <h4>{{ title }}</h4>
    <p>{{ description }}</p>
    <button class="btn-tip" @click="$emit('action-click')">{{ buttonText }}</button>
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

defineEmits(['action-click'])
</script>

<style scoped>
.tip-card {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  color: white;
  padding: 28px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.tip-card h4 {
  font-size: 19px;
  margin: 0 0 14px 0;
  font-weight: 700;
}

.tip-card p {
  font-size: 14px;
  line-height: 1.7;
  margin: 0 0 18px 0;
}

.btn-tip {
  width: 100%;
  padding: 12px;
  background: white;
  color: #1e40af;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-tip:hover {
  transform: translateY(-2px);
}
</style>
