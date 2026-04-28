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
            {{ (result.score / 10).toFixed(1) }}
          </span>
        </div>
        <div class="result-body">
          <div class="score-circle-container">
            <div class="score-circle" :class="getScoreClass(result.score)">
              <span class="score-text">{{ result.totalCorrect }}/{{ result.totalQuestions }}</span>
            </div>
          </div>
          <div class="result-stats">
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
</script>

<style scoped>
.results-container {
  display: grid;
  gap: 16px;
  max-width: 1000px;
  margin: 0 auto;
}

.result-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow: hidden;
  transition: all 0.2s ease;
  border: 1px solid #e5e7eb;
}

.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  border-color: #3b82f6;
}

.result-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.result-header h3 {
  font-size: 16px;
  color: white;
  margin: 0;
  font-weight: 600;
  flex: 1;
  line-height: 1.4;
}

.score-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 15px;
  color: white;
  white-space: nowrap;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.score-badge.excellent {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.score-badge.good {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.score-badge.average {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.score-badge.poor {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.result-body {
  padding: 24px 20px;
  display: flex;
  gap: 24px;
  align-items: center;
}

.score-circle-container {
  flex-shrink: 0;
}

.score-circle {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 6px solid;
  position: relative;
}

.score-circle.excellent {
  border-color: #10b981;
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.score-circle.good {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
}

.score-circle.average {
  border-color: #f59e0b;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
}

.score-circle.poor {
  border-color: #ef4444;
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
}

.score-text {
  font-size: 28px;
  font-weight: 800;
  color: #1f2937;
}

.result-stats {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.stat-item:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 18px;
  color: #1f2937;
  font-weight: 700;
  line-height: 1.2;
}

.stat-highlight {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border: 2px solid #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.stat-highlight:hover {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  border-color: #2563eb;
}

.stat-highlight .stat-label {
  color: #1e40af;
  font-weight: 600;
}

.stat-highlight .stat-value {
  font-size: 32px;
  color: #1e40af;
  font-weight: 800;
}

@media (max-width: 768px) {
  .results-container {
    gap: 12px;
  }

  .result-header {
    padding: 14px 16px;
  }

  .result-header h3 {
    font-size: 15px;
  }

  .score-badge {
    padding: 5px 12px;
    font-size: 14px;
  }

  .result-body {
    flex-direction: column;
    padding: 20px 16px;
    gap: 20px;
  }

  .score-circle {
    width: 100px;
    height: 100px;
    border-width: 5px;
  }

  .score-text {
    font-size: 24px;
  }
  
  .result-stats {
    grid-template-columns: 1fr;
    width: 100%;
    gap: 12px;
  }

  .stat-item {
    padding: 14px;
  }

  .stat-value {
    font-size: 20px;
  }

  .stat-highlight .stat-value {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .result-stats {
    gap: 10px;
  }

  .stat-item {
    padding: 12px;
  }

  .stat-label {
    font-size: 11px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-highlight .stat-value {
    font-size: 24px;
  }
}
</style>
