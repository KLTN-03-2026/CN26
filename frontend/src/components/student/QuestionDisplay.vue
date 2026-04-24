<template>
  <div class="question-container">
    <div class="question-header">
      <h1>Câu {{ currentIndex + 1 }}/{{ totalQuestions }}</h1>
    </div>

    <div class="question-content">
      <div class="question-text">
        <MathText :text="question.content" :auto-wrap="true" />
      </div>
      
      <div class="options-list">
        <label v-for="option in ['A', 'B', 'C', 'D']" :key="option" 
               class="option-item"
               :class="{ 'selected': selectedAnswer === option }">
          <input type="radio" 
                 :name="'question-' + question.id"
                 :value="option"
                 :checked="selectedAnswer === option"
                 @change="$emit('select-answer', question.id, option)">
          <span class="option-letter">{{ option }}</span>
          <span class="option-text">
            <MathText :text="question['option' + option]" :auto-wrap="true" />
          </span>
        </label>
      </div>
    </div>

    <div class="navigation-buttons">
      <button class="btn-nav btn-prev" @click="$emit('previous')" :disabled="currentIndex === 0">
        ← Câu trước
      </button>
      <button v-if="currentIndex < totalQuestions - 1" 
              class="btn-nav btn-next" 
              @click="$emit('next')">
        Câu tiếp →
      </button>
    </div>
  </div>
</template>

<script setup>
import MathText from '../common/MathText.vue'

defineProps({
  question: {
    type: Object,
    required: true
  },
  currentIndex: {
    type: Number,
    required: true
  },
  totalQuestions: {
    type: Number,
    required: true
  },
  selectedAnswer: String
})

defineEmits(['select-answer', 'previous', 'next'])
</script>

<style scoped>
.question-container { max-width: 900px; margin: 0 auto; padding: 40px; }
.question-header { margin-bottom: 30px; }
.question-header h1 { font-size: 24px; color: #1f2937; margin: 0; font-weight: 700; }
.question-content { background: white; padding: 40px; border-radius: 16px; box-shadow: 0 4px 16px rgba(0,0,0,0.08); margin-bottom: 30px; }
.question-text { font-size: 18px; color: #1f2937; font-weight: 600; margin-bottom: 30px; line-height: 1.8; }
.options-list { display: flex; flex-direction: column; gap: 15px; }
.option-item { display: flex; align-items: center; gap: 15px; padding: 20px; background: #f9fafb; border: 2px solid #e5e7eb; border-radius: 12px; cursor: pointer; transition: all 0.3s; }
.option-item:hover { background: #f3f4f6; border-color: #cbd5e1; }
.option-item.selected { background: #eff6ff; border-color: #3b82f6; }
.option-item input[type="radio"] { width: 20px; height: 20px; cursor: pointer; }
.option-letter { display: inline-flex; align-items: center; justify-content: center; width: 36px; height: 36px; background: #1e40af; color: white; border-radius: 8px; font-weight: 700; font-size: 16px; flex-shrink: 0; }
.option-text { flex: 1; font-size: 16px; color: #374151; line-height: 1.6; }
.navigation-buttons { display: flex; justify-content: space-between; gap: 15px; }
.btn-nav { padding: 14px 28px; border: none; border-radius: 10px; font-weight: 700; font-size: 15px; cursor: pointer; transition: all 0.3s; }
.btn-prev { background: #f3f4f6; color: #374151; }
.btn-prev:hover:not(:disabled) { background: #e5e7eb; }
.btn-prev:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-next { background: #1e40af; color: white; }
.btn-next:hover { background: #1e3a8a; }
</style>
