<template>
  <div class="auth-layout">
    <div class="auth-container-small">
      <div class="resend-card">
        <div class="card-header">
          <h2>Gửi lại email xác thực</h2>
          <p>Nhập email của bạn để nhận lại link kích hoạt tài khoản</p>
        </div>

        <div v-if="success" class="alert alert-success">
          Email xác thực đã được gửi! Vui lòng kiểm tra hộp thư của bạn.
        </div>

        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <form @submit.prevent="handleResend" class="resend-form">
          <div class="form-group">
            <label>Email</label>
            <input 
              type="email" 
              v-model="email" 
              placeholder="example@gmail.com" 
              required
            />
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? 'Đang gửi...' : 'Gửi email xác thực' }}
          </button>
        </form>

        <div class="form-footer">
          <router-link to="/login" class="link">← Quay lại đăng nhập</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../../services/api'

const email = ref('')
const loading = ref(false)
const error = ref('')
const success = ref(false)

const handleResend = async () => {
  try {
    loading.value = true
    error.value = ''
    success.value = false

    const response = await api.post('/auth/resend-verification', { email: email.value })

    if (response.success) {
      success.value = true
    }
  } catch (err) {
    error.value = err.message || err.error || 'Có lỗi xảy ra. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.auth-container-small {
  max-width: 450px;
  width: 100%;
}

.resend-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  padding: 40px;
}

.card-header {
  margin-bottom: 30px;
  text-align: center;
}

.card-header h2 {
  font-size: 26px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 800;
}

.card-header p {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.alert {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  font-weight: 500;
}

.alert-success {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.resend-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.form-group input {
  padding: 14px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.btn-submit {
  padding: 14px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-submit:hover {
  background: #1e3a8a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.form-footer {
  text-align: center;
  margin-top: 25px;
}

.link {
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
  font-size: 14px;
}

.link:hover {
  text-decoration: underline;
}
</style>
