<template>
  <div class="auth-layout">
    <div class="auth-container">
      <BrandPanel />
      <div class="auth-right">
        <div class="form-container">
          <div class="form-header">
            <h2>Đặt Lại Mật Khẩu</h2>
            <p>Nhập mật khẩu mới của bạn</p>
          </div>

          <div v-if="error" class="alert alert-error">{{ error }}</div>

          <div v-if="success" class="alert alert-success">
            <div class="alert-icon">✓</div>
            <div>
              <div class="alert-title">Đặt lại mật khẩu thành công!</div>
              <div class="alert-text">Bạn có thể đăng nhập với mật khẩu mới.</div>
            </div>
          </div>

          <div v-if="invalidToken" class="invalid-token">
            <div class="error-icon">⚠️</div>
            <h3>Link không hợp lệ hoặc đã hết hạn</h3>
            <p>Link đặt lại mật khẩu có thể đã hết hạn hoặc đã được sử dụng.</p>
            <router-link to="/forgot-password" class="btn-secondary">Gửi lại email</router-link>
          </div>

          <form v-if="!success && !invalidToken" @submit.prevent="handleSubmit" class="auth-form">
            <div class="form-group">
              <label>Mật khẩu mới</label>
              <div class="password-input">
                <input
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  required
                  minlength="6"
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="toggle-password"
                  @click="showPassword = !showPassword"
                  :disabled="loading"
                >
                  {{ showPassword ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
              <small class="hint">Tối thiểu 6 ký tự</small>
            </div>

            <div class="form-group">
              <label>Xác nhận mật khẩu</label>
              <div class="password-input">
                <input
                  v-model="confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  required
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="toggle-password"
                  @click="showConfirmPassword = !showConfirmPassword"
                  :disabled="loading"
                >
                  {{ showConfirmPassword ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
            </div>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? 'Đang xử lý...' : 'Đặt Lại Mật Khẩu' }}
            </button>
          </form>

          <p class="form-footer">
            <router-link v-if="success" to="/login" class="link">Đăng nhập ngay →</router-link>
            <router-link v-else-if="!invalidToken" to="/login" class="link">← Quay lại đăng nhập</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import authService from '../../services/authService'
import BrandPanel from '../../components/auth/BrandPanel.vue'

const route = useRoute()
const router = useRouter()

const token = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref(false)
const invalidToken = ref(false)

onMounted(() => {
  token.value = route.query.token
  if (!token.value) invalidToken.value = true
})

const handleSubmit = async () => {
  try {
    error.value = ''

    if (password.value !== confirmPassword.value) {
      error.value = 'Mật khẩu xác nhận không khớp'
      return
    }

    if (password.value.length < 6) {
      error.value = 'Mật khẩu phải có ít nhất 6 ký tự'
      return
    }

    loading.value = true
    const response = await authService.resetPassword(token.value, password.value)

    if (response.success) {
      success.value = true
      setTimeout(() => router.push('/login'), 2000)
    }
  } catch (err) {
    const errorMsg = err.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại.'
    
    if (errorMsg.includes('Token') || errorMsg.includes('hết hạn') || errorMsg.includes('không hợp lệ')) {
      invalidToken.value = true
    } else {
      error.value = errorMsg
    }
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

.password-input {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input input {
  flex: 1;
  padding-right: 60px;
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 10px;
}

.toggle-password:hover:not(:disabled) {
  color: #1e40af;
}

.toggle-password:disabled {
  cursor: not-allowed;
}

.hint {
  display: block;
  font-size: 12px;
  color: #6b7280;
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

.btn-secondary {
  display: inline-block;
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #1e3a8a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.invalid-token {
  text-align: center;
  padding: 20px 0;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.invalid-token h3 {
  font-size: 20px;
  color: #991b1b;
  margin: 0 0 12px 0;
}

.invalid-token p {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 24px 0;
  line-height: 1.5;
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
