<template>
  <StudentLayout activeMenu="results">
    <header class="top-header">
      <div class="header-left">
        <h1>Kết Quả Thi Của Tôi</h1>
      </div>
    </header>

    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="results.length === 0"
      message="Bạn chưa có kết quả thi nào"
      actionText="Xem danh sách đề thi"
      @action="$router.push('/student/exams')"
    />

    <div v-else class="results-container">
      <div v-for="result in results" :key="result.id" class="result-card">
        <div class="result-header">
          <h3>{{ result.examName }}</h3>
          <span :class="['score-badge', getScoreClass(result.score)]">
            {{ result.score }}%
          </span>
        </div>
        <div class="result-body">
          <div class="result-stats">
            <div class="stat-item">
              <span class="stat-label">Số câu đúng</span>
              <span class="stat-value">{{ result.totalCorrect }}/{{ result.totalQuestions }}</span>
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
        <div class="result-footer">
          <button class="btn-view-detail" @click="viewDetail(result.id)">
            Xem chi tiết
          </button>
        </div>
      </div>
    </div>
  </StudentLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import resultService from '../../services/resultService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

const router = useRouter()
const { getScoreClass, formatDateTime, formatTimeSpent } = useFormatters()

const results = ref([])
const loading = ref(false)

onMounted(() => {
  loadResults()
})

const loadResults = async () => {
  try {
    loading.value = true
    const response = await resultService.getMyResults()
    if (response.success) {
      results.value = response.data.filter(r => r.status === 'graded')
    }
  } catch (error) {
    console.error('Error loading results:', error)
  } finally {
    loading.value = false
  }
}

const viewDetail = (resultId) => {
  router.push(`/student/result/${resultId}`)
}
</script>

<style scoped>
.results-container {
  display: grid;
  gap: 20px;
}

.result-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: all 0.3s;
}

.result-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.result-header {
  padding: 20px 25px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header h3 {
  font-size: 18px;
  color: white;
  margin: 0;
  font-weight: 700;
}

.score-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 800;
  font-size: 16px;
  color: white;
}

.score-badge.excellent {
  background: #10b981;
}

.score-badge.good {
  background: #3b82f6;
}

.score-badge.average {
  background: #f59e0b;
}

.score-badge.poor {
  background: #ef4444;
}

.result-body {
  padding: 25px;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.stat-value {
  font-size: 18px;
  color: #1f2937;
  font-weight: 700;
}

.result-footer {
  padding: 15px 25px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.btn-view-detail {
  width: 100%;
  padding: 12px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view-detail:hover {
  background: #1e3a8a;
}

@media (max-width: 768px) {
  .result-stats {
    grid-template-columns: 1fr;
  }
}
</style>
