<template>
  <div class="verify-layout">
    <div class="verify-container">
      <div class="verify-card">
        <div v-if="loading" class="verify-loading">
          <div class="spinner"></div>
          <h2>Đang xác thực tài khoản...</h2>
          <p>Vui lòng chờ trong giây lát</p>
        </div>

        <div v-else-if="success" class="verify-success">
          <div class="success-icon">✓</div>
          <h2>Xác thực thành công!</h2>
          <p>Tài khoản của bạn đã được kích hoạt. Bạn có thể đăng nhập ngay bây giờ.</p>
          <button class="btn-login" @click="goToLogin">Đăng nhập</button>
        </div>

        <div v-else-if="error" class="verify-error">
          <div class="error-icon">✗</div>
          <h2>Xác thực thất bại</h2>
          <p class="error-message">{{ error }}</p>
          <div class="error-actions">
            <button class="btn-retry" @click="retryVerification">Thử lại</button>
            <button class="btn-back" @click="goToLogin">Quay lại đăng nhập</button>
          </div>
        </div>
      </div>

      <div class="verify-footer">
        <p>Hệ Thống Thi Trực Tuyến</p>
        <span>© 2026 All rights reserved</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../../services/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const success = ref(false)
const error = ref('')

onMounted(() => {
  verifyEmail()
})

const verifyEmail = async () => {
  try {
    loading.value = true
    success.value = false
    error.value = ''

    const token = route.query.token

    if (!token) {
      error.value = 'Token xác thực không hợp lệ hoặc đã hết hạn'
      loading.value = false
      return
    }

    const response = await api.get(`/auth/verify?token=${token}`)

    if (response.success) {
      success.value = true
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Xác thực thất bại. Token có thể đã hết hạn hoặc không hợp lệ.'
  } finally {
    loading.value = false
  }
}

const retryVerification = () => {
  verifyEmail()
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.verify-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.verify-container {
  max-width: 500px;
  width: 100%;
}

.verify-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  padding: 60px 50px;
  text-align: center;
}

.verify-loading,
.verify-success,
.verify-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 5px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.success-icon,
.error-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50px;
  font-weight: 700;
}

.success-icon {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  border: 5px solid #10b981;
  color: #065f46;
}

.error-icon {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: 5px solid #ef4444;
  color: #991b1b;
}

.verify-card h2 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.verify-card p {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}

.error-message {
  color: #991b1b;
  background: #fee2e2;
  padding: 15px 20px;
  border-radius: 10px;
  border: 1px solid #fecaca;
  font-size: 15px;
}

.btn-login {
  padding: 14px 40px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn-login:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.4);
}

.error-actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.btn-retry,
.btn-back {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-retry {
  background: #1e40af;
  color: white;
}

.btn-retry:hover {
  background: #1e3a8a;
}

.btn-back {
  background: #f3f4f6;
  color: #374151;
}

.btn-back:hover {
  background: #e5e7eb;
}

.verify-footer {
  text-align: center;
  margin-top: 30px;
  color: white;
}

.verify-footer p {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 5px 0;
}

.verify-footer span {
  font-size: 13px;
  opacity: 0.8;
}

@media (max-width: 600px) {
  .verify-card {
    padding: 40px 30px;
  }
  .error-actions {
    flex-direction: column;
    width: 100%;
  }
  .btn-retry,
  .btn-back {
    width: 100%;
  }
}
</style>
