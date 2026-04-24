<template>
  <div class="exam-card">
    <div class="exam-header">
      <h3>{{ exam.name }}</h3>
      <span :class="['status-badge', getExamStatusClass(exam)]">
        {{ getExamStatusText(exam) }}
      </span>
    </div>
    <p class="exam-description">{{ exam.description }}</p>
    <div class="exam-meta">
      <span class="meta-item">{{ exam.totalQuestions }} câu</span>
      <span class="meta-item">{{ exam.duration }} phút</span>
      <span class="meta-item">{{ exam.subject }}</span>
    </div>
    <div v-if="exam.startTime || exam.endTime" class="exam-dates">
      <div v-if="exam.startTime" class="date-item">
        <span class="date-label">Bắt đầu:</span>
        <span class="date-value">{{ formatDateTime(exam.startTime) }}</span>
      </div>
      <div v-if="exam.endTime" class="date-item">
        <span class="date-label">Kết thúc:</span>
        <span class="date-value">{{ formatDateTime(exam.endTime) }}</span>
      </div>
    </div>
    <div class="exam-actions">
      <button class="btn-action btn-stats" @click="$emit('view-stats', exam.id)">
        Thống kê
      </button>
      <button class="btn-action btn-toggle" @click="$emit('toggle-status', exam)">
        {{ exam.isActive ? 'Đóng' : 'Mở' }}
      </button>
      <button class="btn-edit" @click="$emit('edit', exam)">
        Sửa
      </button>
      <button class="btn-delete" @click="$emit('delete', exam.id)">
        Xóa
      </button>
    </div>
  </div>
</template>

<script setup>
import { useFormatters } from '../../composables/useFormatters'

defineProps({
  exam: {
    type: Object,
    required: true
  }
})

defineEmits(['view-stats', 'toggle-status', 'edit', 'delete'])

const { formatDateTime } = useFormatters()

const getExamStatusClass = (exam) => {
  if (!exam.isActive) return 'inactive'
  const now = new Date()
  const start = exam.startTime ? new Date(exam.startTime) : null
  const end = exam.endTime ? new Date(exam.endTime) : null
  
  if (start && now < start) return 'upcoming'
  if (end && now > end) return 'ended'
  return 'active'
}

const getExamStatusText = (exam) => {
  if (!exam.isActive) return 'Đã đóng'
  const now = new Date()
  const start = exam.startTime ? new Date(exam.startTime) : null
  const end = exam.endTime ? new Date(exam.endTime) : null
  
  if (start && now < start) return 'Sắp diễn ra'
  if (end && now > end) return 'Đã kết thúc'
  return 'Đang mở'
}
</script>

<style scoped>
.exam-card { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: all 0.3s; }
.exam-card:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.12); }
.exam-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.exam-header h3 { font-size: 18px; color: #1f2937; margin: 0; font-weight: 700; }
.status-badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.status-badge.active { background: #d1fae5; color: #065f46; }
.status-badge.inactive { background: #f3f4f6; color: #6b7280; }
.status-badge.upcoming { background: #dbeafe; color: #1e40af; }
.status-badge.ended { background: #fee2e2; color: #991b1b; }
.exam-description { font-size: 14px; color: #6b7280; margin: 0 0 15px 0; line-height: 1.6; }
.exam-meta { display: flex; gap: 15px; margin-bottom: 15px; }
.meta-item { font-size: 13px; color: #374151; font-weight: 500; }
.exam-dates { background: #f9fafb; padding: 12px; border-radius: 8px; margin-bottom: 15px; }
.date-item { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 6px; }
.date-item:last-child { margin-bottom: 0; }
.date-label { color: #6b7280; }
.date-value { color: #1f2937; font-weight: 600; }
.exam-actions { display: flex; gap: 8px; }
.btn-action { padding: 8px 16px; border: none; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; transition: all 0.3s; }
.btn-stats { background: #eff6ff; color: #1e40af; }
.btn-toggle { background: #fef3c7; color: #92400e; }
.btn-edit { padding: 8px 16px; background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; }
.btn-delete { padding: 8px 16px; background: #fee2e2; color: #dc2626; border: 1px solid #fecaca; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; }
</style>
