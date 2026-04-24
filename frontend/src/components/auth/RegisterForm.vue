<template>
  <div class="form-container">
    <div v-if="success" class="alert alert-success">
      <div class="alert-icon">✓</div>
      <div>
        <div class="alert-title">Đăng ký thành công!</div>
        <div class="alert-text">Chúng tôi đã gửi email xác thực đến <strong>{{ email }}</strong>. Vui lòng kiểm tra hộp thư và nhấp vào link để kích hoạt tài khoản.</div>
      </div>
    </div>

    <div class="form-header">
      <h2>Tạo tài khoản mới</h2>
      <p>Tham gia cùng hàng ngàn sinh viên tại Hệ Thống Thi Trực Tuyến</p>
    </div>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <form @submit.prevent="handleSubmit" class="auth-form">
      <div class="form-row">
        <div class="form-group">
          <label>Họ và tên</label>
          <input 
            type="text" 
            v-model="formData.fullName" 
            placeholder="Nguyễn Văn A" 
            required
          />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input 
            type="email" 
            v-model="formData.email" 
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
            v-model="formData.password" 
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
            v-model="formData.confirmPassword" 
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
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  success: {
    type: Boolean,
    default: false
  },
  email: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['submit'])

const formData = ref({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const acceptTerms = ref(false)

const handleSubmit = () => {
  emit('submit', { ...formData.value })
}
</script>

<style scoped>
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
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
