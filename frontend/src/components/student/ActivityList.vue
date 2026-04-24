<template>
  <div class="activity-card">
    <h4>Hoạt Động Gần Đây</h4>
    <div v-if="loading" class="loading-state-small">
      <div class="spinner-small"></div>
    </div>
    <div v-else-if="results.length === 0" class="empty-state-small">
      <p>Chưa có hoạt động nào</p>
    </div>
    <div v-else class="activity-list">
      <div v-for="result in results.slice(0, 3)" :key="result.id" class="activity-item">
        <div class="activity-icon">
          <span :class="getActivityClass(result.score)">•</span>
        </div>
        <div class="activity-info">
          <p class="activity-title">{{ result.examName }}</p>
          <p class="activity-time">{{ formatTimeAgo(result.submitTime) }}</p>
        </div>
        <div class="activity-score">
          <span :class="getScoreClass(result.score)">{{ result.score }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useFormatters } from '../../composables/useFormatters'

defineProps({
  results: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const { getScoreClass, formatTimeAgo } = useFormatters()

const getActivityClass = (score) => {
  if (score >= 80) return 'activity-success'
  if (score >= 50) return 'activity-warning'
  return 'activity-danger'
}
</script>

<style scoped>
.activity-card {
  background: white;
  padding: 28px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.activity-card h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 18px 0;
  font-weight: 700;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f9fafb;
  border-radius: 10px;
}

.activity-item:hover {
  background: #f3f4f6;
}

.activity-icon span {
  font-size: 28px;
}

.activity-success {
  color: #10b981;
}

.activity-warning {
  color: #f59e0b;
}

.activity-danger {
  color: #ef4444;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.activity-time {
  font-size: 12px;
  color: #6b7280;
  margin: 0;
}

.activity-score {
  font-weight: 800;
  font-size: 16px;
}

.loading-state-small, .empty-state-small {
  text-align: center;
  padding: 25px;
  color: #6b7280;
  font-size: 14px;
}

.spinner-small {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
