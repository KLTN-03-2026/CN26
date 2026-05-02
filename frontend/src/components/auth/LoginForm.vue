<template>
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

    <form @submit.prevent="handleSubmit" class="auth-form">
      <div class="form-group">
        <label>Email</label>
        <input 
          type="email" 
          v-model="formData.email" 
          placeholder="example@gmail.com" 
          required
        />
      </div>

      <div class="form-group">
        <label>Mật khẩu</label>
        <div class="password-input">
          <input 
            :type="showPassword ? 'text' : 'password'"
            v-model="formData.password" 
            placeholder="••••••••" 
            required
          />
          <button type="button" class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? 'Ẩn' : 'Hiện' }}
          </button>
        </div>
        <router-link to="/forgot-password" class="forgot-link">Quên mật khẩu?</router-link>
      </div>

      <button type="submit" class="btn-submit" :disabled="loading">
        {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
      </button>
    </form>

    <p class="form-footer">
      Chưa có tài khoản? 
      <router-link to="/register" class="link">Đăng ký tài khoản</router-link>
      <br>
      <router-link to="/resend-verification" class="link-small">Chưa nhận được email xác thực?</router-link>
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
  needsActivation: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit'])

const formData = ref({
  email: '',
  password: ''
})

const showPassword = ref(false)

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

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
  display: block;
  margin-bottom: 8px;
}

.forgot-link {
  font-size: 13px;
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
  display: inline-block;
  margin-top: 8px;
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
</style>
