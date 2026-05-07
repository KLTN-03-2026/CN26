<template>
  <!-- Bottom Navigation Bar -->
  <div class="exam-bottom-nav">
    <button 
      class="nav-btn" 
      @click="$emit('previous')"
      :disabled="currentIndex === 0"
    >
      ◀ Trước
    </button>
    
    <div class="question-indicator">
      Câu {{ currentIndex + 1 }}/{{ totalQuestions }}
    </div>
    
    <button 
      class="nav-btn nav-btn-grid" 
      @click="toggleSheet"
    >
      📋 Danh sách
    </button>
    
    <button 
      class="nav-btn" 
      @click="$emit('next')"
      :disabled="currentIndex === totalQuestions - 1"
    >
      Sau ▶
    </button>
  </div>

  <!-- Floating Timer -->
  <div class="floating-timer">
    <div class="timer-icon">⏱️</div>
    <div class="timer-text">{{ formattedTime }}</div>
  </div>

  <!-- Bottom Sheet Overlay -->
  <Transition name="sheet-fade">
    <div 
      v-if="isSheetOpen" 
      class="sheet-backdrop" 
      @click="closeSheet"
    ></div>
  </Transition>

  <!-- Bottom Sheet -->
  <Transition name="sheet-slide">
    <div v-if="isSheetOpen" class="bottom-sheet">
      <div class="sheet-header">
        <div class="sheet-title">
          <h3>{{ examName }}</h3>
          <p>Đã trả lời: {{ answeredCount }}/{{ totalQuestions }}</p>
        </div>
        <button class="sheet-close" @click="closeSheet">✕</button>
      </div>
      
      <div class="sheet-content">
        <div class="question-grid-mobile">
          <button 
            v-for="(question, index) in questions" 
            :key="question.id"
            @click="goToQuestion(index)"
            :class="['question-btn-mobile', { 
              'answered': answers[question.id], 
              'current': index === currentIndex 
            }]"
          >
            {{ index + 1 }}
          </button>
        </div>
      </div>

      <div class="sheet-footer">
        <button class="btn-submit-mobile" @click="$emit('submit')">
          Nộp bài
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  examName: String,
  timeRemaining: Number,
  questions: Array,
  answers: Object,
  currentIndex: Number
})

const emit = defineEmits(['go-to-question', 'submit', 'previous', 'next'])

const isSheetOpen = ref(false)

const answeredCount = computed(() => Object.keys(props.answers).length)
const totalQuestions = computed(() => props.questions.length)

const formattedTime = computed(() => {
  const minutes = Math.floor(props.timeRemaining / 60)
  const seconds = props.timeRemaining % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

const toggleSheet = () => {
  isSheetOpen.value = !isSheetOpen.value
  if (isSheetOpen.value) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
}

const closeSheet = () => {
  isSheetOpen.value = false
  document.body.style.overflow = ''
}

const goToQuestion = (index) => {
  emit('go-to-question', index)
  closeSheet()
}
</script>

<style scoped>
/* Bottom Navigation Bar */
.exam-bottom-nav {
  display: none; /* Hidden on desktop */
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 2px solid #e5e7eb;
  padding: 12px 15px;
  gap: 10px;
  z-index: 998;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
}

.nav-btn {
  flex: 1;
  padding: 12px 16px;
  background: #f3f4f6;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 44px;
}

.nav-btn:hover:not(:disabled) {
  background: #e5e7eb;
  border-color: #1e40af;
}

.nav-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.nav-btn-grid {
  background: #1e40af;
  color: white;
  border-color: #1e40af;
}

.nav-btn-grid:hover {
  background: #1e3a8a;
}

.question-indicator {
  flex: 1;
  text-align: center;
  font-weight: 700;
  font-size: 15px;
  color: #1f2937;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Floating Timer */
.floating-timer {
  display: none; /* Hidden on desktop */
  position: fixed;
  top: 70px;
  right: 15px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  padding: 10px 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
  z-index: 997;
  display: flex;
  align-items: center;
  gap: 8px;
}

.timer-icon {
  font-size: 18px;
}

.timer-text {
  font-size: 16px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
}

/* Bottom Sheet */
.sheet-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.bottom-sheet {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  max-height: 70vh;
  background: white;
  border-radius: 20px 20px 0 0;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.2);
}

.sheet-header {
  padding: 20px;
  border-bottom: 2px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sheet-title h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 5px 0;
  font-weight: 700;
}

.sheet-title p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.sheet-close {
  width: 36px;
  height: 36px;
  background: #f3f4f6;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.sheet-close:hover {
  background: #e5e7eb;
  color: #1f2937;
}

.sheet-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.question-grid-mobile {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.question-btn-mobile {
  padding: 14px;
  background: #f3f4f6;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 44px;
}

.question-btn-mobile:active {
  transform: scale(0.95);
}

.question-btn-mobile.answered {
  background: #d1fae5;
  border-color: #10b981;
  color: #065f46;
}

.question-btn-mobile.current {
  background: #1e40af;
  border-color: #1e40af;
  color: white;
}

.sheet-footer {
  padding: 15px 20px;
  border-top: 2px solid #e5e7eb;
}

.btn-submit-mobile {
  width: 100%;
  padding: 14px;
  background: #dc2626;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 48px;
}

.btn-submit-mobile:active {
  transform: scale(0.98);
  background: #b91c1c;
}

/* Animations */
.sheet-fade-enter-active,
.sheet-fade-leave-active {
  transition: opacity 0.3s ease;
}

.sheet-fade-enter-from,
.sheet-fade-leave-to {
  opacity: 0;
}

.sheet-slide-enter-active,
.sheet-slide-leave-active {
  transition: transform 0.3s ease;
}

.sheet-slide-enter-from,
.sheet-slide-leave-to {
  transform: translateY(100%);
}

/* Show on mobile only */
@media (max-width: 1024px) {
  .exam-bottom-nav {
    display: flex;
  }
  
  .floating-timer {
    display: flex;
  }
}

/* Mobile small adjustments */
@media (max-width: 480px) {
  .nav-btn {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .question-grid-mobile {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .floating-timer {
    top: 65px;
    right: 10px;
    padding: 8px 12px;
  }
  
  .timer-text {
    font-size: 14px;
  }
}
</style>
