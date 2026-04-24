<template>
  <TeacherLayout activeMenu="profile">
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
                <input v-model="profileForm.phone" type="tel">
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
          <span class="role-badge">Giáo viên</span>
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
  </TeacherLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/authStore'
import userService from '../../services/userService'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'

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

const updating = ref(false)
const changingPassword = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

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

const updateProfile = async () => {
  try {
    updating.value = true
    successMessage.value = ''
    errorMessage.value = ''
    
    const response = await userService.updateProfile(profileForm.value)
    if (response.success) {
      successMessage.value = 'Cập nhật thông tin thành công!'
      setTimeout(() => successMessage.value = '', 3000)
    }
  } catch (error) {
    errorMessage.value = error.message || error.error || 'Có lỗi khi cập nhật thông tin'
  } finally {
    updating.value = false
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp'
    return
  }

  try {
    changingPassword.value = true
    successMessage.value = ''
    errorMessage.value = ''
    
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
  } catch (error) {
    errorMessage.value = error.message || error.error || 'Có lỗi khi đổi mật khẩu'
  } finally {
    changingPassword.value = false
  }
}
</script>

<style scoped>
.profile-container {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 25px;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.profile-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.profile-card h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.btn-save {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-save:hover:not(:disabled) {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.btn-save:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.profile-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
}

.user-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: white;
  font-weight: 700;
  margin: 0 auto 20px;
}

.user-card h3 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.user-card p {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 15px 0;
}

.role-badge {
  display: inline-block;
  padding: 6px 16px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.tips-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.tips-card h3 {
  font-size: 16px;
  color: #1f2937;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.tips-card ul {
  margin: 0;
  padding-left: 20px;
  color: #6b7280;
}

.tips-card li {
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
  .profile-sidebar {
    order: -1;
  }
}
</style>
