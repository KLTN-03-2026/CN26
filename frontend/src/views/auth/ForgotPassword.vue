<template>
  <div class="auth-layout">
    <div class="auth-container">
      <BrandPanel />
      <div class="auth-right">
        <div class="form-container">
          <div class="form-header">
            <h2>Quên Mật Khẩu</h2>
            <p>Nhập email của bạn để nhận link đặt lại mật khẩu</p>
          </div>

          <div v-if="error" class="alert alert-error">{{ error }}</div>

          <div v-if="success" class="alert alert-success">
            <div class="alert-icon">✓</div>
            <div>
              <div class="alert-title">Email đã được gửi!</div>
              <div class="alert-text">Vui lòng kiểm tra hộp thư của bạn và click vào link để đặt lại mật khẩu. Link sẽ hết hạn sau 30 phút.</div>
            </div>
          </div>

          <form v-if="!success" @submit.prevent="handleSubmit" class="auth-form">
            <div class="form-group">
              <label>Email</label>
              <input
                v-model="email"
                type="email"
                placeholder="example@gmail.com"
                required
                :disabled="loading"
              />
            </div>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? 'Đang gửi...' : 'Gửi Email' }}
            </button>
          </form>

          <p class="form-footer">
            <router-link to="/login" class="link">← Quay lại đăng nhập</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import authService from '../../services/authService'
import BrandPanel from '../../components/auth/BrandPanel.vue'

const email = ref('')
const loading = ref(false)
const error = ref('')
const success = ref(false)

const handleSubmit = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await authService.forgotPassword(email.value)
    if (response.success) success.value = true
  } catch (err) {
    error.value = err.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại.'
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

.auth-container {
  display: grid;
  grid-template-columns: 450px 550px;
  max-width: 1000px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}

.auth-right {
  padding: 50px 50px;
  display: flex;
  align-items: center;
}

.form-container {
  width: 100%;
}

.form-header {
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 28px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.form-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

.alert {
  padding: 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
  font-weight: 500;
}

.alert-success {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
  display: flex;
  gap: 12px;
}

.alert-icon {
  width: 24px;
  height: 24px;
  background: #10b981;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.alert-title {
  font-weight: 700;
  margin-bottom: 4px;
}

.alert-text {
  font-size: 13px;
  opacity: 0.9;
}

.auth-form {
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
  font-family: inherit;
}

.form-group input:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.form-group input:disabled {
  background: #f3f4f6;
  cursor: not-allowed;
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
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
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
  font-size: 14px;
  color: #6b7280;
}

.link {
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}

@media (max-width: 968px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 500px;
  }
  .auth-right {
    padding: 40px 30px;
  }
}
</style>
