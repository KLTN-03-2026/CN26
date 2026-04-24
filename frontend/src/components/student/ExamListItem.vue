<template>
  <div class="exam-item">
    <div class="exam-icon">
      <div class="icon-placeholder">{{ exam.subject.charAt(0) }}</div>
    </div>
    <div class="exam-info">
      <h4>{{ exam.name }}</h4>
      <p class="exam-meta">{{ exam.subject }} • {{ exam.totalQuestions }} câu • {{ exam.duration }} phút</p>
    </div>
    <div class="exam-actions">
      <button class="btn-details" @click="$emit('start-exam', exam.id)">Bắt đầu</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  exam: {
    type: Object,
    required: true
  }
})

defineEmits(['start-exam'])
</script>

<style scoped>
.exam-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 22px;
  background: #f9fafb;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.exam-item:hover {
  background: white;
  border-color: #1e40af;
  transform: translateY(-2px);
}

.exam-icon {
  width: 56px;
  height: 56px;
}

.icon-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  color: #1e40af;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
}

.exam-info {
  flex: 1;
}

.exam-info h4 {
  font-size: 17px;
  color: #1f2937;
  margin: 0 0 6px 0;
  font-weight: 700;
}

.exam-meta {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.btn-details {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-details:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
}
</style>
