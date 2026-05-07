<template>
  <component :is="layoutComponent" :activeMenu="activeMenu">
    <header class="top-header">
      <div class="header-left">
        <h1>Cài Đặt Tài Khoản</h1>
      </div>
    </header>

    <div class="profile-container">
      <div class="profile-main">
        <div class="profile-card">
          <h2>Thông Tin Cá Nhân</h2>
          <div v-if="successMessage" class="alert alert-success">
            {{ successMessage }}
          </div>
          <div v-if="errorMessage" class="alert alert-error">
            {{ errorMessage }}
          </div>
          <form @submit.prevent="updateProfile">
            <div class="form-row">
              <div class="form-group">
                <label>Họ và tên</label>
                <input v-model="profileForm.fullName" required>
              </div>
              <div class="form-group">
                <label>Email</label>
                <input v-model="profileForm.email" type="email" disabled>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Số điện thoại</label>
                <input 
                  v-model="profileForm.phone" 
                  type="tel"
                  pattern="[0-9]{10}"
                  maxlength="10"
                  @input="validatePhone"
                >
                <small v-if="phoneError" class="error-text">{{ phoneError }}</small>
              </div>
              <div class="form-group">
                <label>Ngày sinh</label>
                <input v-model="profileForm.dateOfBirth" type="date">
              </div>
            </div>
            <div class="form-group">
              <label>Địa chỉ</label>
              <textarea v-model="profileForm.address" rows="3"></textarea>
            </div>
            <button type="submit" class="btn-save" :disabled="updating">
              {{ updating ? 'Đang cập nhật...' : 'Cập nhật thông tin' }}
            </button>
          </form>
        </div>

        <div class="profile-card">
          <h2>Đổi Mật Khẩu</h2>
          <form @submit.prevent="changePassword">
            <div class="form-group">
              <label>Mật khẩu hiện tại</label>
              <input v-model="passwordForm.currentPassword" type="password" required>
            </div>
            <div class="form-group">
              <label>Mật khẩu mới</label>
              <input v-model="passwordForm.newPassword" type="password" required minlength="6">
            </div>
            <div class="form-group">
              <label>Xác nhận mật khẩu mới</label>
              <input v-model="passwordForm.confirmPassword" type="password" required>
            </div>
            <button type="submit" class="btn-save" :disabled="changingPassword">
              {{ changingPassword ? 'Đang đổi...' : 'Đổi mật khẩu' }}
            </button>
          </form>
        </div>
      </div>

      <div class="profile-sidebar">
        <div class="user-card">
          <div class="user-avatar">
            {{ authStore.user?.fullName?.charAt(0) }}
          </div>
          <h3>{{ authStore.user?.fullName }}</h3>
          <p>{{ authStore.user?.email }}</p>
          <span class="role-badge">{{ roleName }}</span>
        </div>

        <div class="tips-card">
          <h3>Lưu ý</h3>
          <ul>
            <li>Email không thể thay đổi</li>
            <li>Mật khẩu phải có ít nhất 6 ký tự</li>
            <li>Cập nhật thông tin để dễ dàng liên hệ</li>
          </ul>
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/authStore'
import { useAsyncAction } from '../../composables/useAsyncAction'
import userService from '../../services/userService'

const props = defineProps({
  layoutComponent: {
    type: Object,
    required: true
  },
  roleName: {
    type: String,
    required: true
  },
  activeMenu: {
    type: String,
    default: 'profile'
  }
})

const authStore = useAuthStore()

const profileForm = ref({
  fullName: '',
  email: '',
  phone: '',
  dateOfBirth: '',
  address: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const { loading: updating, execute: executeUpdate } = useAsyncAction()
const { loading: changingPassword, execute: executeChangePassword } = useAsyncAction()
const successMessage = ref('')
const errorMessage = ref('')
const phoneError = ref('')

onMounted(() => {
  loadProfile()
})

const loadProfile = async () => {
  try {
    const response = await userService.getProfile()
    if (response.success) {
      profileForm.value = {
        fullName: response.data.fullName || '',
        email: response.data.email || '',
        phone: response.data.phone || '',
        dateOfBirth: response.data.dateOfBirth || '',
        address: response.data.address || ''
      }
    }
  } catch (error) {
    console.error('Error loading profile:', error)
  }
}

const validatePhone = (event) => {
  const value = event.target.value
  phoneError.value = ''
  
  // Remove non-numeric characters
  const numericValue = value.replace(/\D/g, '')
  profileForm.value.phone = numericValue
  
  // Validate
  if (numericValue && numericValue.length > 0) {
    if (numericValue.length !== 10) {
      phoneError.value = 'Số điện thoại phải có đúng 10 số'
    } else if (!numericValue.startsWith('0')) {
      phoneError.value = 'Số điện thoại phải bắt đầu bằng số 0'
    }
  }
}

const updateProfile = async () => {
  successMessage.value = ''
  errorMessage.value = ''
  
  // Validate phone before submit
  if (profileForm.value.phone) {
    if (profileForm.value.phone.length !== 10) {
      errorMessage.value = 'Số điện thoại phải có đúng 10 số'
      return
    }
    if (!profileForm.value.phone.startsWith('0')) {
      errorMessage.value = 'Số điện thoại phải bắt đầu bằng số 0'
      return
    }
  }
  
  try {
    await executeUpdate(async () => {
      const response = await userService.updateProfile(profileForm.value)
      if (response.success) {
        successMessage.value = 'Cập nhật thông tin thành công!'
        setTimeout(() => successMessage.value = '', 3000)
      }
    })
  } catch (error) {
    errorMessage.value = error.message || error.error || 'Có lỗi khi cập nhật thông tin'
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp'
    return
  }

  successMessage.value = ''
  errorMessage.value = ''
  
  try {
    await executeChangePassword(async () => {
      const response = await userService.changePassword({
        currentPassword: passwordForm.value.currentPassword,
        newPassword: passwordForm.value.newPassword
      })
      
      if (response.success) {
        successMessage.value = 'Đổi mật khẩu thành công!'
        passwordForm.value = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        setTimeout(() => successMessage.value = '', 3000)
      }
    })
  } catch (error) {
    errorMessage.value = error.message || error.error || 'Có lỗi khi đổi mật khẩu'
  }
}
</script>

<style scoped>
.error-text {
  color: #dc2626;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}
</style>
