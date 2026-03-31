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
              <div class="feature-title">Bắt đầu hành trình học tập an toàn</div>
              <p>Hệ thống giảm sát thông minh giúp bảo vệ tính minh bạch và công bằng cho mọi kỳ thi của bạn.</p>
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
          <div v-if="success" class="alert alert-success">
            <div class="alert-icon">✓</div>
            <div>
              <div class="alert-title">Đăng ký thành công!</div>
              <div class="alert-text">Chúng tôi đã gửi email xác thực đến <strong>{{ form.email }}</strong>. Vui lòng kiểm tra hộp thư và nhấp vào link để kích hoạt tài khoản.</div>
            </div>
          </div>

          <div class="form-header">
            <h2>Tạo tài khoản mới</h2>
            <p>Tham gia cùng hàng ngàn sinh viên tại Hệ Thống Thi Trực Tuyến</p>
          </div>

          <div v-if="error" class="alert alert-error">
            {{ error }}
          </div>

          <form @submit.prevent="handleRegister" class="auth-form">
            <div class="form-row">
              <div class="form-group">
                <label>Họ và tên</label>
                <input 
                  type="text" 
                  v-model="form.fullName" 
                  placeholder="Nguyễn Văn A" 
                  required
                />
              </div>
              <div class="form-group">
                <label>Email</label>
                <input 
                  type="email" 
                  v-model="form.email" 
                  placeholder="student@example.com" 
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label>Mật khẩu</label>
              <div class="password-input">
                <input 
                  :type="showPassword ? 'text' : 'password'"
                  v-model="form.password" 
                  placeholder="••••••••" 
                  required
                  minlength="6"
                />
                <button type="button" class="toggle-password" @click="showPassword = !showPassword">
                  {{ showPassword ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
            </div>

            <div class="form-group">
              <label>Xác nhận mật khẩu</label>
              <div class="password-input">
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'"
                  v-model="form.confirmPassword" 
                  placeholder="••••••••" 
                  required
                />
                <button type="button" class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
                  {{ showConfirmPassword ? 'Ẩn' : 'Hiện' }}
                </button>
              </div>
            </div>

            <div class="checkbox-group">
              <input type="checkbox" id="terms" v-model="acceptTerms" required />
              <label for="terms">
                Tôi đồng ý với <a href="#" class="link">Điều khoản dịch vụ</a> và <a href="#" class="link">Chính sách bảo mật</a> của Hệ Thống Thi Trực Tuyến.
              </label>
            </div>

            <button type="submit" class="btn-submit" :disabled="loading || !acceptTerms">
              {{ loading ? 'Đang đăng ký...' : 'Đăng ký' }}
            </button>
          </form>

          <p class="form-footer">
            Bạn đã có tài khoản? 
            <router-link to="/login" class="link">Đăng nhập ngay →</router-link>
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
  fullName: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')
const success = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const acceptTerms = ref(false)

const handleRegister = async () => {
  try {
    loading.value = true
    error.value = ''
    success.value = false
    
    if (form.value.password !== form.value.confirmPassword) {
      error.value = 'Mật khẩu xác nhận không khớp'
      return
    }
    
    const response = await authStore.register({
      fullName: form.value.fullName,
      email: form.value.email,
      password: form.value.password
    })
    
    if (response.success) {
      success.value = true
      setTimeout(() => {
        router.push('/login')
      }, 3000)
    }
  } catch (err) {
    error.value = err.message || 'Đăng ký thất bại'
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
  grid-template-columns: 450px 600px;
  max-width: 1050px;
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
  padding: 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
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

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
  font-weight: 500;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
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

.checkbox-group {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.checkbox-group input[type="checkbox"] {
  margin-top: 3px;
  width: 18px;
  height: 18px;
  cursor: pointer;
  flex-shrink: 0;
}

.checkbox-group label {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
  cursor: pointer;
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

@media (max-width: 1068px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 550px;
  }
  .auth-left {
    display: none;
  }
  .auth-right {
    padding: 40px 30px;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
