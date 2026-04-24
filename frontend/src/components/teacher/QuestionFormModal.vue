<template>
  <AppModal :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" 
            :title="isEditing ? 'Sửa Câu Hỏi' : 'Tạo Câu Hỏi Mới'">
    <form @submit.prevent="$emit('submit', formData)">
      <div class="form-group">
        <label>Nội dung câu hỏi</label>
        <textarea v-model="formData.content" required rows="3"></textarea>
      </div>
      <div class="form-group">
        <label>Đáp án A</label>
        <input v-model="formData.optionA" required>
      </div>
      <div class="form-group">
        <label>Đáp án B</label>
        <input v-model="formData.optionB" required>
      </div>
      <div class="form-group">
        <label>Đáp án C</label>
        <input v-model="formData.optionC" required>
      </div>
      <div class="form-group">
        <label>Đáp án D</label>
        <input v-model="formData.optionD" required>
      </div>
      <div class="form-group">
        <label>Đáp án đúng</label>
        <select v-model="formData.correctAnswer" required>
          <option value="A">A</option>
          <option value="B">B</option>
          <option value="C">C</option>
          <option value="D">D</option>
        </select>
      </div>
      <div class="form-group">
        <label>Độ khó</label>
        <select v-model="formData.level" required>
          <option value="easy">Dễ</option>
          <option value="medium">Trung bình</option>
          <option value="hard">Khó</option>
        </select>
      </div>
      <div class="modal-actions">
        <button type="button" class="btn-cancel" @click="$emit('update:modelValue', false)">Hủy</button>
        <button type="submit" class="btn-save">Lưu</button>
      </div>
    </form>
  </AppModal>
</template>

<script setup>
import { watch, ref } from 'vue'
import AppModal from '../common/AppModal.vue'

const props = defineProps({
  modelValue: Boolean,
  question: Object,
  isEditing: Boolean
})

defineEmits(['update:modelValue', 'submit'])

const formData = ref({
  content: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: 'A',
  level: 'medium'
})

watch(() => props.question, (newVal) => {
  if (newVal) {
    formData.value = { ...newVal }
  } else {
    formData.value = {
      content: '',
      optionA: '',
      optionB: '',
      optionC: '',
      optionD: '',
      correctAnswer: 'A',
      level: 'medium'
    }
  }
}, { immediate: true })
</script>

<style scoped>
.modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 25px; }
.btn-cancel { padding: 12px 24px; background: #f3f4f6; color: #374151; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
.btn-save { padding: 12px 24px; background: #1e40af; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
</style>
