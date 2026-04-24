<template>
  <div class="auth-layout">
    <div class="auth-container">
      <BrandPanel />
      <div class="auth-right">
        <RegisterForm 
          :loading="loading"
          :error="error"
          :success="success"
          :email="form.email"
          @submit="handleRegister"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import BrandPanel from '../../components/auth/BrandPanel.vue'
import RegisterForm from '../../components/auth/RegisterForm.vue'

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

const handleRegister = async (formData) => {
  try {
    loading.value = true
    error.value = ''
    success.value = false
    
    // Update form for email display in success message
    form.value = formData
    
    if (formData.password !== formData.confirmPassword) {
      error.value = 'Mật khẩu xác nhận không khớp'
      return
    }
    
    const response = await authStore.register({
      fullName: formData.fullName,
      email: formData.email,
      password: formData.password
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

.auth-right {
  padding: 50px 50px;
  display: flex;
  align-items: center;
}

@media (max-width: 1068px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 550px;
  }
  .auth-right {
    padding: 40px 30px;
  }
}
</style>
