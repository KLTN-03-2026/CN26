<template>
  <div class="question-card">
    <div class="question-header">
      <span :class="['level-badge', `level-${question.level}`]">{{ getLevelText(question.level) }}</span>
      <div class="question-actions">
        <button class="btn-edit" @click="$emit('edit', question)">Sửa</button>
        <button class="btn-delete" @click="$emit('delete', question.id)">Xóa</button>
      </div>
    </div>
    <div class="question-content">
      <p class="question-text">
        <MathText :text="question.content" :auto-wrap="true" />
      </p>
      <div class="options-list">
        <div class="option-item">
          <span class="option-label">A</span>
          <MathText :text="question.optionA" :auto-wrap="true" />
        </div>
        <div class="option-item">
          <span class="option-label">B</span>
          <MathText :text="question.optionB" :auto-wrap="true" />
        </div>
        <div class="option-item">
          <span class="option-label">C</span>
          <MathText :text="question.optionC" :auto-wrap="true" />
        </div>
        <div class="option-item">
          <span class="option-label">D</span>
          <MathText :text="question.optionD" :auto-wrap="true" />
        </div>
      </div>
      <div class="correct-answer">
        Đáp án đúng: <strong>{{ question.correctAnswer }}</strong>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useFormatters } from '../../composables/useFormatters'
import MathText from '../common/MathText.vue'

defineProps({
  question: {
    type: Object,
    required: true
  }
})

defineEmits(['edit', 'delete'])

const { getLevelText } = useFormatters()
</script>

<style scoped>
.question-card { background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); overflow: hidden; transition: all 0.3s; }
.question-card:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.12); }
.question-header { padding: 15px 20px; background: #f9fafb; border-bottom: 1px solid #e5e7eb; display: flex; justify-content: space-between; align-items: center; }
.question-actions { display: flex; gap: 8px; }
.btn-edit { padding: 6px 16px; background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; }
.btn-delete { padding: 6px 16px; background: #fee2e2; color: #dc2626; border: 1px solid #fecaca; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; }
.question-content { padding: 20px; }
.question-text { font-size: 15px; color: #1f2937; font-weight: 600; margin: 0 0 15px 0; line-height: 1.6; }
.options-list { margin-bottom: 15px; }
.option-item { display: flex; align-items: flex-start; gap: 10px; padding: 10px; background: #f9fafb; border-radius: 6px; margin-bottom: 8px; font-size: 14px; color: #374151; }
.option-label { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; background: #1e40af; color: white; border-radius: 4px; font-weight: 700; font-size: 12px; flex-shrink: 0; }
.correct-answer { padding: 10px 15px; background: #d1fae5; border-left: 3px solid #10b981; border-radius: 6px; color: #065f46; font-size: 14px; font-weight: 500; }
.correct-answer strong { font-weight: 700; }
.level-badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.level-badge.level-easy { background: #d1fae5; color: #065f46; }
.level-badge.level-medium { background: #fef3c7; color: #92400e; }
.level-badge.level-hard { background: #fee2e2; color: #991b1b; }
</style>
