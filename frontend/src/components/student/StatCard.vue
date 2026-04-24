<template>
  <div class="stat-card">
    <div class="stat-header">
      <span :class="['stat-badge', badgeType]">{{ badgeText }}</span>
    </div>
    <div class="stat-value">{{ value }}</div>
    <div class="stat-label">{{ label }}</div>
  </div>
</template>

<script setup>
defineProps({
  value: {
    type: [Number, String],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  badgeText: {
    type: String,
    required: true
  },
  badgeType: {
    type: String,
    default: '',
    validator: (value) => ['', 'success', 'warning'].includes(value)
  }
})
</script>

<style scoped>
.stat-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.stat-header {
  margin-bottom: 18px;
}

.stat-badge {
  display: inline-block;
  padding: 6px 14px;
  background: #eff6ff;
  color: #1e40af;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.stat-badge.success {
  background: #d1fae5;
  color: #065f46;
}

.stat-badge.warning {
  background: #fef3c7;
  color: #92400e;
}

.stat-value {
  font-size: 42px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}
</style>
