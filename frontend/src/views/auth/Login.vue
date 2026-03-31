<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-left">
        <div class="brand-section">
          <div class="brand-logo">
            <img src="/logo/logo.png" alt="Logo" class="logo-image" />
          </div>
          <h1>Hệ Thống Thi Trực Tuyến</h1>
          <p class="brand-tagline">Nền tảng thi trắc nghiệm hiện đại</p>
          <div class="features">
            <div class="feature-item">
              <div class="feature-title">Thời gian</div>
              <p>Giảm sát thời gian thực chính xác tuyệt đối.</p>
            </div>
            <div class="feature-item">
              <div class="feature-title">Bảo mật</div>
              <p>Công nghệ định danh sinh trắc học tiên tiến.</p>
            </div>
          </div>
          <div class="brand-footer">
            <span>PRECISION</span>
            <span>SECURITY</span>
            <span>INTEGRITY</span>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="form-container">
          <div class="form-header">
            <h2>Chào mừng trở lại</h2>
            <p>Vui lòng nhập thông tin để truy cập hệ thống phòng thi.</p>
          </div>

          <div v-if="error" class="alert alert-error">
            {{ error }}
          </div>

          <div v-if="needsActivation" class="alert alert-warning">
            <strong>Tài khoản chưa được kích hoạt!</strong>
            <p>Vui lòng kiểm tra email để xác thực tài khoản trước khi đăng nhập.</p>
            <router-link to="/resend-verification" class="alert-link">Gửi lại email xác thực</router-link>
          </div>

          <form @submit.prevent="handleLogin" class="auth-form">
            <div class="form-group">
              <label>Email</label>
              <input 
                type="email" 
                v-model="form.email" 
                placeholder="example@university.edu" 
                required
              />
            </div>

            <div class="form-group">
              <div class="label-row">
                <label>Mật khẩu</label>
                <a href="#" class="forgot-link">Quên mật khẩu?</a>
              </div>
              <div class="password-input">
                <input 
                  :type="showPassword ? 'text' : 'password'"
                  v-model="form.password" 
                  placeholder="••••••••" 
                  required
                />
                <button type="button" class="toggle-password" @click="showPassword = !showPassword">
                  {{ showPassword ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
            </div>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
            </button>
          </form>

          <div class="divider">
            <span>hoặc</span>
          </div>

          <button class="btn-google">
            Google
          </button>

          <p class="form-footer">
            Chưa có tài khoản? 
            <router-link to="/register" class="link">Đăng ký tài khoản</router-link>
            <br>
            <router-link to="/resend-verification" class="link-small">Chưa nhận được email xác thực?</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  email: '',
  password: ''
})

const loading = ref(false)
const error = ref('')
const showPassword = ref(false)
const needsActivation = ref(false)

const handleLogin = async () => {
  try {
    loading.value = true
    error.value = ''
    needsActivation.value = false
    
    const response = await authStore.login(form.value)
    
    if (response.success) {
      const role = authStore.user.role
      router.push(`/${role}`)
    }
  } catch (err) {
    // Backend returns error in err.message or err.error format
    const errorMessage = err.error || err.message || 'Đăng nhập thất bại'
    
    // Check if error is about account activation
    if (errorMessage.includes('chưa được kích hoạt') || errorMessage.includes('inactive')) {
      needsActivation.value = true
      error.value = ''
    } else {
      error.value = errorMessage
      needsActivation.value = false
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

.auth-left {
  background: linear-gradient(180deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand-section {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.brand-logo {
  margin-bottom: 10px;
}

.logo-image {
  height: 180px;
  width: auto;
  object-fit: contain;
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
  backdrop-filter: blur(10px);
}

.brand-section h1 {
  font-size: 28px;
  font-weight: 800;
  margin: 0;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 15px;
  opacity: 0.9;
  margin: 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feature-title {
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.feature-item p {
  font-size: 14px;
  opacity: 0.85;
  line-height: 1.6;
  margin: 0;
}

.brand-footer {
  display: flex;
  gap: 15px;
  font-size: 11px;
  font-weight: 700;
  opacity: 0.7;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-top: 30px;
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
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  font-weight: 500;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.alert-warning {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
}

.alert-warning strong {
  display: block;
  margin-bottom: 6px;
  font-size: 15px;
}

.alert-warning p {
  margin: 0 0 8px 0;
  font-size: 13px;
  line-height: 1.5;
}

.alert-link {
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
  font-size: 13px;
}

.alert-link:hover {
  text-decoration: underline;
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

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.forgot-link {
  font-size: 13px;
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
}

.forgot-link:hover {
  text-decoration: underline;
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

.toggle-password:hover {
  color: #1e40af;
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

.divider {
  text-align: center;
  margin: 25px 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 1px;
  background: #e5e7eb;
}

.divider span {
  background: white;
  padding: 0 15px;
  font-size: 13px;
  color: #9ca3af;
  position: relative;
  font-weight: 500;
}

.btn-google {
  width: 100%;
  padding: 14px;
  background: white;
  color: #374151;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-google:hover {
  background: #f9fafb;
  border-color: #d1d5db;
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

.link-small {
  color: #6b7280;
  text-decoration: none;
  font-weight: 500;
  font-size: 13px;
  display: inline-block;
  margin-top: 8px;
}

.link-small:hover {
  color: #1e40af;
  text-decoration: underline;
}

@media (max-width: 968px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 500px;
  }
  .auth-left {
    display: none;
  }
  .auth-right {
    padding: 40px 30px;
  }
}
</style>
