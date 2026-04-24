<template>
  <div class="user-card">
    <div class="user-avatar">{{ user?.fullName?.charAt(0) }}</div>
    <h4>{{ user?.fullName }}</h4>
    <p class="user-email">{{ user?.email }}</p>
    <div class="user-stats-mini">
      <div class="stat-mini">
        <div class="stat-mini-value">{{ completedExams }}</div>
        <div class="stat-mini-label">Bài đã thi</div>
      </div>
      <div class="stat-mini">
        <div class="stat-mini-value">{{ averageScore }}%</div>
        <div class="stat-mini-label">Điểm TB</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  user: {
    type: Object,
    required: true
  },
  completedExams: {
    type: Number,
    required: true
  },
  averageScore: {
    type: Number,
    required: true
  }
})
</script>

<style scoped>
.user-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  text-align: center;
}

.user-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  margin: 0 auto 15px;
}

.user-card h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 5px 0;
  font-weight: 700;
}

.user-email {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 20px 0;
}

.user-stats-mini {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.stat-mini {
  text-align: center;
}

.stat-mini-value {
  font-size: 24px;
  font-weight: 800;
  color: #1e40af;
  margin-bottom: 5px;
}

.stat-mini-label {
  font-size: 12px;
  color: #6b7280;
}
</style>
