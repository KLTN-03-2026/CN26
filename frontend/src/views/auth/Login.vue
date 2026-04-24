<template>
  <div class="auth-layout">
    <div class="auth-container">
      <BrandPanel />
      <div class="auth-right">
        <LoginForm 
          :loading="loading"
          :error="error"
          :needs-activation="needsActivation"
          @submit="handleLogin"
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
import LoginForm from '../../components/auth/LoginForm.vue'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref('')
const needsActivation = ref(false)

const handleLogin = async (formData) => {
  try {
    loading.value = true
    error.value = ''
    needsActivation.value = false
    
    const response = await authStore.login(formData)
    
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

.auth-right {
  padding: 50px 50px;
  display: flex;
  align-items: center;
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
