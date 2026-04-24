<template>
  <aside class="exam-sidebar">
    <div class="sidebar-header">
      <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
      <h2>{{ examName }}</h2>
      <div class="timer-box">
        <div class="timer-label">Thời gian còn lại</div>
        <div class="timer-value">{{ formattedTime }}</div>
      </div>
    </div>
    
    <div class="question-grid">
      <div class="grid-header">
        <span>Danh sách câu hỏi</span>
        <span class="answered-count">{{ answeredCount }}/{{ totalQuestions }}</span>
      </div>
      <div class="grid-container">
        <button v-for="(question, index) in questions" 
                :key="question.id"
                @click="$emit('go-to-question', index)"
                :class="['question-btn', { 
                  'answered': answers[question.id], 
                  'current': index === currentIndex 
                }]">
          {{ index + 1 }}
        </button>
      </div>
    </div>

    <div class="sidebar-footer">
      <button class="btn-submit-exam" @click="$emit('submit')">
        Nộp bài
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  examName: String,
  timeRemaining: Number,
  questions: Array,
  answers: Object,
  currentIndex: Number
})

defineEmits(['go-to-question', 'submit'])

const answeredCount = computed(() => Object.keys(props.answers).length)
const totalQuestions = computed(() => props.questions.length)

const formattedTime = computed(() => {
  const minutes = Math.floor(props.timeRemaining / 60)
  const seconds = props.timeRemaining % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})
</script>

<style scoped>
.exam-sidebar { width: 320px; background: white; border-right: 1px solid #e5e7eb; display: flex; flex-direction: column; height: 100vh; position: fixed; left: 0; top: 0; }
.sidebar-header { padding: 25px; border-bottom: 1px solid #e5e7eb; }
.sidebar-logo { height: 50px; margin-bottom: 15px; }
.sidebar-header h2 { font-size: 18px; color: #1f2937; margin: 0 0 20px 0; font-weight: 700; }
.timer-box { background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%); padding: 20px; border-radius: 12px; text-align: center; }
.timer-label { font-size: 13px; color: rgba(255,255,255,0.9); margin-bottom: 8px; }
.timer-value { font-size: 32px; color: white; font-weight: 800; font-family: 'Courier New', monospace; }
.question-grid { flex: 1; overflow-y: auto; padding: 20px; }
.grid-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; font-size: 14px; color: #6b7280; font-weight: 600; }
.answered-count { color: #1e40af; }
.grid-container { display: grid; grid-template-columns: repeat(5, 1fr); gap: 10px; }
.question-btn { padding: 12px; background: #f3f4f6; border: 2px solid #e5e7eb; border-radius: 8px; font-weight: 700; font-size: 14px; color: #6b7280; cursor: pointer; transition: all 0.3s; }
.question-btn:hover { background: #e5e7eb; }
.question-btn.answered { background: #d1fae5; border-color: #10b981; color: #065f46; }
.question-btn.current { background: #1e40af; border-color: #1e40af; color: white; }
.sidebar-footer { padding: 20px; border-top: 1px solid #e5e7eb; }
.btn-submit-exam { width: 100%; padding: 14px; background: #dc2626; color: white; border: none; border-radius: 10px; font-weight: 700; font-size: 15px; cursor: pointer; transition: all 0.3s; }
.btn-submit-exam:hover { background: #b91c1c; }
</style>
