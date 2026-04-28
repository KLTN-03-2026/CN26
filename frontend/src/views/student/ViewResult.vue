<template>
  <StudentLayout activeMenu="results">
    <LoadingSpinner v-if="loading" />
    <EmptyState 
      v-else-if="!result"
      message="Không tìm thấy kết quả"
      actionText="Quay lại danh sách"
      @action="$router.push('/student/results')"
    />
    <div v-else>
      <header class="top-header">
        <div class="header-left">
          <h1>{{ result.examName }}</h1>
        </div>
        <button class="btn-back" @click="$router.push('/student/results')">Quay lại</button>
      </header>

      <div class="summary-card">
        <div class="score-circle" :class="getScoreClass(result.score)">
          <span class="score-value">{{ result.totalCorrect }}/{{ result.totalQuestions }}</span>
        </div>
        <div class="summary-stats">
          <div class="stat-item stat-highlight">
            <span class="stat-label">Điểm số</span>
            <span class="stat-value">{{ (result.score / 10).toFixed(1) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Thời gian làm</span>
            <span class="stat-value">{{ formatTimeSpent(result.timeSpent) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Ngày thi</span>
            <span class="stat-value">{{ formatDateTime(result.submitTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </StudentLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import resultService from '../../services/resultService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

const router = useRouter()
const route = useRoute()
const { getScoreClass, formatDateTime, formatTimeSpent } = useFormatters()

const result = ref(null)
const loading = ref(false)

const loadResult = async () => {
  try {
    loading.value = true
    const response = await resultService.getResultDetail(route.params.id)
    if (response.success) result.value = response.data
  } catch (error) {
    console.error('Error loading result:', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadResult)
</script>

<style scoped>
.btn-back { padding: 10px 20px; background: #f3f4f6; color: #374151; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
.summary-card { background: white; padding: 40px; border-radius: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); display: flex; gap: 40px; align-items: center; }
.score-circle { width: 150px; height: 150px; border-radius: 50%; display: flex; align-items: center; justify-content: center; border: 8px solid; flex-shrink: 0; }
.score-circle.excellent { border-color: #10b981; background: #d1fae5; }
.score-circle.good { border-color: #3b82f6; background: #dbeafe; }
.score-circle.average { border-color: #f59e0b; background: #fef3c7; }
.score-circle.poor { border-color: #ef4444; background: #fee2e2; }
.score-value { font-size: 36px; font-weight: 800; color: #1f2937; }
.summary-stats { flex: 1; display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }
.stat-item { display: flex; flex-direction: column; gap: 8px; }
.stat-label { font-size: 14px; color: #6b7280; font-weight: 500; }
.stat-value { font-size: 24px; color: #1f2937; font-weight: 700; }
.stat-highlight { background: #eff6ff; padding: 20px; border-radius: 12px; border: 2px solid #3b82f6; }
.stat-highlight .stat-label { color: #1e40af; font-size: 15px; font-weight: 600; }
.stat-highlight .stat-value { font-size: 42px; color: #1e40af; font-weight: 800; }
@media (max-width: 768px) {
  .summary-card { flex-direction: column; }
  .summary-stats { grid-template-columns: 1fr; }
}
</style>
